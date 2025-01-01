package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.BookStore;
import com.example.demo.service.BookService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api")
public class BookController {

    @Autowired
    private BookService service;

    // CRUD

    @PostMapping("/book")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addBook(@Valid @RequestBody BookStore book, BindingResult result) {
        // if(book.getPublisher() == null || (book.getPublisher().getId()))
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addBook(book));
    }
    
    @GetMapping("/book/{id}")
    public ResponseEntity<?> getBook(@PathVariable int id){
        return new ResponseEntity<BookStore>(service.getBook(id),HttpStatus.OK);
    }

    @PutMapping("/book/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateBook(@Valid @PathVariable int id, @RequestBody BookStore book){
        return new ResponseEntity<BookStore>(service.updateBook(id,book),HttpStatus.OK);
    }

    @DeleteMapping("/book/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteBook(@PathVariable int id){
        return new ResponseEntity<String>(service.deleteBook(id), HttpStatus.OK);
    }

    @DeleteMapping("/book")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteAllBook(){
        return new ResponseEntity<String>(service.deleteAllBook(),HttpStatus.OK);
        
    }

    // Paging, Sorting and filtering
    @GetMapping("/book")
    public ResponseEntity<?> getAllBooks(
        @RequestParam(required=false) String bookName, @RequestParam(required=false) String authorName,
        @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="5") int size,
        @RequestParam(defaultValue = "id") String sort ) {
         
        Pageable pageable = PageRequest.of(page,size,Sort.by(sort).ascending());
 
        if(bookName != null && authorName != null) 
            return new ResponseEntity<Page<BookStore>>(service.searchByBookNameAndAuthorName(bookName,authorName,pageable),HttpStatus.OK);
        else if(bookName != null)
            return new ResponseEntity<Page<BookStore>>(service.searchByAuthor(bookName,pageable),HttpStatus.OK);
        else if(authorName != null)
            return new ResponseEntity<Page<BookStore>>(service.searchByBook(authorName,pageable),HttpStatus.OK);
             
        return new ResponseEntity<Page<BookStore>>(service.getAllBooks(pageable),HttpStatus.OK);
     }
 
    
    @GetMapping("/search/year")
    public ResponseEntity<?> searchByYear(@RequestParam int year){
        return new ResponseEntity<List<BookStore>>(service.searchByYear(year),HttpStatus.OK);
    }

    @GetMapping("/search/genre")
    public ResponseEntity<?> searchByGenre(
        @RequestParam(required = true) String genre, @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size, @RequestParam(defaultValue = "id") String sort ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        return new ResponseEntity<Page<BookStore>>(service.searchByGenre(genre,pageable),HttpStatus.OK);
    }

    @GetMapping("/filter/price-range")
    public ResponseEntity<?> getBooksByPriceRange(
        @RequestParam double minPrice,
        @RequestParam double maxPrice,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam(defaultValue = "price") String sort,
        @RequestParam(defaultValue = "asc") String order) {
        
        Sort sortOrder = order.equalsIgnoreCase("desc") ? Sort.by(sort).descending() : Sort.by(sort).ascending();
        Pageable pageable = PageRequest.of(page, size, sortOrder);

        return new ResponseEntity<>(service.getBooksByPriceRange(minPrice, maxPrice, pageable), HttpStatus.OK);
    }


    @GetMapping("/filter/author")
    public ResponseEntity<?> filterByAuthor(@RequestParam String authorName, @RequestParam int year){
        return new ResponseEntity<List<BookStore>>(service.filterByAuthor(authorName,year),HttpStatus.OK);
    }

}
