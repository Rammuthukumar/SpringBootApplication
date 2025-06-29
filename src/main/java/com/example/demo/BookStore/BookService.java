package com.example.demo.BookStore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.Category.Category;
import com.example.demo.Category.CategoryDTO;
import com.example.demo.Category.CategoryRepo;
import com.example.demo.Publisher.Publisher;
import com.example.demo.Publisher.PublisherRepo;
import com.example.demo.Publisher.PublisherService;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.utils.EntityMapper;

@Service
public class BookService {

    private BookRepo bookRepo;
    private PublisherRepo publisherRepo;
    private PublisherService publisherService;
    private CategoryRepo categoryRepo;
    private EntityMapper entityMapper;

    public BookService(BookRepo bookRepo, PublisherRepo publisherRepo, CategoryRepo categoryRepo,EntityMapper entityMapper, PublisherService publisherService){
        this.bookRepo = bookRepo;
        this.publisherRepo = publisherRepo;
        this.categoryRepo = categoryRepo;
        this.entityMapper = entityMapper;
        this.publisherService = publisherService;
    }

    private Set<CategoryDTO> savedCategoriesList = new HashSet<>();

    private static Logger logger = LoggerFactory.getLogger(BookService.class);

    @Cacheable(value="books",key="#pageable")
    public List<BookStoreDTO> getAllBooks(Pageable pageable){
        logger.trace("getAllBooks() : Mehod called");
        logger.trace("cache is empty,fetching data from database");
        Page<BookStore> books = bookRepo.findAll(pageable);

        List<BookStoreDTO> bookStore = new ArrayList<>();
        
        
        if(books.isEmpty()){
            throw new BusinessException("601","Repo is Empty, Please add some data");
        }

        for(BookStore book : books){
            Set<CategoryDTO> categoriesList = new HashSet<>();
            
            BookStoreDTO bookStoreDTO = entityMapper.bookStoreToBookStoreDTO(book);
            bookStoreDTO.setPublisherDTO(entityMapper.publisherToPublisherDTO(book.getPublisher()));

            for(Category category : book.getCategories()){
                CategoryDTO categoriesDto = new CategoryDTO();
                categoriesDto.setId(category.getId());
                categoriesDto.setCategoryName(category.getCategoryName());
                categoriesList.add(categoriesDto);
            }
            bookStoreDTO.setCategoriesDTO(categoriesList);
            bookStore.add(bookStoreDTO);
        }
        return bookStore;
    }

    // For storing the data as cache in redis, Whatever the object value we are returning it has to be 
    // implement Serializable interface.

    // CachePut - adding new key:value pair in the memory.
    @CachePut(value = "book", key = "#result.id")
    @Caching(evict = {
        @CacheEvict(value = "books", allEntries = true) })
    public BookStoreDTO addBook(BookStoreDTO bookStoreDTO) {
        logger.trace("addBook() service method called");

        //Getting publisher data from db by using publisherId.
        Optional<Publisher> optionalPublisher = publisherRepo.findByPublisherName(bookStoreDTO.getPublisherDTO().getPublisherName());
        Publisher publisher;
        if (optionalPublisher.isPresent()) {
            publisher = optionalPublisher.get();
        } else {
            logger.trace("Publisher data is not available in db");
            logger.trace("adding Publisher details...");
            publisher = publisherService.addPublisher(bookStoreDTO.getPublisherDTO());
        }

        Set<Category> categories = new HashSet<>();

        //Getting Categories data from db by using categoriesId.
        for(CategoryDTO categoryDTO : bookStoreDTO.getCategoriesDTO()){
            Optional<Category> optionalCategory = categoryRepo.findByCategoryName(categoryDTO.getCategoryName());
            Category category;
            if(optionalCategory.isPresent())
                category = optionalCategory.get();
            else{
                logger.trace("Category data is not available in db");
                logger.trace("adding Category details...");
                category = categoryRepo.save(new Category(categoryDTO.getCategoryName()));
            }
            
            categories.add(category);
        }

        // mapping dto to entity
        BookStore book = entityMapper.bookStoreDTOtoBookStore(bookStoreDTO);  
        book.setPublisher(publisher);
        book.setCategories(categories);
        
        logger.trace("saving book data in db",book);
        BookStore savedBook = bookRepo.save(book);
        logger.trace("book saved in db",savedBook);

        // mapping saved entity to dto
        bookStoreDTO = entityMapper.bookStoreToBookStoreDTO(savedBook);       
        bookStoreDTO.setPublisherDTO(entityMapper.publisherToPublisherDTO(savedBook.getPublisher()));

        Set<CategoryDTO> savedCategories = new HashSet<>();

        for(Category category : savedBook.getCategories()){
            CategoryDTO categoriesDto = new CategoryDTO();
            categoriesDto.setId(category.getId());
            categoriesDto.setCategoryName(category.getCategoryName());
            savedCategories.add(categoriesDto);
        }
        
        bookStoreDTO.setCategoriesDTO(savedCategories);

        logger.trace("saving book data in cache");
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
    
    //Cacheable : checks if the books is exist in the cache memory for the given id before executing the method.
    // if it is there it wont execute and return the book from the cache...
    // if not the method will execute and stores the returning value in cache...
    @Cacheable(value = "book", key = "#id")
    public BookStoreDTO getBook(int id) {
        logger.trace("getBook() service method is called");
        //fetching book from db by id.
        BookStore book = bookRepo.findById(id).orElseThrow(()->  
            new BusinessException("602", "Given book id does not found"));

        logger.trace("getBook() Method called");

        // mapping entity to dto.
        BookStoreDTO bookStoreDTO = entityMapper.bookStoreToBookStoreDTO(book);
        bookStoreDTO.setPublisherDTO(entityMapper.publisherToPublisherDTO(book.getPublisher()));

        Set<CategoryDTO> categoriesList = new HashSet<>();
        
        for(Category category : book.getCategories()){
            System.out.println(category);
            CategoryDTO categoriesDto = new CategoryDTO();
            categoriesDto.setId(category.getId());
            categoriesDto.setCategoryName(category.getCategoryName());
            categoriesList.add(categoriesDto);
        }
        
        bookStoreDTO.setCategoriesDTO(categoriesList);
        return bookStoreDTO;
    }

    public BookStore getBookEntityById(int id){
        return bookRepo.findById(id).orElseThrow(
            () -> new BusinessException("602","Given book is not found in db")
        );
    }
    
    @CachePut(value="book",key="#id")
    @CacheEvict(value = "books", allEntries = true)
    public BookStoreDTO updateBook(int id, BookStoreDTO bookDTO) {
        logger.trace("updatebook() service method called "+ id );
        BookStore matchingBook = bookRepo.findById(id).orElseThrow(() -> 
            new BusinessException("602","Given book id does not found in the database"));

        // updating book data
        matchingBook.setPrice(bookDTO.getPrice());
        matchingBook.setStock(bookDTO.getStock());
        matchingBook = bookRepo.save(matchingBook);
        logger.trace("updated book data saved in db");

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

    @CacheEvict(value = "books", allEntries = true)
    public String deleteBook(int id) {

        if(!bookRepo.existsById(id))
            throw new BusinessException("606","Given book id does not exist in the DataBase , please enter some valid id");

        bookRepo.deleteById(id);
        logger.trace("book deleted"+id);
        return "Sucessfully Deleted";
    }

    @CacheEvict(value="books",key="#pageable")
    public String deleteAllBook(){
        bookRepo.deleteAll();
        return "Sucessfully Deleted";
    }

    public List<BookStore> searchByAuthor(String authorName) {
        List<BookStore> books = bookRepo.findByAuthorNameContainingIgnoreCase(authorName);
        if(books.isEmpty())
            throw new ResourceNotFoundException("No Book is found for the given author name "+ authorName);
        return books;
    }

    public List<BookStore> searchByBook(String bookName){
        List<BookStore> books = bookRepo.findByBookNameContainingIgnoreCase(bookName);
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
