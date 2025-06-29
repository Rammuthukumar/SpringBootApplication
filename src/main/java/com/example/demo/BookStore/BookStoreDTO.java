package com.example.demo.BookStore;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.hateoas.RepresentationModel;

import com.example.demo.Category.CategoryDTO;
import com.example.demo.Publisher.PublisherDTO;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

// RepresentationalModel -> for HateOAS
// Serializable -> Caching obj should be serialiazable (Redis Caching)
public class BookStoreDTO extends RepresentationModel<BookStoreDTO> implements Serializable{
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
    private Set<CategoryDTO> categoriesDTO;
}
