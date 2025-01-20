package com.example.demo.Order;

import com.example.demo.BookStore.BookResponseDTO;

import lombok.Data;

@Data
public class OrderResponseDTO {
    private int id;
    private BookResponseDTO bookResponse;
    private String userName;
    private int quantity;
    private double totalPrice;
}
