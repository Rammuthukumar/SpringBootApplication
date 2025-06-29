package com.example.demo.Category;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.example.demo.BookStore.BookStore;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class Category implements Serializable{
    
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String categoryName;

    @ManyToMany(mappedBy = "categories")
    @JsonIgnore
    private Set<BookStore> books = new HashSet<>();

    public Category(String categoryName){
        this.categoryName = categoryName;
    }
}
