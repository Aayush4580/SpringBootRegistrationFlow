package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.PasswordResetToken;

@Repository
public interface PasswordResetTokenRepositiory extends JpaRepository<PasswordResetToken, Long>{

	PasswordResetToken findByToken(String token);
}
