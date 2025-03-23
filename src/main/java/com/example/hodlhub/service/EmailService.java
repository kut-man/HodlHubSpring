package com.example.hodlhub.service;

import com.example.hodlhub.util.exceptions.EmailSendingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

  private final JavaMailSender mailSender;

  @Value("${spring.mail.username}")
  private String fromEmail;

  @Value("${app.frontend.url}")
  private String frontendUrl;

  @Autowired
  public EmailService(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  @Async
  public void sendVerificationEmail(String toEmail, String verificationCode) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true);

      helper.setFrom(fromEmail);
      helper.setTo(toEmail);
      helper.setSubject("HodlHub Email Verification");

      String verificationLink =
          frontendUrl + "/verify-email?code=" + verificationCode + "&email=" + toEmail;

      String emailContent =
          "<h1>Welcome to HodlHub!</h1>"
              + "<p>Thank you for registering. Please verify your email address by clicking the link below:</p>"
              + "<p><a href='"
              + verificationLink
              + "'>Verify Email</a></p>"
              + "<p>Or enter this verification code: <strong>"
              + verificationCode
              + "</strong></p>"
              + "<p>This code will expire in 24 hours.</p>"
              + "<p>If you did not create an account, please ignore this email.</p>";

      helper.setText(emailContent, true);

      mailSender.send(message);
    } catch (MessagingException e) {
      throw new EmailSendingException("Failed to send verification email to: " + toEmail, "/auth");
    } catch (Exception e) {
      throw new EmailSendingException(
          "Unexpected error when sending email to: " + toEmail, "/auth");
    }
  }
}
