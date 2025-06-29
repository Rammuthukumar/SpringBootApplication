package com.example.demo.Publisher;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PublisherRepo extends JpaRepository<Publisher,Integer> {

    Optional<Publisher> findByPublisherName(String publisherName);
    
} 