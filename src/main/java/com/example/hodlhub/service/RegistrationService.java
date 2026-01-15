package com.example.hodlhub.service;

import com.example.hodlhub.model.EmailVerification;
import com.example.hodlhub.model.Holder;
import com.example.hodlhub.repository.EmailVerificationRepository;
import com.example.hodlhub.repository.HolderRepository;
import com.example.hodlhub.util.exceptions.RecaptchaVerificationException;
import com.example.hodlhub.util.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class RegistrationService {
  private final HolderRepository holderRepository;
  private final PasswordEncoder passwordEncoder;
  private final EmailVerificationRepository emailVerificationRepository;
  private final EmailService emailService;
  private final String recaptchaSecret;
  private static final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

  @Autowired
  public RegistrationService(
      HolderRepository holderRepository,
      PasswordEncoder passwordEncoder,
      EmailVerificationRepository emailVerificationRepository,
      EmailService emailService,
      @Value("${google.recaptcha.secret}") String recaptchaSecret) {
    this.holderRepository = holderRepository;
    this.passwordEncoder = passwordEncoder;
    this.emailVerificationRepository = emailVerificationRepository;
    this.emailService = emailService;
    this.recaptchaSecret = recaptchaSecret;
  }

  public boolean verifyRecaptchaToken(String token) {
    RestTemplate restTemplate = new RestTemplate();
    try {
      MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
      params.add("secret", recaptchaSecret);
      params.add("response", token);

      ResponseEntity<Map> response =
          restTemplate.postForEntity(RECAPTCHA_VERIFY_URL, params, Map.class);

      Map<String, Object> body = response.getBody();

      boolean success = Boolean.TRUE.equals(body.get("success"));

      if (!success) {
        return false;
      }

      Double score = (Double) body.get("score");
      double threshold = 0.5;

      return score != null && score >= threshold;
    } catch (Exception e) {
      return false;
    }
  }

  @Transactional
  public void registerHolder(Holder holder) {
    if (!verifyRecaptchaToken(holder.getRecaptchaToken())) {
      throw new RecaptchaVerificationException("/auth");
    }

    String encodedPassword = passwordEncoder.encode(holder.getPassword());
    String verificationCode = generateVerificationCode();
    LocalDateTime now = LocalDateTime.now();

    Optional<EmailVerification> existingVerificationOpt =
        emailVerificationRepository.findByEmail(holder.getEmail());

    if (existingVerificationOpt.isPresent()) {
      EmailVerification existingVerification = existingVerificationOpt.get();
      existingVerification.setName(holder.getName());
      existingVerification.setEncodedPassword(encodedPassword);
      existingVerification.setVerificationCode(verificationCode);
      existingVerification.setCreatedAt(now);
      existingVerification.setExpiresAt(now.plusHours(24));
      emailVerificationRepository.save(existingVerification);
    } else {
      EmailVerification verification = new EmailVerification();
      verification.setEmail(holder.getEmail());
      verification.setName(holder.getName());
      verification.setEncodedPassword(encodedPassword);
      verification.setVerificationCode(verificationCode);
      verification.setCreatedAt(now);
      verification.setExpiresAt(now.plusHours(24));
      emailVerificationRepository.save(verification);
    }

    emailService.sendVerificationEmail(holder.getEmail(), verificationCode);
  }

  private String generateVerificationCode() {
    SecureRandom random = new SecureRandom();
    int code = random.nextInt(900000) + 100000;
    return String.valueOf(code);
  }

  @Transactional
  public boolean verifyEmail(String email, String verificationCode) {
    if (holderRepository.existsByEmail(email)) {
      return true;
    }

    EmailVerification verification =
        emailVerificationRepository
            .findByEmail(email)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Verification record for email " + email + " not found", "/auth"));

    if (verification.getVerificationCode().equals(verificationCode)
        && verification.getExpiresAt().isAfter(LocalDateTime.now())) {

      Holder holder = new Holder();
      holder.setEmail(verification.getEmail());
      holder.setName(verification.getName());
      holder.setPassword(verification.getEncodedPassword());

      holderRepository.save(holder);
      emailVerificationRepository.delete(verification);

      return true;
    }

    return false;
  }
}
