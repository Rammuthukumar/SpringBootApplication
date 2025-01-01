package com.example.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Publisher;

public interface PublisherRepo extends JpaRepository<Publisher,Integer> {
    
} 