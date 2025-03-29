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
      String emailContent =
          "<!DOCTYPE html>"
              + "<html>"
              + "<head>"
              + "<style>"
              + "body { font-family: sans-serif; line-height: 1.6; color: #333; }"
              + "h1 { color: #007bff; }"
              + // Example blue color
              "strong { background-color: #f0f0f0; padding: 5px; border-radius: 3px; }"
              + "p { margin-bottom: 15px; }"
              + ".footer { margin-top: 20px; font-size: 0.8em; color: #777; }"
              + "</style>"
              + "</head>"
              + "<body>"
              + "<h1>Welcome to HodlHub!</h1>"
              + "<p>Thank you for registering. Please verify your email address by "
              + "entering this verification code: <strong>"
              + verificationCode
              + "</strong></p>"
              + "<p>This code will expire in 24 hours.</p>"
              + "<p>Once your account is verified, you'll have full access to your account features.</p>"
              + "<p>Didn’t request this email?</p>"
              + "<p>Your address may have been entered by mistake. Simply ignore this email, and nothing further will happen.</p>"
              + "<p class=\"footer\">This is an automatically generated email. Please do not reply.</p>"
              + "</body>"
              + "</html>";

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
