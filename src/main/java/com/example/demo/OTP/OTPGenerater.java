package com.example.demo.OTP;

import java.util.Random;

public class OTPGenerater {
    public static String generateOtp(){
        return String.valueOf(new Random().nextInt(999999 - 100000) + 100000);
    }
}
