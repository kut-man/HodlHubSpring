package com.example.hodlhub.controller;

import com.example.hodlhub.dto.response.ResponseCoinDTO;
import com.example.hodlhub.model.Coin;
import com.example.hodlhub.service.CoinService;
import com.example.hodlhub.util.ApiResponse;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/coin")
public class CoinController {
  private final CoinService coinService;
  private final ModelMapper modelMapper;

  @Autowired
  public CoinController(CoinService coinService, ModelMapper modelMapper) {
    this.coinService = coinService;
    this.modelMapper = modelMapper;
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<ResponseCoinDTO>>> getCoins() {
    List<Coin> coinList = coinService.getCoinsWithPrices();
    List<ResponseCoinDTO> coinDTOList =
        coinList.stream()
            .map(coin -> modelMapper.map(coin, ResponseCoinDTO.class))
            .toList();

    HttpStatus status = HttpStatus.OK;
    ApiResponse<List<ResponseCoinDTO>> response = new ApiResponse<>(status, coinDTOList, "/coin");

    return new ResponseEntity<>(response, status);
  }
}
