package com.example.hodlhub.service;

import com.example.hodlhub.model.Holder;
import com.example.hodlhub.repository.HolderRepository;
import org.springframework.stereotype.Service;

@Service
public class HolderService {

  private final HolderRepository holderRepository;

  public HolderService(HolderRepository holderRepository) {
    this.holderRepository = holderRepository;
  }

  public Holder getHolder(String email) {
    return holderRepository.findByEmail(email).orElse(null);
  }
}
