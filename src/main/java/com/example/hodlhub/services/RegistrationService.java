package com.example.hodlhub.services;

import com.example.hodlhub.models.Holder;
import com.example.hodlhub.repositories.HolderRepository;
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


    public void save(Holder holder){
        holder.setPassword(passwordEncoder.encode(holder.getPassword()));
        holderRepository.save(holder);
    }
}
