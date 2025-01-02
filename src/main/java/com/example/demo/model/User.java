package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
@Table(name ="users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull(message = "Username shouldnt be null")
    @Size(min = 3, max = 16, message="Username should be of length 3 to 16 chars")
    private String username;

    @NotNull(message = "Password shouldnt be null")
    @Size(min = 4, max = 200, message="Password should be of length 3 to 16 chars")
    private String password;

    @NotNull(message = "Role shouldnt be null")
    private String role;

}
