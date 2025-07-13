package com.example.demo.OTP;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.OTP.OTPModel;
import com.example.demo.OTP.OTPRepo;
import com.example.demo.OTP.OtpRequest;
//import com.example.demo.User.User;
//import com.example.demo.User.UserRepo;
import com.example.demo.User.UserService;
//import com.example.demo.utils.EmailService;



@RestController
@RequestMapping("verify-otp")
public class OTPController {

   // @Autowired private EmailService emailService;
   // @Autowired private UserRepo userRepo;
    @Autowired private OTPRepo otpRepo;

    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    @PostMapping
    public String verifyOTP(@RequestBody OtpRequest otpRequest) {
        logger.info("verify controller method called");
        logger.info(otpRequest.getEmail()+" "+otpRequest.getOtpCode());
        
        Optional<OTPModel> record = otpRepo.findByEmail(otpRequest.getEmail());

         if (record.isPresent() && 
            record.get().getOTP().equals(otpRequest.getOtpCode()) && 
            record.get().getLocalDataTime().isAfter(LocalDateTime.now())){
                logger.info("unique user email id verified");
                // User user = userRepo.findByEmail(otpRequest.getEmail()).get();
                 return "Successful";
             }
         return "";
    }
}

