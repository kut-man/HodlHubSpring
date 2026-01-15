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
      System.out.println("sendVerificationEmail is called.");
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



      System.err.println("=== MESSAGING EXCEPTION ===");
      System.err.println("Failed to send email to: " + toEmail);
      System.err.println("Error message: " + e.getMessage());
      System.err.println("Error class: " + e.getClass().getName());

      if (e.getCause() != null) {
        System.err.println("Root cause: " + e.getCause().getMessage());
        System.err.println("Root cause class: " + e.getCause().getClass().getName());
      }

      // Check for specific error types
      String errorMsg = e.getMessage();
      if (errorMsg != null) {
        if (errorMsg.contains("Authentication") || errorMsg.contains("535")) {
          System.err.println(">>> AUTHENTICATION FAILED - Check username/password/app password");
        } else if (errorMsg.contains("Connection") || errorMsg.contains("timeout")) {
          System.err.println(">>> CONNECTION FAILED - Check host/port/firewall");
        } else if (errorMsg.contains("550")) {
          System.err.println(">>> RECIPIENT REJECTED - Check recipient email address");
        } else if (errorMsg.contains("SSL") || errorMsg.contains("TLS")) {
          System.err.println(">>> SSL/TLS ERROR - Check port and security settings");
        }
      }


      System.err.println("Full stack trace:");
      e.printStackTrace();
      System.err.println("=== END MESSAGING EXCEPTION ===");

      throw new EmailSendingException("Failed to send verification email to: " + toEmail, "/auth");
    } catch (Exception e) {
      System.err.println("=== UNEXPECTED EXCEPTION ===");
      System.err.println("Unexpected error when sending email to: " + toEmail);
      System.err.println("Error type: " + e.getClass().getName());
      System.err.println("Error message: " + e.getMessage());
      System.err.println("Full stack trace:");
      e.printStackTrace();
      System.err.println("=== END UNEXPECTED EXCEPTION ===");
      throw new EmailSendingException(
          "Unexpected error when sending email to: " + toEmail, "/auth");
    }
  }
}
