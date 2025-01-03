package com.example.demo.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.DTO.BookStoreDTO;
import com.example.demo.DTO.CategoryDTO;
import com.example.demo.DTO.PublisherDTO;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.EntityMapper;
import com.example.demo.model.BookStore;
import com.example.demo.model.Category;
import com.example.demo.model.Publisher;
import com.example.demo.repo.BookRepo;
import com.example.demo.repo.CategoryRepo;
import com.example.demo.repo.PublisherRepo;

@Service
public class BookService {

    @Autowired private BookRepo bookRepo;
    @Autowired private PublisherRepo publisherRepo;
    @Autowired private CategoryRepo categoryRepo;
    @Autowired private EntityMapper entityMapper;

    //@Autowired private CategoryRepo categoryRepo;
    private List<BookStoreDTO> bookStoreList = new ArrayList<>();
    private Set<CategoryDTO> savedCategoriesList = new HashSet<>();

    public List<BookStoreDTO> getAllBooks(Pageable pageable){
        Page<BookStore> books = bookRepo.findAll(pageable);

        if(books.isEmpty()){
            throw new BusinessException("601","Repo is Empty, Please add some data");
        }

        for(BookStore book : books){
            BookStoreDTO bookStoreDTO = entityMapper.bookStoreToBookStoreDTO(book);
            bookStoreDTO.setPublisherDTO(entityMapper.publisherToPublisherDTO(book.getPublisher()));

            for(Category category : book.getCategories()){
                CategoryDTO categoriesDto = new CategoryDTO();
                categoriesDto.setId(category.getId());
                categoriesDto.setCategoryName(category.getCategoryName());
                savedCategoriesList.add(categoriesDto);
            }
            bookStoreDTO.setCategoriesDTO(savedCategoriesList);
            bookStoreList.add(bookStoreDTO);
        }

        return bookStoreList;
    }

    public BookStoreDTO addBook(BookStoreDTO bookStoreDTO) {
        Publisher publisher = publisherRepo.findById(bookStoreDTO.getPublisherDTO().getId())
            .orElseThrow(() -> new BusinessException("614","Publisher not found in the database"));
       
        Set<Category> categories = new HashSet<>();
         
        for(CategoryDTO categoryDTO : bookStoreDTO.getCategoriesDTO()){
            Category category = categoryRepo.findById(categoryDTO.getId())
                .orElseThrow(() -> new BusinessException("615","Category not found in the database"));
            
            categories.add(category);
        }

        BookStore book = entityMapper.bookStoreDTOtoBookStore(bookStoreDTO);  // mapping dto to entity
        book.setPublisher(publisher);
        book.setCategories(categories);
        
        BookStore savedBook = bookRepo.save(book);                            // saving entity data in db

        bookStoreDTO = entityMapper.bookStoreToBookStoreDTO(savedBook);       // mapping saved entity to dto
        bookStoreDTO.setPublisherDTO(entityMapper.publisherToPublisherDTO(savedBook.getPublisher()));  // mapping entity to dto

        Set<CategoryDTO> savedCategories = new HashSet<>();

        for(Category category : savedBook.getCategories()){
            CategoryDTO categoriesDto = new CategoryDTO();
            categoriesDto.setId(category.getId());
            categoriesDto.setCategoryName(category.getCategoryName());
            savedCategories.add(categoriesDto);
        }

        bookStoreDTO.setCategoriesDTO(savedCategories);
    
        return bookStoreDTO;
    }

    public BookStoreDTO getBook(int id) {
        BookStore book = bookRepo.findById(id).orElseThrow(()->  
            new BusinessException("602", "Given book id does not found"));

        BookStoreDTO bookStoreDTO = entityMapper.bookStoreToBookStoreDTO(book);
        bookStoreDTO.setPublisherDTO(entityMapper.publisherToPublisherDTO(book.getPublisher()));

        for(Category category : book.getCategories()){
            CategoryDTO categoriesDto = new CategoryDTO();
            categoriesDto.setId(category.getId());
            categoriesDto.setCategoryName(category.getCategoryName());
            savedCategoriesList.add(categoriesDto);
        }
        bookStoreDTO.setCategoriesDTO(savedCategoriesList);

        return bookStoreDTO;
    }

    public BookStoreDTO updateBook(int id, BookStoreDTO bookDTO) {
        BookStore matchingBook = bookRepo.findById(id).orElseThrow(() -> 
            new BusinessException("602","Given book id does not found in the database"));

        matchingBook.setPrice(bookDTO.getPrice());
        matchingBook.setStock(bookDTO.getStock());

        matchingBook = bookRepo.save(matchingBook);

        BookStoreDTO bookStoreDTO = entityMapper.bookStoreToBookStoreDTO(matchingBook);
        bookStoreDTO.setPublisherDTO(entityMapper.publisherToPublisherDTO(matchingBook.getPublisher()));

        for(Category category : matchingBook.getCategories()){
            CategoryDTO categoriesDto = new CategoryDTO();
            categoriesDto.setId(category.getId());
            categoriesDto.setCategoryName(category.getCategoryName());
            savedCategoriesList.add(categoriesDto);
        }
        bookStoreDTO.setCategoriesDTO(savedCategoriesList);

        return bookStoreDTO;
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
