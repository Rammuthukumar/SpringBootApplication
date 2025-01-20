package com.example.demo.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.config.JwtService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {
    
    @Autowired private UserService service;
    @Autowired private JwtService jwtService;
    @Autowired AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public User saveUser(@Valid @RequestBody User user){
        return service.saveUser(user);
    }

    @PostMapping("/login")
    public String login(@Valid @RequestBody User user){
        Authentication authentication = authenticationManager
            .authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        if(authentication.isAuthenticated()) {
            return jwtService.createToken(user.getUsername());
        }
        else return "Login Failed";
    }
    
}
