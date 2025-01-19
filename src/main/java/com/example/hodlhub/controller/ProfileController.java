package com.example.hodlhub.controller;

import com.example.hodlhub.dto.response.ResponseHolderDTO;
import com.example.hodlhub.model.Holder;
import com.example.hodlhub.security.HolderDetails;
import com.example.hodlhub.service.HolderService;
import com.example.hodlhub.util.ApiResponse;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
  public ResponseEntity<ApiResponse<ResponseHolderDTO>> getHolder(
      @AuthenticationPrincipal HolderDetails holderDetails) {
    Holder holder = holderService.getHolder(holderDetails.getUsername());
    ResponseHolderDTO responseHolderDTO = modelMapper.map(holder, ResponseHolderDTO.class);

    HttpStatus status = HttpStatus.OK;
    ApiResponse<ResponseHolderDTO> response = new ApiResponse<>(status, responseHolderDTO, "/user");

    return new ResponseEntity<>(response, status);
  }

  @PutMapping()
  public ResponseEntity<ApiResponse<Void>> editHolder(
          @RequestBody @Valid ResponseHolderDTO holderDTO, @AuthenticationPrincipal HolderDetails holderDetails) {
    Holder holder = holderService.getHolder(holderDetails.getUsername());
    holder.setName(holderDTO.getName());
    holder.setAvatar(holderDTO.getAvatar());
    holderService.editHolder(holder);

    HttpStatus status = HttpStatus.OK;
    ApiResponse<Void> response = new ApiResponse<>(status, "Profile edited successfully", "/user");

    return new ResponseEntity<>(response, status);
  }
}
