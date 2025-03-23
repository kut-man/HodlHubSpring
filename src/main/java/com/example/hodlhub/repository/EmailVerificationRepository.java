package com.example.hodlhub.repository;

import com.example.hodlhub.model.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Integer> {
  Optional<EmailVerification> findByEmail(String email);
}
