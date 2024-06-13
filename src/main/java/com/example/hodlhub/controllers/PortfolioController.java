package com.example.hodlhub.controllers;

import com.example.hodlhub.dto.request.RequestPortfolioDTO;
import com.example.hodlhub.dto.response.ResponsePortfolioDTO;
import com.example.hodlhub.models.Holder;
import com.example.hodlhub.models.Portfolio;
import com.example.hodlhub.security.HolderDetails;
import com.example.hodlhub.services.HolderService;
import com.example.hodlhub.services.PortfolioService;
import com.example.hodlhub.utils.ApiResponse;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

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
    public ResponseEntity<ApiResponse<?>> register(@RequestBody @Valid RequestPortfolioDTO portfolioDTO,
                                      BindingResult bindingResult,
                                      @AuthenticationPrincipal HolderDetails holderDetails) {

        if (bindingResult.hasErrors()) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            ApiResponse<Void> response = new ApiResponse<>(
                    status,
                    bindingResult,
                    "/portfolio"
            );
            return new ResponseEntity<>(response, status);
        }

        Holder holder = holderService.getHolder(holderDetails.getUsername());
        Portfolio portfolio = modelMapper.map(portfolioDTO, Portfolio.class);
        portfolio.setHolder(holder);
        portfolioService.save(portfolio);

        HttpStatus status = HttpStatus.CREATED;
        ApiResponse<Void> response = new ApiResponse<>(
                status,
                "Portfolio created successfully",
                "/portfolio"
        );

        return new ResponseEntity<>(response, status);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ResponsePortfolioDTO>>> getHolderPortfolios(@AuthenticationPrincipal HolderDetails holderDetails) {
        Holder holder = holderService.getHolder(holderDetails.getUsername());

        List<Portfolio> portfolioList = portfolioService.get(holder.getEmail());
        List<ResponsePortfolioDTO> portfolioDTOList = portfolioList.stream()
                .map(portfolio -> modelMapper.map(portfolio, ResponsePortfolioDTO.class))
                .collect(Collectors.toList());


        HttpStatus status = HttpStatus.OK;
        ApiResponse<List<ResponsePortfolioDTO>> response = new ApiResponse<>(
                status,
                portfolioDTOList,
                "/portfolio"
        );

        return new ResponseEntity<>(response, status);
    }

//    @DeleteMapping("/{name}")
//    public ResponseEntity<?> removePortfolio(@PathVariable String name, @AuthenticationPrincipal HolderDetails holderDetails) {
//        Holder holder = holderService.getHolder(holderDetails.getUsername());
//        portfolioService.removePortfolioByNameAndHolder(name, holder);
//        return ResponseEntity.noContent().build();
//    }
//
//    @ExceptionHandler(RuntimeException.class)
//    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
//        HttpStatus status = HttpStatus.BAD_REQUEST;
//        ErrorResponse response = new ErrorResponse(e.getMessage(), new Date(), status);
//        return new ResponseEntity<>(response, status);
//    }
}
