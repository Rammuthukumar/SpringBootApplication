package com.example.demo.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.BookStore;

@Repository
public interface BookRepo extends JpaRepository<BookStore, Integer> {
  
  Page<BookStore> findByAuthorNameContainingIgnoreCase(String authorName,Pageable pageable);

  Page<BookStore> findByBookNameContainingIgnoreCase(String bookName,Pageable pageable);

  List<BookStore> findByYear(int year);

  List<BookStore> findByAuthorNameContainingIgnoreCaseAndYear(String authorName, int year);

  Page<BookStore> findByBookNameContainingAndAuthorNameContaining(String bookName, String authorName, Pageable pageable);
  
  Page<BookStore> findByGenreContaining(String genre, Pageable pageable);

  Page<BookStore> findByPriceBetween(double minPrice, double maxPrice, Pageable pageable);
}
