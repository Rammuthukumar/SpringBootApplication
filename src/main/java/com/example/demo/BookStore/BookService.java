package com.example.demo.BookStore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Category.Category;
import com.example.demo.Category.CategoryDTO;
import com.example.demo.Category.CategoryRepo;
import com.example.demo.Publisher.Publisher;
import com.example.demo.Publisher.PublisherRepo;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.EntityMapper;

@Service
public class BookService {

    private BookRepo bookRepo;
    private PublisherRepo publisherRepo;
    private CategoryRepo categoryRepo;
    private EntityMapper entityMapper;

    public BookService(BookRepo bookRepo, PublisherRepo publisherRepo, CategoryRepo categoryRepo,EntityMapper entityMapper){
        this.bookRepo = bookRepo;
        this.publisherRepo = publisherRepo;
        this.categoryRepo = categoryRepo;
        this.entityMapper = entityMapper;
    }

    private List<BookStoreDTO> bookStoreList = new ArrayList<>();
    private Set<CategoryDTO> savedCategoriesList = new HashSet<>();

    public List<BookStoreDTO> getAllBooks(Pageable pageable){
        System.out.println(pageable);
        Page<BookStore> books = bookRepo.findAll(pageable);
        System.out.println(books);

        if(books.isEmpty()){
            throw new BusinessException("601","Repo is Empty, Please add some data");
        }

        for(BookStore book : books){
            System.out.println("IN LOOP");
            System.out.println(book);
            BookStoreDTO bookStoreDTO = entityMapper.bookStoreToBookStoreDTO(book);
            bookStoreDTO.setPublisherDTO(entityMapper.publisherToPublisherDTO(book.getPublisher()));

            for(Category category : book.getCategories()){
                System.out.println(book.getCategories());
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
 
    /*
     * BUG : When fetching the book from db it doesnt properly fetchs the category details from the database.
     * it stores empty set. But when fetching all the books it properly populates the category fiels.
     *
     *  Tried All the steps i know (ChatGPT tells) :-
     *     1) ADDED @Transactional ANNOTATION.
     *     2) USED Hibernate.intialize(book.getCategories()).
     *     3) DEBUGGED WITH PRINTING THE VALUE OF book.getCategories().
     *     4) ADDED fetch = FetchType.EAGER / LAZY IN BOTH THE ENTITY CLASSES , ALSO TRIED WITH ONE CLASS AT A TIME.
     * 
     * ALSO RAN THE QUERY SUCH AS :- 
     *     1) select * from book_category where book_id = 16;
     *     2) SELECT bs.id, bs.book_name, c.id, c.category_name FROM book_store bs
                LEFT JOIN book_category bc ON bs.id = bc.book_id
                LEFT JOIN category c ON c.id = bc.category_id
                WHERE bs.id = 16;

        IT FETCHS THE DATA PROPERLY.

        ANYONE SAW THIS MESSAGE, AND IF YOU KNOW HOW TO FIX THIS BUG , KINDLY HELP!!!!


        FIXED.

        JUST REMOVED @Data by replacing @Getter @Setter, because when loading the child data from the db,
        it tries to access the toString() to serialize the category obj which eventaullay leads to infinite 
        cyclic access of data.
     */
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

    public BookStore getBookEntityById(int id){
        return bookRepo.findById(id).orElseThrow(
            () -> new BusinessException("602","Given book is not found in db")
        );
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

    public BookStore addCategory(int bookId, int catId) {
        Set<Category> categories = null;

        BookStore book = bookRepo.findById(bookId).get();
        Category category = categoryRepo.findById(catId).get();

        categories = book.getCategories();
        categories.add(category);

        book.setCategories(categories);
        System.out.println(bookRepo.save(book));

        return book;
    }

}
