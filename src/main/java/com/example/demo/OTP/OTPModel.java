package com.example.demo.OTP;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
public class OTPModel {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String OTP;
    private LocalDateTime localDataTime;

    public OTPModel(String email, String getOtp, LocalDateTime plusMinutes) {
        this.email=email;
        this.OTP=getOtp;
        this.localDataTime=plusMinutes;
    }
}
