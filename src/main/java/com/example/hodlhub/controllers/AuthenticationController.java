package com.example.hodlhub.controllers;

import com.example.hodlhub.dto.RequestHolderDTO;
import com.example.hodlhub.models.Holder;
import com.example.hodlhub.services.RegistrationService;
import com.example.hodlhub.utils.EmailExistsException;
import com.example.hodlhub.utils.ErrorMessageBuilder;
import com.example.hodlhub.utils.ErrorResponse;
import com.example.hodlhub.utils.HolderValidator;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final RegistrationService registrationService;
    private final ModelMapper modelMapper;
    private final HolderValidator holderValidator;

    @Autowired
    public AuthenticationController(RegistrationService registrationService, ModelMapper modelMapper, HolderValidator holderValidator) {
        this.registrationService = registrationService;
        this.modelMapper = modelMapper;
        this.holderValidator = holderValidator;
    }

    @PostMapping("/register")
    public ResponseEntity<HttpStatus> register(@RequestBody @Valid RequestHolderDTO requestHolderDTO, BindingResult bindingResult) {
        Holder holder = modelMapper.map(requestHolderDTO, Holder.class);
        holderValidator.validate(holder, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new EmailExistsException(ErrorMessageBuilder.buildErrorMessage(bindingResult));
        }
        registrationService.save(holder);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(EmailExistsException e) {
        ErrorResponse response = new ErrorResponse(e.getMessage(), new Date());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
}
