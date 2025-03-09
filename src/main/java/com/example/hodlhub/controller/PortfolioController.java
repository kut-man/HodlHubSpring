package com.example.hodlhub.controller;

import com.example.hodlhub.dto.request.RequestPortfolioDTO;
import com.example.hodlhub.dto.response.ResponseChartDataDTO;
import com.example.hodlhub.dto.response.ResponsePortfolioDTO;
import com.example.hodlhub.model.Portfolio;
import com.example.hodlhub.security.HolderDetails;
import com.example.hodlhub.service.PortfolioChartService;
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
  private final PortfolioChartService portfolioChartService;

  public PortfolioController(
      PortfolioService portfolioService,
      ModelMapper modelMapper,
      PortfolioChartService portfolioChartService) {
    this.portfolioService = portfolioService;
    this.modelMapper = modelMapper;
    this.portfolioChartService = portfolioChartService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<?>> createPortfolio(
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
  public ResponseEntity<ApiResponse<List<ResponsePortfolioDTO>>> getPortfolios(
      @AuthenticationPrincipal HolderDetails holderDetails) {
    List<Portfolio> portfolioList =
        portfolioService.getUserPortfolioSummaries(holderDetails.getUsername());
    List<ResponsePortfolioDTO> portfolioDTOList =
        portfolioList.stream()
            .map(portfolio -> modelMapper.map(portfolio, ResponsePortfolioDTO.class))
            .collect(Collectors.toList());

    HttpStatus status = HttpStatus.OK;
    ApiResponse<List<ResponsePortfolioDTO>> response =
        new ApiResponse<>(status, portfolioDTOList, "/portfolio");

    return new ResponseEntity<>(response, status);
  }

  @GetMapping("/{portfolioId}")
  public ResponseEntity<ApiResponse<ResponsePortfolioDTO>> getPortfolio(
      @AuthenticationPrincipal HolderDetails holderDetails, @PathVariable int portfolioId) {
    Portfolio portfolio = portfolioService.getById(portfolioId, holderDetails.getUsername());
    ResponsePortfolioDTO portfolioDTO = modelMapper.map(portfolio, ResponsePortfolioDTO.class);

    HttpStatus status = HttpStatus.OK;
    ApiResponse<ResponsePortfolioDTO> response =
        new ApiResponse<>(status, portfolioDTO, "/portfolio");

    return new ResponseEntity<>(response, status);
  }

  @GetMapping("/{portfolioId}/chart/{interval}")
  public ResponseEntity<ApiResponse<List<ResponseChartDataDTO>>> getPortfolioChart(
      @AuthenticationPrincipal HolderDetails holderDetails,
      @PathVariable int portfolioId,
      @PathVariable String interval) {
    List<ResponseChartDataDTO> chartDataDTO =
        portfolioChartService.getPortfolioHistoricalValue(
            portfolioId, holderDetails.getUsername(), interval);

    HttpStatus status = HttpStatus.OK;
    ApiResponse<List<ResponseChartDataDTO>> response =
        new ApiResponse<>(status, chartDataDTO, "/portfolio");

    return new ResponseEntity<>(response, status);
  }

  @PutMapping("/{portfolioId}")
  public ResponseEntity<ApiResponse<?>> editPortfolio(
      @PathVariable int portfolioId,
      @RequestBody @Valid RequestPortfolioDTO portfolioDTO,
      BindingResult bindingResult,
      @AuthenticationPrincipal HolderDetails holderDetails) {

    if (bindingResult.hasErrors()) {
      HttpStatus status = HttpStatus.BAD_REQUEST;
      ApiResponse<Void> response = new ApiResponse<>(status, bindingResult, "/portfolio");
      return new ResponseEntity<>(response, status);
    }

    Portfolio portfolio = modelMapper.map(portfolioDTO, Portfolio.class);
    portfolioService.edit(portfolio, portfolioId, holderDetails.getUsername());

    HttpStatus status = HttpStatus.OK;
    ApiResponse<Void> response =
        new ApiResponse<>(status, "Portfolio updated successfully", "/portfolio");

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
  public ResponseEntity<?> removeAsset(
      @PathVariable int portfolioId,
      @PathVariable String ticker,
      @AuthenticationPrincipal HolderDetails holderDetails) {
    portfolioService.removeAssetFromPortfolio(portfolioId, ticker, holderDetails.getUsername());
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
