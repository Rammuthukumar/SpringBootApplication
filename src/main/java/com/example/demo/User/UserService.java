package com.example.demo.User;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.OTP.OTPGenerater;
import com.example.demo.OTP.OTPModel;
import com.example.demo.OTP.OTPRepo;
import com.example.demo.utils.EmailService;

@Service
public class UserService {

    @Autowired private UserRepo repo;
    @Autowired private EmailService emailService;
    @Autowired private OTPRepo otpRepo;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    public User saveUser(User user) {
        user.setPassword(encoder.encode(user.getPassword()));

        logger.info("service method called");
        if(verifyEmail(user.getEmail())){
            logger.info("user verified");

            //generating otp
            String getOtp = OTPGenerater.generateOtp();
            //saving otp in db for verification with the user input
            otpRepo.save(new OTPModel(user.getEmail(),getOtp,LocalDateTime.now().plusMinutes(7)));
            //sending otp mail to the user.
            emailService.sendOtpEmail(user.getEmail(),getOtp);
            return repo.save(user);
        }
        logger.info("email id already registred");
        return new User(); 
    }
    
    public boolean verifyEmail(String email){
        logger.info(email);
        logger.info("verfiy email called");
        Optional<User> user = repo.findByEmail(email);
        return user.isEmpty();
    }
}
