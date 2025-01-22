package com.example.demo.Category;

import java.io.Serializable;

import lombok.Data;

@Data
public class CategoryDTO implements Serializable{
    private int id;
    private String categoryName; 
}
