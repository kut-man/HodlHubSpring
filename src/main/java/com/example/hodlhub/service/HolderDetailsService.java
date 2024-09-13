package com.example.hodlhub.service;

import com.example.hodlhub.model.Holder;
import com.example.hodlhub.repository.HolderRepository;
import com.example.hodlhub.security.HolderDetails;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class HolderDetailsService implements UserDetailsService {

  private final HolderRepository holderRepository;

  @Autowired
  public HolderDetailsService(HolderRepository holderRepository) {
    this.holderRepository = holderRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Optional<Holder> holder = holderRepository.findByEmail(email);
    if (holder.isEmpty()) {
      throw new UsernameNotFoundException("User Not Found!");
    }
    return new HolderDetails(holder.get());
  }
}
