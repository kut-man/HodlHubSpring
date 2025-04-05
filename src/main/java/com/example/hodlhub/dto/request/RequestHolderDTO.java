package com.example.hodlhub.dto.request;

import jakarta.validation.constraints.*;

public class RequestHolderDTO {
  @NotEmpty(message = "Name is missing!")
  @Size(min = 2, max = 100, message = "Invalid Name!")
  private String name;

  @NotEmpty(message = "Email is missing!")
  @Email(message = "Email should be valid!")
  private String email;

  @NotEmpty(message = "Password is missing!")
  @Size(min = 8, message = "Password should be at least 8 characters!")
  @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one number!")
  private String password;

  @NotBlank(message = "reCAPTCHA verification is required!")
  private String recaptchaToken;

  private String avatar;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getRecaptchaToken() {
    return recaptchaToken;
  }

  public void setRecaptchaToken(String recaptchaToken) {
    this.recaptchaToken = recaptchaToken;
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }
}
