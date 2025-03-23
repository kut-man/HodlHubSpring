package com.example.hodlhub.repository;

import com.example.hodlhub.model.Holder;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HolderRepository extends JpaRepository<Holder, Integer> {
  Optional<Holder> findByEmail(String email);
  boolean existsByEmail(String email);
}
