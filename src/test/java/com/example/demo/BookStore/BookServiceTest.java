package com.example.demo.BookStore;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.Category.Category;
import com.example.demo.Category.CategoryDTO;
import com.example.demo.Publisher.Publisher;
import com.example.demo.Publisher.PublisherDTO;
import com.example.demo.exception.BusinessException;
import com.example.demo.utils.EntityMapper;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepo bookRepo; // Mocked repository

    @Mock
    private EntityMapper entityMapper; // Mocked mapper

    @InjectMocks
    private BookService bookStoreService; // Service under test

    @Test
    void testGetBook_Success() {
        // Arrange
        int bookId = 1;

        // Mocking the book entity
        Publisher publisher = new Publisher();
        publisher.setId(1);
        publisher.setPublisherName("Test Publisher");

        Category category1 = new Category();
        category1.setId(1);
        category1.setCategoryName("Fiction");

        Category category2 = new Category();
        category2.setId(2);
        category2.setCategoryName("Adventure");

        BookStore book = new BookStore();
        book.setId(bookId);
        book.setBookName("Test Book");
        book.setAuthorName("Author");
        book.setYear(2021);
        book.setPrice(299.99);
        book.setGenre("Fantasy");
        book.setLanguage("English");
        book.setPublishedDate(LocalDate.of(2021, 5, 20));
        book.setPages(300);
        book.setStock(10);
        book.setPublisher(publisher);
        book.setCategories(Set.of(category1, category2));

        // Mocking DTOs
        PublisherDTO publisherDTO = new PublisherDTO();
        publisherDTO.setId(1);
        publisherDTO.setPublisherName("Test Publisher");

        BookStoreDTO bookStoreDTO = new BookStoreDTO();
        bookStoreDTO.setId(bookId);
        bookStoreDTO.setBookName("Test Book");
        bookStoreDTO.setAuthorName("Author");
        bookStoreDTO.setYear(2021);
        bookStoreDTO.setPrice(299.99);
        bookStoreDTO.setGenre("Fantasy");
        bookStoreDTO.setLanguage("English");
        bookStoreDTO.setPublishedDate(LocalDate.of(2021, 5, 20));
        bookStoreDTO.setPages(300);
        bookStoreDTO.setStock(10);
        bookStoreDTO.setPublisherDTO(publisherDTO);

        CategoryDTO categoryDTO1 = new CategoryDTO();
        categoryDTO1.setId(1);
        categoryDTO1.setCategoryName("Fiction");

        CategoryDTO categoryDTO2 = new CategoryDTO();
        categoryDTO2.setId(2);
        categoryDTO2.setCategoryName("Adventure");

        // Mocking repository and mapper behaviors
        when(bookRepo.findById(bookId)).thenReturn(Optional.of(book));
        when(entityMapper.bookStoreToBookStoreDTO(book)).thenReturn(bookStoreDTO);
        when(entityMapper.publisherToPublisherDTO(book.getPublisher())).thenReturn(publisherDTO);

        // Act
        BookStoreDTO result = bookStoreService.getBook(bookId);

        // Assert
        assertNotNull(result);
        assertEquals(bookId, result.getId());
        assertEquals("Test Book", result.getBookName());
        assertEquals("Author", result.getAuthorName());
        assertEquals(2021, result.getYear());
        assertEquals(299.99, result.getPrice());
        assertEquals("Fantasy", result.getGenre());
        assertEquals("English", result.getLanguage());
        assertEquals(LocalDate.of(2021, 5, 20), result.getPublishedDate());
        assertEquals(300, result.getPages());
        assertEquals(10, result.getStock());
        assertEquals(publisherDTO, result.getPublisherDTO());
        assertEquals(2, result.getCategoriesDTO().size());

        verify(bookRepo, times(1)).findById(1); 
        verify(bookRepo).findById(bookId); // Check if findById() was called
        verify(entityMapper).bookStoreToBookStoreDTO(book); // Check if mapper was called
        verify(entityMapper).publisherToPublisherDTO(book.getPublisher()); // Check publisher mapping
    }

    @Test
    void testGetBook_NotFound() {
        // Arrange
        int bookId = 99;

        // Mocking repository behavior
        when(bookRepo.findById(bookId)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> bookStoreService.getBook(bookId));
        assertEquals("602", exception.getErrorCode());
        assertEquals("Given book id does not found", exception.getErrorDisc());
    }

    @Test
    void testGetBookId_Sucsess(){
        // Arrange
        int bookId = 1;

        // Mocking the book entity
        Publisher publisher = new Publisher();
        publisher.setId(1);
        publisher.setPublisherName("Test Publisher");

        Category category1 = new Category();
        category1.setId(1);
        category1.setCategoryName("Fiction");

        Category category2 = new Category();
        category2.setId(2);
        category2.setCategoryName("Adventure");

        BookStore book = new BookStore();
        book.setId(bookId);
        book.setBookName("Test Book");
        book.setAuthorName("Author");
        book.setYear(2021);
        book.setPrice(299.99);
        book.setGenre("Fantasy");
        book.setLanguage("English");
        book.setPublishedDate(LocalDate.of(2021, 5, 20));
        book.setPages(300);
        book.setStock(10);
        book.setPublisher(publisher);
        book.setCategories(Set.of(category1, category2));

        when(bookRepo.findById(bookId)).thenReturn(Optional.of(book));

        BookStore result = bookStoreService.getBookEntityById(bookId);

        assertNotNull(result);
        assertEquals(bookId, result.getId());
        assertEquals("Test Book", result.getBookName());
        assertEquals("Author", result.getAuthorName());
        assertEquals(2021, result.getYear());
        assertEquals(299.99, result.getPrice());
        assertEquals("Fantasy", result.getGenre());
        assertEquals("English", result.getLanguage());
        assertEquals(LocalDate.of(2021, 5, 20), result.getPublishedDate());
        assertEquals(300, result.getPages());
        assertEquals(10, result.getStock());

        verify(bookRepo).findById(bookId); // Check if findById() was called
    }

    @Test
    void testUpdataBook(){
        int bookId = 1;

        BookStore book = new BookStore();
        book.setId(bookId);
        book.setPrice(299.99);
        book.setStock(10);

        BookStoreDTO bookStoreDTO = new BookStoreDTO();
        bookStoreDTO.setId(bookId);
        bookStoreDTO.setPrice(399.99);
        bookStoreDTO.setStock(10);

        when(bookRepo.findById(bookId)).thenReturn(Optional.of(book));
        when(bookRepo.save(book)).thenAnswer(invocation -> invocation.getArgument(0));

        when(entityMapper.bookStoreToBookStoreDTO(book)).thenReturn(bookStoreDTO);
        when(entityMapper.publisherToPublisherDTO(any())).thenReturn(new PublisherDTO());

        BookStoreDTO result = bookStoreService.updateBook(bookId, bookStoreDTO);

        assertEquals(399.99, result.getPrice());
    }
}

