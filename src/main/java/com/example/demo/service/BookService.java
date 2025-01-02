package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.DTO.BookStoreDTO;
import com.example.demo.DTO.CategoryDTO;
import com.example.demo.DTO.PublisherDTO;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.BookStore;
import com.example.demo.model.Publisher;
import com.example.demo.repo.BookRepo;
import com.example.demo.repo.PublisherRepo;

@Service
public class BookService {

    @Autowired private BookRepo bookRepo;

    @Autowired private PublisherRepo publisherRepo;

    //@Autowired private CategoryRepo categoryRepo;
    
    public Page<BookStore> getAllBooks(Pageable pageable){
        Page<BookStore> books = bookRepo.findAll(pageable);
        if(books.isEmpty()){
            throw new BusinessException("601","Repo is Empty, Please add some data");
        }
        return books;
    }

    public BookStoreDTO addBook(BookStoreDTO bookStoreDTO) {
        Publisher publisher = publisherRepo.findById(bookStoreDTO.getPublisherDTO().getId())
            .orElseThrow(() -> new BusinessException("614","Publisher not found in the database"));
       
            /*  if(newBook.getPublisher() == null || !publisherRepo.existsById(newBook.getPublisher().getId()))
            throw new BusinessException("614","Publisher not found in the database"); */
        
        /* for(CategoryDTO categoryDTO : bookStoreDTO.getCategoriesDTO()){
            
        } */

        BookStore book = new BookStore();
        book.setBookName(bookStoreDTO.getBookName());
        book.setAuthorName(bookStoreDTO.getAuthorName());
        book.setLanguage(bookStoreDTO.getLanguage());
        book.setGenre(bookStoreDTO.getGenre());
        book.setPublishedDate(bookStoreDTO.getPublishedDate());
        book.setPages(bookStoreDTO.getPages());
        book.setPrice(bookStoreDTO.getPrice());
        book.setStock(bookStoreDTO.getStock());
        book.setYear(bookStoreDTO.getYear());
        book.setPublisher(publisher);
        
        BookStore savedBook = bookRepo.save(book);

        bookStoreDTO.setId(savedBook.getId());
        bookStoreDTO.setBookName(savedBook.getBookName());
        bookStoreDTO.setAuthorName(savedBook.getAuthorName());
        bookStoreDTO.setLanguage(savedBook.getLanguage());
        bookStoreDTO.setGenre(savedBook.getGenre());
        bookStoreDTO.setPublishedDate(savedBook.getPublishedDate());
        bookStoreDTO.setPages(savedBook.getPages());
        bookStoreDTO.setPrice(savedBook.getPrice());
        bookStoreDTO.setStock(savedBook.getStock());
        bookStoreDTO.setYear(savedBook.getYear());
        
        PublisherDTO publisherDTO = new PublisherDTO();
        publisherDTO.setId(savedBook.getPublisher().getId());
        publisherDTO.setPublisherName(savedBook.getPublisher().getPublisherName());

        bookStoreDTO.setPublisherDTO(publisherDTO);
    
        return bookStoreDTO;
    }

    public BookStore getBook(int id) {
        return bookRepo.findById(id).orElseThrow(()->  
            new BusinessException("602", "Given book id does not found"));
    }

    public BookStore updateBook(int id, BookStore book) {
        BookStore matchingBook = getBook(id);
        matchingBook.setBookName(book.getBookName());
        matchingBook.setAuthorName(book.getAuthorName());
        matchingBook.setPrice(book.getPrice());
        bookRepo.save(matchingBook);
        return matchingBook;
    }

    public String deleteBook(int id) {
        if(!bookRepo.existsById(id))
            throw new BusinessException("606","Given book id does not exist in the DataBase , please enter some valid id");
        bookRepo.deleteById(id);
        return "Sucessfully Deleted";
    }

    public String deleteAllBook(){
        bookRepo.deleteAll();
        return "Sucessfully Deleted";
    }

    public Page<BookStore> searchByAuthor(String authorName, Pageable pageable) {
        Page<BookStore> books = bookRepo.findByAuthorNameContainingIgnoreCase(authorName,pageable);
        if(books.isEmpty())
            throw new ResourceNotFoundException("No Book is found for the given author name "+ authorName);
        return books;
    }

    public Page<BookStore> searchByBook(String bookName, Pageable pageable){
        Page<BookStore> books = bookRepo.findByBookNameContainingIgnoreCase(bookName,pageable);
        if(books.isEmpty())
            throw new ResourceNotFoundException("No book is found by the name "+ bookName);
        return books;
    }

    public List<BookStore> searchByYear(int year){
        List<BookStore> books = bookRepo.findByYear(year);
        if(books.isEmpty())
            throw new ResourceNotFoundException("No Book is found for the year "+year);
        return books;
    }

    public List<BookStore> filterByAuthor(String authorName, int year){
        List<BookStore> books = bookRepo.findByAuthorNameContainingIgnoreCaseAndYear(authorName, year);
        if(books.isEmpty())
            throw new ResourceNotFoundException("No book is found for the given author name "+ authorName + " and year "+year);
        return books;
    } 

    public Page<BookStore> searchByBookNameAndAuthorName(String bookName,String authorName,Pageable pageable){
        Page<BookStore> books = bookRepo.findByBookNameContainingAndAuthorNameContaining(bookName,authorName,pageable);
        if(books.isEmpty())
            throw new ResourceNotFoundException("No book is found for the given book name "+bookName+" and author name "+authorName);
        return books;

    }

    public Page<BookStore> searchByGenre(String genre, Pageable pageable) {
        Page<BookStore> books = bookRepo.findByGenreContaining(genre,pageable);

        if(books.isEmpty())
            throw new ResourceNotFoundException("Cant find any books for the genre "+genre);
        return books;
    }

    public Page<BookStore> getBooksByPriceRange(double minPrice, double maxPrice, Pageable pageable) {
        Page<BookStore> books = bookRepo.findByPriceBetween(minPrice,maxPrice,pageable);

        if(books.isEmpty())
            throw new ResourceNotFoundException("Cant find books in this price range "+minPrice+" - "+maxPrice);
        return books;
    }

}
