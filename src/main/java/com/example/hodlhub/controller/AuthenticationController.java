package com.example.hodlhub.controller;

import com.example.hodlhub.dto.request.RequestHolderDTO;
import com.example.hodlhub.model.Holder;
import com.example.hodlhub.service.RegistrationService;
import com.example.hodlhub.util.ApiResponse;
import com.example.hodlhub.util.validators.HolderValidator;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

  private final RegistrationService registrationService;
  private final ModelMapper modelMapper;
  private final HolderValidator holderValidator;

  @Autowired
  public AuthenticationController(
      RegistrationService registrationService,
      ModelMapper modelMapper,
      HolderValidator holderValidator) {
    this.registrationService = registrationService;
    this.modelMapper = modelMapper;
    this.holderValidator = holderValidator;
  }

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<Void>> register(
      @RequestBody @Valid RequestHolderDTO requestHolderDTO, BindingResult bindingResult) {
    Holder holder = modelMapper.map(requestHolderDTO, Holder.class);
    holderValidator.validate(holder, bindingResult);
    if (bindingResult.hasErrors()) {
      HttpStatus status = HttpStatus.BAD_REQUEST;
      ApiResponse<Void> response = new ApiResponse<>(status, bindingResult, "/portfolio");
      return new ResponseEntity<>(response, status);
    }
    registrationService.save(holder);

    HttpStatus status = HttpStatus.CREATED;
    ApiResponse<Void> response =
        new ApiResponse<>(status, "User created successfully", "/auth/register");

    return new ResponseEntity<>(response, status);
  }
}
