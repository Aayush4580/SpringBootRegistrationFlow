package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.VerificationToken;

@Repository
public interface VerificationTokenRepositiory extends JpaRepository<VerificationToken, Long>{

	VerificationToken findByToken(String token);

}
