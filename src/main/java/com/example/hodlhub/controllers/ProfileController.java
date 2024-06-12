package com.example.hodlhub.controllers;

import com.example.hodlhub.dto.response.ResponseHolderDTO;
import com.example.hodlhub.models.Holder;
import com.example.hodlhub.security.HolderDetails;
import com.example.hodlhub.services.HolderService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class ProfileController {

    private final HolderService holderService;
    private final ModelMapper modelMapper;

    public ProfileController(HolderService holderService, ModelMapper modelMapper) {
        this.holderService = holderService;
        this.modelMapper = modelMapper;
    }
    @GetMapping()
    public ResponseEntity<?> getHolder(@AuthenticationPrincipal HolderDetails holderDetails) {
        if (holderDetails != null) {
            Holder holder = holderService.getHolder(holderDetails.getUsername());
            ResponseHolderDTO responseHolderDTO = modelMapper.map(holder, ResponseHolderDTO.class);
            return ResponseEntity.ok(responseHolderDTO);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
    }
}
