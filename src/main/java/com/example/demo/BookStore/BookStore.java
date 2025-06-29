package com.example.demo.BookStore;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


import com.example.demo.Category.Category;
import com.example.demo.Publisher.Publisher;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
public class BookStore {
    
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull(message = "Book Name is required")
    @Size(min = 2 , max = 100 , message = "Book Name length should in between 2 char to 100 char")
    private String bookName;

    @NotNull(message = "Author Name is required")
    @Size(min = 2, max = 50, message = "Author Name length should in between 2 char to 50 char")
    private String authorName;

    @NotNull(message = "Year is required")
    @Min(value = 1400, message = "Year should by greater than 1400")
    private int year;

    @NotNull(message = "Book Price is required")
    @Min(value = 1, message = "Book price should be a positive number")
    private double price;

    @NotNull(message = "Genre is required")
    @Size(min = 2, max = 50, message = "Genre length should be between 2 to 50 characters")
    private String genre;

    @Size(max = 50, message = "Language name should not exceed 50 characters")
    private String language;

    @PastOrPresent(message = "Published date cannot be in the future")
    private LocalDate publishedDate;

    @Min(value = 1, message = "Page count must be at least 1")
    private int pages;

    @Min(value = 0, message = "Stock cannot be negative")
    private int stock;

    @ManyToOne @JoinColumn(name = "publisher_id") 
    private Publisher publisher; 

    @ManyToMany 
    @JoinTable(
        name = "book_category",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();
}
