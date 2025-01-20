package com.example.demo.Order;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.BookStore.BookRepo;
import com.example.demo.BookStore.BookResponseDTO;
import com.example.demo.BookStore.BookService;
import com.example.demo.BookStore.BookStore;
import com.example.demo.User.User;
import com.example.demo.User.UserRepo;
import com.example.demo.config.JwtService;
import com.example.demo.exception.BusinessException;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class OrderService {
    
    private BookService bookService;
    private OrderRepo orderRepo;
    private UserRepo userRepo;
    private JwtService jwtService;
    private BookRepo bookRepo;

    public OrderService(BookService bookService,OrderRepo orderRepo,
                        UserRepo userRepo,JwtService jwtService,
                        BookRepo bookRepo){
        this.bookService = bookService;
        this.orderRepo = orderRepo;
        this.userRepo = userRepo;
        this.jwtService = jwtService;
        this.bookRepo = bookRepo;
    }
    
    private List<OrderResponseDTO> ordersList = new ArrayList<>();
    private OrderResponseDTO orderResponse = new OrderResponseDTO();

    private String getUserNameFromToken(HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");
        return jwtService.extractUserName(authHeader.substring(7));
    }
    
    public List<OrderResponseDTO> placeOrder(List<OrderDTO> ordersDTO,HttpServletRequest request){

        BookResponseDTO bookResponse = new BookResponseDTO();

        ordersDTO.forEach(orderDTO->{
            // Fetching Book.
            BookStore book =  bookService.getBookEntityById(orderDTO.getBookId());

            //Checking the ordered Quantity is avaliable in stock.
            if(book.getStock() < orderDTO.getQuantity()) 
                throw new BusinessException("801","Insufficent Stock");

            // Fetching user.
            User user = userRepo.findByUsername(getUserNameFromToken(request));

            //Updating stock quantity.
            book.setStock(book.getStock() - orderDTO.getQuantity());

            BookStore savedBook = bookRepo.save(book);

            Order order = new Order();
            order.setUser(user);
            order.setBook(savedBook); 
            order.setQuantity(orderDTO.getQuantity());
            order.setTotalPrice(savedBook.getPrice() * orderDTO.getQuantity());

            //Saving the order in db.
            Order savedOrder = orderRepo.save(order);
            
            //Mapping entity to dto.
            orderResponse.setId(savedOrder.getId());

            bookResponse.setId(savedOrder.getBook().getId());
            bookResponse.setBookName(savedOrder.getBook().getBookName());
            bookResponse.setAuthorName(savedOrder.getBook().getAuthorName());

            orderResponse.setBookResponse(bookResponse);

            orderResponse.setUserName(savedOrder.getUser().getUsername());
            orderResponse.setQuantity(savedOrder.getQuantity());
            orderResponse.setTotalPrice(savedOrder.getTotalPrice());
            
            //adding the dto obj to list.
            ordersList.add(orderResponse);
            
        });

        return ordersList;
    }

    public List<OrderResponseDTO> getOrders(){
        List<Order> orders = orderRepo.findAll();
        
        if(orders.isEmpty())
            throw new BusinessException("803","Cant find Order list for the given id");

        List<OrderResponseDTO> orderResponseList = new ArrayList<>();

        BookResponseDTO bookResponse = new BookResponseDTO();
    
        orders.forEach(order->{
            orderResponse.setId(order.getId());

            bookResponse.setId(order.getBook().getId());
            bookResponse.setBookName(order.getBook().getBookName());
            bookResponse.setAuthorName(order.getBook().getAuthorName());

            orderResponse.setBookResponse(bookResponse);
            orderResponse.setUserName(order.getUser().getUsername());
            orderResponse.setQuantity(order.getQuantity());
            orderResponse.setTotalPrice(order.getTotalPrice());

            orderResponseList.add(orderResponse);
        });

        return orderResponseList;
    }

    public List<OrderResponseDTO> getUserOrders(HttpServletRequest request){
        String username = getUserNameFromToken(request);
        List<Order> orders = orderRepo.findByUser_Username(username);
        
        if(orders.isEmpty())
            throw new BusinessException("803","Cant find Order list for the given id");

        List<OrderResponseDTO> orderResponseList = new ArrayList<>();

        BookResponseDTO bookResponse = new BookResponseDTO();
    
        orders.forEach(order->{
            orderResponse.setId(order.getId());

            bookResponse.setId(order.getBook().getId());
            bookResponse.setBookName(order.getBook().getBookName());
            bookResponse.setAuthorName(order.getBook().getAuthorName());

            orderResponse.setBookResponse(bookResponse);
            orderResponse.setUserName(username);
            orderResponse.setQuantity(order.getQuantity());
            orderResponse.setTotalPrice(order.getTotalPrice());

            orderResponseList.add(orderResponse);
        });

        return orderResponseList;
    }

    public OrderResponseDTO updateOrder(OrderDTO orderDTO,HttpServletRequest request){
        Order order = orderRepo.findById(orderDTO.getId()).orElseThrow(
            () -> new BusinessException("804","Cant find the Order for the given id")
        );

        if(!order.getUser().getUsername().equals(getUserNameFromToken(request))){
            throw new BusinessException("808","You dont have access to change the order details");
        }

        BookStore book = order.getBook();
        int newQuantity = orderDTO.getQuantity();

        if(orderDTO.getQuantity() > book.getStock()){
            throw new BusinessException("805","Insufficient stock");
        }
        
        // Updating the stock value of book based on the new quantity value.
        if(order.getQuantity() >= newQuantity) 
            book.setStock(book.getStock() + (order.getQuantity() - newQuantity));
        else 
            book.setStock(book.getStock() - (order.getQuantity() - newQuantity));
        
        bookRepo.save(book);

        order.setQuantity(newQuantity);
        order.setTotalPrice(newQuantity * book.getPrice());

        Order savedOrder = orderRepo.save(order);
        OrderResponseDTO orderResponse = new OrderResponseDTO();
        BookResponseDTO bookResponse = new BookResponseDTO();

        orderResponse.setId(savedOrder.getId());

        bookResponse.setId(book.getId());
        bookResponse.setBookName(book.getBookName());
        bookResponse.setAuthorName(book.getAuthorName());

        orderResponse.setBookResponse(bookResponse);

        orderResponse.setUserName(getUserNameFromToken(request));
        orderResponse.setTotalPrice(order.getTotalPrice());
        orderResponse.setQuantity(order.getQuantity());

        return orderResponse;
    }

    /* public void cancelOrder(int orderId,HttpServletRequest request){
        List<OrderResponseDTO> userOrders = getUserOrders(request);
        boolean orderExists = true;

        for(OrderResponseDTO order : userOrders){
            if(orderId == order.getId()) orderExists = false;
        }

        if(!orderExists){
            throw new BusinessException("806","Cant find order for the given id");
        }
        orderRepo.deleteById(orderId);
    } */

    public void cancelOrder(int orderId, HttpServletRequest request) {
        // Fetch user orders
        List<OrderResponseDTO> userOrders = getUserOrders(request);
    
        // Check if the order exists using anyMatch for better readability
        boolean orderExists = userOrders.stream()
                                        .anyMatch(order -> orderId == order.getId());
    
        // Throw exception if order does not exist
        if (!orderExists) {
            throw new BusinessException("806", "Cannot find order for the given ID");
        }
    
        // Delete the order
        orderRepo.deleteById(orderId);
    }
    
}
