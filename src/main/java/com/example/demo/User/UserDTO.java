package com.example.demo.User;

import org.springframework.hateoas.RepresentationModel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO extends RepresentationModel<UserDTO>{
    private String jwtToken;
}
