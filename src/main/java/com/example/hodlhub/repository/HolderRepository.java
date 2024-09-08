package com.example.hodlhub.repository;

import com.example.hodlhub.model.Holder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HolderRepository extends JpaRepository<Holder, Integer> {
  Optional<Holder> findByEmail(String email);
}
