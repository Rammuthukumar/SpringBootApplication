package com.example.demo.exception;

import org.springframework.stereotype.Component;

@Component
public class ResourceNotFoundException extends RuntimeException{
    private String errorDisc;

    public String getErrorDisc() {
        return errorDisc;
    }

    public void setErrorDisc(String errorDisc) {
        this.errorDisc = errorDisc;
    }

    public ResourceNotFoundException(String errorDisc) {
        super();
        this.errorDisc = errorDisc;
    }

    public ResourceNotFoundException(){
    }
}
