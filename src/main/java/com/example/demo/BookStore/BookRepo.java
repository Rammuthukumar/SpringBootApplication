package com.example.demo.BookStore;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepo extends JpaRepository<BookStore, Integer> {

  @Query("SELECT b FROM BookStore b LEFT JOIN FETCH b.categories WHERE b.id = :id")
  Optional<BookStore> findByIdWithCategories(@Param("id") int id);

  List<BookStore> findByAuthorNameContainingIgnoreCase(String authorName);

  List<BookStore> findByBookNameContainingIgnoreCase(String bookName);

  List<BookStore> findByYear(int year);

  List<BookStore> findByAuthorNameContainingIgnoreCaseAndYear(String authorName, int year);

  Page<BookStore> findByBookNameContainingAndAuthorNameContaining(String bookName, String authorName, Pageable pageable);
  
  Page<BookStore> findByGenreContaining(String genre, Pageable pageable);

  Page<BookStore> findByPriceBetween(double minPrice, double maxPrice, Pageable pageable);
}
