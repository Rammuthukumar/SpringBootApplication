package com.example.demo.dto;

import lombok.Data;

@Data
public class OrderDTO {
    private int id;
    private int bookId;
    private String userName;
    private int quantity;
}
