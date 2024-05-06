package com.example.hodlhub.controllers;

import com.example.hodlhub.dto.PortfolioDTO;
import com.example.hodlhub.models.Holder;
import com.example.hodlhub.models.Portfolio;
import com.example.hodlhub.security.HolderDetails;
import com.example.hodlhub.services.HolderService;
import com.example.hodlhub.services.PortfolioService;
import com.example.hodlhub.utils.ErrorMessageBuilder;
import com.example.hodlhub.utils.ErrorResponse;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final ModelMapper modelMapper;
    private final HolderService holderService;

    public PortfolioController(PortfolioService portfolioService, ModelMapper modelMapper, HolderService holderService) {
        this.portfolioService = portfolioService;
        this.modelMapper = modelMapper;
        this.holderService = holderService;
    }

    @PostMapping
    public ResponseEntity<HttpStatus> register(@RequestBody @Valid PortfolioDTO portfolioDTO,
                                               BindingResult bindingResult,
                                               @AuthenticationPrincipal HolderDetails holderDetails) {
        if (bindingResult.hasErrors()) {
            throw new RuntimeException(ErrorMessageBuilder.buildErrorMessage(bindingResult));
        }
        Holder holder = holderService.getHolder(holderDetails.getUsername());
        Portfolio portfolio = modelMapper.map(portfolioDTO, Portfolio.class);
        portfolio.setHolder(holder);
        portfolioService.save(portfolio);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(RuntimeException e) {
        ErrorResponse response = new ErrorResponse(e.getMessage(), new Date());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
