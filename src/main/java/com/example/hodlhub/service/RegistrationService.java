package com.example.hodlhub.service;

import com.example.hodlhub.model.Holder;
import com.example.hodlhub.repository.HolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {
  private final HolderRepository holderRepository;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public RegistrationService(HolderRepository holderRepository, PasswordEncoder passwordEncoder) {
    this.holderRepository = holderRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public void save(Holder holder) {
    holder.setPassword(passwordEncoder.encode(holder.getPassword()));
    holderRepository.save(holder);
  }
}
