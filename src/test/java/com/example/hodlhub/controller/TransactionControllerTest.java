package com.example.hodlhub.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.hodlhub.dto.request.RequestTransactionDTO;
import com.example.hodlhub.model.Coin;
import com.example.hodlhub.model.Holder;
import com.example.hodlhub.model.Transaction;
import com.example.hodlhub.security.HolderDetails;

import com.example.hodlhub.service.TransactionService;
import com.example.hodlhub.util.enums.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

class TransactionControllerTest {

  private MockMvc mockMvc;
  private TransactionService transactionService;
  private TransactionController transactionController;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    transactionService = Mockito.mock(TransactionService.class);
    objectMapper = new ObjectMapper();

    transactionController = new TransactionController(transactionService);

    mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
  }

  @Test
  @WithMockUser(username = "test@example.com")
  void add_ValidTransaction_ReturnsCreated() throws Exception {
    RequestTransactionDTO transactionDTO = new RequestTransactionDTO();
    transactionDTO.setPortfolioId(1);
    transactionDTO.setCoin("BTC");
    transactionDTO.setAmount(0.5);
    transactionDTO.setPricePerCoin(50000);
    transactionDTO.setDate(LocalDateTime.now().toString());
    transactionDTO.setTransactionType("BUY");

    Coin coin = new Coin();
    coin.setTicker("BTC");
    Transaction transaction = new Transaction();
    transaction.setCoin(coin);
    transaction.setAmount(0.5);
    transaction.setPricePerCoin(50000);
    transaction.setDate(OffsetDateTime.now());
    transaction.setTransactionType(TransactionType.BUY);

    when(transactionService.mapToEntity(any(RequestTransactionDTO.class))).thenReturn(transaction);
    doNothing().when(transactionService).save(any(Transaction.class), anyString());

    HolderDetails holderDetails = new HolderDetails(new Holder());
    holderDetails.getHolder().setEmail("test@example.com");

    mockMvc
        .perform(
            post("/transaction")
                .with(SecurityMockMvcRequestPostProcessors.user(holderDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionDTO)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.message").value("Transaction added successfully"));

    verify(transactionService, times(1)).mapToEntity(any(RequestTransactionDTO.class));
    verify(transactionService, times(1)).save(any(Transaction.class), eq("test@example.com"));
  }

  @Test
  @WithMockUser(username = "test@example.com")
  void add_InvalidTransaction_ReturnsBadRequest() throws Exception {
    RequestTransactionDTO transactionDTO = new RequestTransactionDTO();

    HolderDetails holderDetails = new HolderDetails(new Holder());
    holderDetails.getHolder().setEmail("test@example.com");

    mockMvc
        .perform(
            post("/transaction")
                .with(SecurityMockMvcRequestPostProcessors.user(holderDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionDTO)))
        .andExpect(status().isBadRequest());

    verify(transactionService, times(0)).mapToEntity(any(RequestTransactionDTO.class));
    verify(transactionService, times(0)).save(any(Transaction.class), anyString());
  }
}
