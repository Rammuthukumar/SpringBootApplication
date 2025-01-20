package com.example.demo.Order;

import lombok.Data;

@Data
public class OrderDTO {
    private int id;
    private int bookId;
    private int quantity;
}
