package com.example.demo.OTP;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OTPRepo extends JpaRepository<OTPModel,Long>{

    Optional<OTPModel> findByEmail(String email);
    
}
