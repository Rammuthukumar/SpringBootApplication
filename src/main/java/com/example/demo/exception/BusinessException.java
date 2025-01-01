package com.example.demo.exception;

import org.springframework.stereotype.Component;

@Component
public class BusinessException extends RuntimeException{
    
    private String errorCode;
    private String errorDisc;

    public String getErrorCode() {
        return errorCode;
    }
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    public String getErrorDisc() {
        return errorDisc;
    }
    public void setErrorDisc(String errorDisc) {
        this.errorDisc = errorDisc;
    }

    public BusinessException(String errorCode, String errorDisc){
        super();
        this.errorCode = errorCode;
        this.errorDisc = errorDisc;
    }

    public BusinessException(){
        
    }

    
}
