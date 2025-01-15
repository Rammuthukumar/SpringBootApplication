package com.example.demo.dto;

import lombok.Data;

@Data
public class OrderDTO {
    private int id;
    private int bookId;
    private int userId;
    private int quantity;
}
