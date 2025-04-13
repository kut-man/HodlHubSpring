package com.example.hodlhub.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.hodlhub.dto.response.ResponseHolderDTO;
import com.example.hodlhub.model.Holder;
import com.example.hodlhub.security.HolderDetails;
import com.example.hodlhub.service.HolderService;

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

class ProfileControllerTest {

  private MockMvc mockMvc;
  private HolderService holderService;
  private ModelMapper modelMapper;
  private ProfileController profileController;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    holderService = Mockito.mock(HolderService.class);
    modelMapper = Mockito.mock(ModelMapper.class);
    objectMapper = new ObjectMapper();

    profileController = new ProfileController(holderService, modelMapper);

    mockMvc = MockMvcBuilders.standaloneSetup(profileController).build();
  }

  @Test
  @WithMockUser(username = "test@example.com")
  void getHolder_ReturnsHolderDetails() throws Exception {
    Holder holder = new Holder();
    holder.setId(1);
    holder.setEmail("test@example.com");
    holder.setName("Test User");
    holder.setAvatar("avatar.jpg");

    ResponseHolderDTO holderDTO = new ResponseHolderDTO();
    holderDTO.setEmail("test@example.com");
    holderDTO.setName("Test User");
    holderDTO.setAvatar("avatar.jpg");

    when(holderService.getHolder("test@example.com")).thenReturn(holder);
    when(modelMapper.map(holder, ResponseHolderDTO.class)).thenReturn(holderDTO);

    HolderDetails holderDetails = new HolderDetails(new Holder());
    holderDetails.getHolder().setEmail("test@example.com");

    mockMvc
        .perform(get("/user").with(SecurityMockMvcRequestPostProcessors.user(holderDetails)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.email").value("test@example.com"))
        .andExpect(jsonPath("$.data.name").value("Test User"))
        .andExpect(jsonPath("$.data.avatar").value("avatar.jpg"));

    verify(holderService, times(1)).getHolder("test@example.com");
  }

  @Test
  @WithMockUser(username = "test@example.com")
  void editHolder_ValidEdit_ReturnsOk() throws Exception {
    ResponseHolderDTO holderDTO = new ResponseHolderDTO();
    holderDTO.setName("Updated Name");
    holderDTO.setAvatar("updated-avatar.jpg");

    Holder holder = new Holder();
    holder.setEmail("test@example.com");
    holder.setName("Test User");
    holder.setAvatar("avatar.jpg");

    when(holderService.getHolder("test@example.com")).thenReturn(holder);
    doNothing().when(holderService).editHolder(any(Holder.class));

    HolderDetails holderDetails = new HolderDetails(new Holder());
    holderDetails.getHolder().setEmail("test@example.com");

    mockMvc
        .perform(
            put("/user")
                .with(SecurityMockMvcRequestPostProcessors.user(holderDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(holderDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Profile edited successfully"));

    verify(holderService, times(1)).getHolder("test@example.com");
    verify(holderService, times(1)).editHolder(any(Holder.class));
  }
}
