package com.example.hodlhub.services;

import com.example.hodlhub.models.Holder;
import com.example.hodlhub.repositories.HolderRepository;
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
