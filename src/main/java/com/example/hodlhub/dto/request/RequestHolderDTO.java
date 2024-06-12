package com.example.hodlhub.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class RequestHolderDTO {
    @NotEmpty(message = "Name is missing!")
    @Size(min = 2, max = 100, message = "Invalid Name!")
    private String name;

    @NotEmpty(message = "Email is missing!")
    @Email(message = "Email should be valid!")
    private String email;

    @NotEmpty(message = "Password is missing!")
    @Size(min = 6, message = "Password should be longer than 6 characters!")
    private String password;

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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
