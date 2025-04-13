package com.example.hodlhub.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.hodlhub.dto.response.ResponseCoinDTO;
import com.example.hodlhub.model.Coin;
import com.example.hodlhub.service.CoinService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

class CoinControllerTest {

  private MockMvc mockMvc;
  private CoinService coinService;
  private ModelMapper modelMapper;
  private CoinController coinController;

  @BeforeEach
  void setUp() {
    coinService = Mockito.mock(CoinService.class);
    modelMapper = Mockito.mock(ModelMapper.class);

    coinController = new CoinController(coinService, modelMapper);

    mockMvc = MockMvcBuilders.standaloneSetup(coinController).build();
  }

  @Test
  void getCoins_ReturnsCoins() throws Exception {
    // Arrange
    Coin bitcoin = new Coin();
    bitcoin.setTicker("BTC");
    bitcoin.setName("Bitcoin");
    bitcoin.setCurrentPrice(50000.00);

    Coin ethereum = new Coin();
    ethereum.setTicker("ETH");
    ethereum.setName("Ethereum");
    ethereum.setCurrentPrice(3000.00);

    List<Coin> coins = Arrays.asList(bitcoin, ethereum);

    ResponseCoinDTO bitcoinDTO = new ResponseCoinDTO();
    bitcoinDTO.setTicker("BTC");
    bitcoinDTO.setName("Bitcoin");
    bitcoinDTO.setCurrentPrice(50000.00);

    ResponseCoinDTO ethereumDTO = new ResponseCoinDTO();
    ethereumDTO.setTicker("ETH");
    ethereumDTO.setName("Ethereum");
    ethereumDTO.setCurrentPrice(3000.00);

    when(coinService.getCoinsWithPrices()).thenReturn(coins);
    when(modelMapper.map(bitcoin, ResponseCoinDTO.class)).thenReturn(bitcoinDTO);
    when(modelMapper.map(ethereum, ResponseCoinDTO.class)).thenReturn(ethereumDTO);

    // Act & Assert
    mockMvc
        .perform(get("/coin"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(2))
        .andExpect(jsonPath("$.data[0].ticker").value("BTC"))
        .andExpect(jsonPath("$.data[0].name").value("Bitcoin"))
        .andExpect(jsonPath("$.data[1].ticker").value("ETH"))
        .andExpect(jsonPath("$.data[1].name").value("Ethereum"));

    verify(coinService, times(1)).getCoinsWithPrices();
  }
}
