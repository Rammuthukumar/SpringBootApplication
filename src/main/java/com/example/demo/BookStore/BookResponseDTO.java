package com.example.demo.BookStore;

import lombok.Data;

@Data
public class BookResponseDTO { 
    private int id;
    private String bookName;
    private String authorName;
}
