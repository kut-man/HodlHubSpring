package com.example.hodlhub.controller;

import com.example.hodlhub.dto.request.RequestPortfolioDTO;
import com.example.hodlhub.dto.response.ResponsePortfolioDTO;
import com.example.hodlhub.model.Portfolio;
import com.example.hodlhub.security.HolderDetails;
import com.example.hodlhub.service.HolderService;
import com.example.hodlhub.service.PortfolioService;
import com.example.hodlhub.util.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/portfolio")
public class PortfolioController {

  private final PortfolioService portfolioService;
  private final ModelMapper modelMapper;

  public PortfolioController(
      PortfolioService portfolioService, ModelMapper modelMapper, HolderService holderService) {
    this.portfolioService = portfolioService;
    this.modelMapper = modelMapper;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<?>> register(
      @RequestBody @Valid RequestPortfolioDTO portfolioDTO,
      BindingResult bindingResult,
      @AuthenticationPrincipal HolderDetails holderDetails) {

    if (bindingResult.hasErrors()) {
      HttpStatus status = HttpStatus.BAD_REQUEST;
      ApiResponse<Void> response = new ApiResponse<>(status, bindingResult, "/portfolio");
      return new ResponseEntity<>(response, status);
    }

    Portfolio portfolio = modelMapper.map(portfolioDTO, Portfolio.class);
    portfolioService.save(portfolio, holderDetails.getUsername());

    HttpStatus status = HttpStatus.CREATED;
    ApiResponse<Void> response =
        new ApiResponse<>(status, "Portfolio created successfully", "/portfolio");

    return new ResponseEntity<>(response, status);
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<ResponsePortfolioDTO>>> getHolderPortfolios(
      @AuthenticationPrincipal HolderDetails holderDetails) {
    List<Portfolio> portfolioList = portfolioService.get(holderDetails.getUsername());
    List<ResponsePortfolioDTO> portfolioDTOList =
        portfolioList.stream()
            .map(portfolio -> modelMapper.map(portfolio, ResponsePortfolioDTO.class))
            .collect(Collectors.toList());

    HttpStatus status = HttpStatus.OK;
    ApiResponse<List<ResponsePortfolioDTO>> response =
        new ApiResponse<>(status, portfolioDTOList, "/portfolio");

    return new ResponseEntity<>(response, status);
  }

  @DeleteMapping("/{portfolioId}")
  public ResponseEntity<?> removePortfolio(
      @PathVariable int portfolioId, @AuthenticationPrincipal HolderDetails holderDetails) {
    portfolioService.removePortfolioByNameAndHolder(portfolioId, holderDetails.getUsername());
    HttpStatus status = HttpStatus.OK;
    ApiResponse<Void> response =
        new ApiResponse<>(status, "Portfolio removed successfully", "/portfolio");

    return new ResponseEntity<>(response, status);
  }

  @DeleteMapping("/{portfolioId}/{ticker}")
  public ResponseEntity<?> removeAsset(@PathVariable int portfolioId, @PathVariable String ticker) {
    portfolioService.removeAssetFromPortfolio(portfolioId, ticker);
    HttpStatus status = HttpStatus.OK;
    ApiResponse<Void> response =
        new ApiResponse<>(status, "Asset removed successfully", "/portfolio");

    return new ResponseEntity<>(response, status);
  }
  //
  //    @ExceptionHandler(RuntimeException.class)
  //    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
  //        HttpStatus status = HttpStatus.BAD_REQUEST;
  //        ErrorResponse response = new ErrorResponse(e.getMessage(), new Date(), status);
  //        return new ResponseEntity<>(response, status);
  //    }
}
