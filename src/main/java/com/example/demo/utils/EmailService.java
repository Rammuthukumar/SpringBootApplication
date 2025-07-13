package com.example.demo.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.demo.audit.Audit;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail,String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Test Email from Spring Boot");
        message.setText(otp);
        message.setFrom("your_email@gmail.com");

        mailSender.send(message);
    }

    public void sendReminderEmail(Audit log){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(log.getEmail());
        message.setSubject(log.getBookname() +" is in limited stock");
        message.setText("Hey "+log.getUsername()+" Why wait any more, just order and start reading");
        message.setFrom("your_email@gmail.com");

        mailSender.send(message);
    }
}

