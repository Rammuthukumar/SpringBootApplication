package com.example.demo.DTO;

import java.time.LocalDate;
import java.util.Set;

import com.example.demo.model.Category;

import lombok.Data;

@Data
public class BookStoreDTO {
    private int id;
    private String bookName;
    private String authorName;
    private int year;
    private double price;
    private String genre;
    private String language;
    private LocalDate publishedDate;
    private int pages;
    private int stock;
    private PublisherDTO publisherDTO;
   // private Set<CategoryDTO> categoriesDTO;
}