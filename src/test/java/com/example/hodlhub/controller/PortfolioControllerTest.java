package com.example.hodlhub.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.hodlhub.dto.request.RequestPortfolioDTO;
import com.example.hodlhub.dto.response.ResponseChartDataDTO;
import com.example.hodlhub.dto.response.ResponsePortfolioDTO;
import com.example.hodlhub.model.Holder;
import com.example.hodlhub.model.Portfolio;
import com.example.hodlhub.security.HolderDetails;
import com.example.hodlhub.service.PortfolioChartService;
import com.example.hodlhub.service.PortfolioService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

class PortfolioControllerTest {

  private MockMvc mockMvc;
  private PortfolioService portfolioService;
  private ModelMapper modelMapper;
  private PortfolioChartService portfolioChartService;
  private PortfolioController portfolioController;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    portfolioService = Mockito.mock(PortfolioService.class);
    modelMapper = Mockito.mock(ModelMapper.class);
    portfolioChartService = Mockito.mock(PortfolioChartService.class);
    objectMapper = new ObjectMapper();

    portfolioController =
        new PortfolioController(portfolioService, modelMapper, portfolioChartService);

    mockMvc = MockMvcBuilders.standaloneSetup(portfolioController).build();
  }

  @Test
  void createPortfolio_ValidPortfolio_ReturnsCreated() throws Exception {
    RequestPortfolioDTO portfolioDTO = new RequestPortfolioDTO();
    portfolioDTO.setName("Test Portfolio");
    portfolioDTO.setColor("red");
    portfolioDTO.setAvatar("A");

    Portfolio portfolio = new Portfolio();
    portfolio.setName("Test Portfolio");
    portfolio.setColor("red");
    portfolio.setAvatar("A");

    when(modelMapper.map(any(RequestPortfolioDTO.class), eq(Portfolio.class)))
        .thenReturn(portfolio);
    doNothing().when(portfolioService).save(any(Portfolio.class), anyString());

    HolderDetails holderDetails = new HolderDetails(new Holder());
    holderDetails.getHolder().setEmail("test@example.com");

    mockMvc
        .perform(
            post("/portfolio")
                .with(SecurityMockMvcRequestPostProcessors.user(holderDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(portfolioDTO)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.message").value("Portfolio created successfully"));

    verify(portfolioService, times(1)).save(any(Portfolio.class), eq("test@example.com"));
  }

  @Test
  void getPortfolios_ReturnsPortfolios() throws Exception {
    // Arrange
    Portfolio portfolio1 = new Portfolio();
    portfolio1.setId(1);
    portfolio1.setName("Portfolio 1");
    portfolio1.setColor("red");
    portfolio1.setAvatar("A");

    Portfolio portfolio2 = new Portfolio();
    portfolio2.setId(2);
    portfolio2.setName("Portfolio 2");
    portfolio2.setColor("red");
    portfolio2.setAvatar("A");

    List<Portfolio> portfolios = Arrays.asList(portfolio1, portfolio2);

    ResponsePortfolioDTO portfolioDTO1 = new ResponsePortfolioDTO();
    portfolioDTO1.setId(1);
    portfolioDTO1.setName("Portfolio 1");
    portfolioDTO1.setColor("red");
    portfolioDTO1.setAvatar("A");

    ResponsePortfolioDTO portfolioDTO2 = new ResponsePortfolioDTO();
    portfolioDTO2.setId(2);
    portfolioDTO2.setName("Portfolio 2");
    portfolioDTO2.setColor("red");
    portfolioDTO2.setAvatar("A");

    when(portfolioService.getUserPortfolioSummaries(anyString())).thenReturn(portfolios);
    when(modelMapper.map(portfolio1, ResponsePortfolioDTO.class)).thenReturn(portfolioDTO1);
    when(modelMapper.map(portfolio2, ResponsePortfolioDTO.class)).thenReturn(portfolioDTO2);

    HolderDetails holderDetails = new HolderDetails(new Holder());
    holderDetails.getHolder().setEmail("test@example.com");

    mockMvc
        .perform(get("/portfolio").with(SecurityMockMvcRequestPostProcessors.user(holderDetails)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(2))
        .andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].name").value("Portfolio 1"))
        .andExpect(jsonPath("$.data[1].id").value(2))
        .andExpect(jsonPath("$.data[1].name").value("Portfolio 2"));

    verify(portfolioService, times(1)).getUserPortfolioSummaries("test@example.com");
  }

  @Test
  @WithMockUser(username = "test@example.com")
  void getPortfolio_ExistingPortfolio_ReturnsPortfolio() throws Exception {
    int portfolioId = 1;

    Portfolio portfolio = new Portfolio();
    portfolio.setId(portfolioId);
    portfolio.setName("Test Portfolio");

    ResponsePortfolioDTO portfolioDTO = new ResponsePortfolioDTO();
    portfolioDTO.setId(portfolioId);
    portfolioDTO.setName("Test Portfolio");

    when(portfolioService.getById(portfolioId, "test@example.com")).thenReturn(portfolio);
    when(modelMapper.map(portfolio, ResponsePortfolioDTO.class)).thenReturn(portfolioDTO);

    HolderDetails holderDetails = new HolderDetails(new Holder());
    holderDetails.getHolder().setEmail("test@example.com");

    mockMvc
        .perform(
            get("/portfolio/{portfolioId}", portfolioId)
                .with(SecurityMockMvcRequestPostProcessors.user(holderDetails)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value(portfolioId))
        .andExpect(jsonPath("$.data.name").value("Test Portfolio"));

    verify(portfolioService, times(1)).getById(portfolioId, "test@example.com");
  }

  @Test
  @WithMockUser(username = "test@example.com")
  void getPortfolioChart_ReturnsChartData() throws Exception {
    int portfolioId = 1;
    String interval = "1d";

    ResponseChartDataDTO dataPoint1 =
        new ResponseChartDataDTO(LocalDateTime.now().minusDays(2), 10000.00);
    ResponseChartDataDTO dataPoint2 =
        new ResponseChartDataDTO(LocalDateTime.now().minusDays(1), 12000.00);

    List<ResponseChartDataDTO> chartData = Arrays.asList(dataPoint1, dataPoint2);

    when(portfolioChartService.getPortfolioHistoricalValue(
            portfolioId, "test@example.com", interval))
        .thenReturn(chartData);

    HolderDetails holderDetails = new HolderDetails(new Holder());
    holderDetails.getHolder().setEmail("test@example.com");

    mockMvc
        .perform(
            get("/portfolio/{portfolioId}/chart/{interval}", portfolioId, interval)
                .with(SecurityMockMvcRequestPostProcessors.user(holderDetails)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(2));

    verify(portfolioChartService, times(1))
        .getPortfolioHistoricalValue(portfolioId, "test@example.com", interval);
  }

  @Test
  @WithMockUser(username = "test@example.com")
  void editPortfolio_ValidEdit_ReturnsOk() throws Exception {
    int portfolioId = 1;
    RequestPortfolioDTO portfolioDTO = new RequestPortfolioDTO();
    portfolioDTO.setName("Updated Portfolio");
    portfolioDTO.setColor("red");
    portfolioDTO.setAvatar("A");

    Portfolio portfolio = new Portfolio();
    portfolio.setName("Updated Portfolio");
    portfolio.setColor("red");
    portfolio.setAvatar("A");

    when(modelMapper.map(any(RequestPortfolioDTO.class), eq(Portfolio.class)))
        .thenReturn(portfolio);
    doNothing().when(portfolioService).edit(any(Portfolio.class), eq(portfolioId), anyString());

    HolderDetails holderDetails = new HolderDetails(new Holder());
    holderDetails.getHolder().setEmail("test@example.com");

    mockMvc
        .perform(
            put("/portfolio/{portfolioId}", portfolioId)
                .with(SecurityMockMvcRequestPostProcessors.user(holderDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(portfolioDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Portfolio updated successfully"))
        .andExpect(jsonPath("$.redirect").value("/portfolio"));

    verify(portfolioService, times(1))
        .edit(any(Portfolio.class), eq(portfolioId), eq("test@example.com"));
  }

  @Test
  @WithMockUser(username = "test@example.com")
  void removePortfolio_ExistingPortfolio_ReturnsOk() throws Exception {
    int portfolioId = 1;

    doNothing()
        .when(portfolioService)
        .removePortfolioByNameAndHolder(portfolioId, "test@example.com");

    HolderDetails holderDetails = new HolderDetails(new Holder());
    holderDetails.getHolder().setEmail("test@example.com");

    mockMvc
        .perform(
            delete("/portfolio/{portfolioId}", portfolioId)
                .with(SecurityMockMvcRequestPostProcessors.user(holderDetails)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Portfolio removed successfully"))
        .andExpect(jsonPath("$.redirect").value("/portfolio"));

    verify(portfolioService, times(1))
        .removePortfolioByNameAndHolder(portfolioId, "test@example.com");
  }

  @Test
  @WithMockUser(username = "test@example.com")
  void removeAsset_ExistingAsset_ReturnsOk() throws Exception {
    int portfolioId = 1;
    String ticker = "BTC";

    doNothing()
        .when(portfolioService)
        .removeAssetFromPortfolio(portfolioId, ticker, "test@example.com");

    HolderDetails holderDetails = new HolderDetails(new Holder());
    holderDetails.getHolder().setEmail("test@example.com");

    mockMvc
        .perform(
            delete("/portfolio/{portfolioId}/{ticker}", portfolioId, ticker)
                .with(SecurityMockMvcRequestPostProcessors.user(holderDetails)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Asset removed successfully"))
        .andExpect(jsonPath("$.redirect").value("/portfolio"));

    verify(portfolioService, times(1))
        .removeAssetFromPortfolio(portfolioId, ticker, "test@example.com");
  }
}
