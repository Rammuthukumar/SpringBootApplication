package com.example.demo.config;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

import org.springframework.security.access.prepost.PreAuthorize;

@Target(ElementType.METHOD)                     // Allows this annotation on methods and classes
@Retention(RetentionPolicy.RUNTIME)              // Keeps annotation available at runtime for Spring Security
@PreAuthorize("hasRole('ADMIN')")                // Ensures only ADMIN users can access the method/class
public @interface AdminOnly {}                  // Declares the custom annotation @AdminOnly
