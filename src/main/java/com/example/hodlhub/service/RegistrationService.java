package com.example.hodlhub.service;

import com.example.hodlhub.model.EmailVerification;
import com.example.hodlhub.model.Holder;
import com.example.hodlhub.repository.EmailVerificationRepository;
import com.example.hodlhub.repository.HolderRepository;
import com.example.hodlhub.util.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class RegistrationService {
  private final HolderRepository holderRepository;
  private final PasswordEncoder passwordEncoder;
  private final EmailVerificationRepository emailVerificationRepository;
  private final EmailService emailService;

  @Autowired
  public RegistrationService(
      HolderRepository holderRepository,
      PasswordEncoder passwordEncoder,
      EmailVerificationRepository emailVerificationRepository,
      EmailService emailService) {
    this.holderRepository = holderRepository;
    this.passwordEncoder = passwordEncoder;
    this.emailVerificationRepository = emailVerificationRepository;
    this.emailService = emailService;
  }

  @Transactional
  public void registerHolder(Holder holder) {
    String encodedPassword = passwordEncoder.encode(holder.getPassword());

    String verificationCode = generateVerificationCode();

    EmailVerification verification = new EmailVerification();
    verification.setEmail(holder.getEmail());
    verification.setName(holder.getName());
    verification.setEncodedPassword(encodedPassword);
    verification.setVerificationCode(verificationCode);
    verification.setCreatedAt(LocalDateTime.now());
    verification.setExpiresAt(LocalDateTime.now().plusHours(24));

    emailVerificationRepository.save(verification);

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
