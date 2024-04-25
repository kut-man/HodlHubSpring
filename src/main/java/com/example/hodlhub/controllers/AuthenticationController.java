package com.example.hodlhub.controllers;

import com.example.hodlhub.models.Holder;
import com.example.hodlhub.services.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final RegistrationService registrationService;

    @Autowired
    public AuthenticationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    public ResponseEntity<HttpStatus> register(@RequestBody @Valid Holder holder, BindingResult bindingResult) {
        System.out.println("Hi my");
        registrationService.save(holder);
        System.out.println("Hi");
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
