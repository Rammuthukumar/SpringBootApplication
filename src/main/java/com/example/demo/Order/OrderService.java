package com.example.demo.Order;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    
    // private List<OrderResponseDTO> ordersList = new ArrayList<>();
    private Logger logger = LoggerFactory.getLogger(OrderService.class);

    private String getUserNameFromToken(HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");
        return jwtService.extractUserName(authHeader.substring(7));
    }
    
    public OrderResponseDTO placeOrder(OrderDTO orderDTO,HttpServletRequest request){
        logger.trace("placeOrder service method called");
        // Fetching user details to get the orders of the user
        User user = userRepo.findByUsername(getUserNameFromToken(request));
        List<Order> orders = orderRepo.findByUser_Username(user.getUsername());


        //If order already exists, call the updateOrder method, no need to enter a new order for the same book.
        for(Order order : orders){
            if(order.getBook().getId() == orderDTO.getBookId()){
                logger.trace("Order already exists in the db");
                logger.trace("Calling the update order service method");
                orderDTO.setQuantity(order.getQuantity()+1);
                return updateOrder(orderDTO, request);
            } 
        }

        // Fetching Book.
        BookStore book =  bookService.getBookEntityById(orderDTO.getBookId());

        //Updating stock quantity.
        book.setStock(book.getStock() - 1);
        BookStore savedBook = bookRepo.save(book);
        logger.trace("Updated book quanity in database");

        Order order = new Order();
        order.setUser(user);
        order.setBook(savedBook); 
        order.setQuantity(1); // Always 1 for new order
        order.setTotalPrice(savedBook.getPrice() * 1);

        //Saving the order in db.
        logger.trace("placing the order...");
        Order savedOrder = orderRepo.save(order);
        logger.trace("order saved in database");

        OrderResponseDTO orderResponse = new OrderResponseDTO();
        BookResponseDTO bookResponse = new BookResponseDTO();
            
        //Mapping entity to dto.
        bookResponse.setId(savedOrder.getBook().getId());
        bookResponse.setBookName(savedOrder.getBook().getBookName());
        bookResponse.setAuthorName(savedOrder.getBook().getAuthorName());

        orderResponse.setBookResponse(bookResponse);
        orderResponse.setId(savedOrder.getId());
        orderResponse.setUserName(savedOrder.getUser().getUsername());
        orderResponse.setQuantity(savedOrder.getQuantity());
        orderResponse.setTotalPrice(savedOrder.getTotalPrice());
        
        return orderResponse;
    }

    public List<OrderResponseDTO> getOrders(){
        List<Order> orders = orderRepo.findAll();
        
        if(orders.isEmpty())
            throw new BusinessException("803","Cant find Order list for the given id");

        List<OrderResponseDTO> orderResponseList = new ArrayList<>(); 
    
        orders.forEach(order->{
            OrderResponseDTO orderResponse = new OrderResponseDTO();
            BookResponseDTO bookResponse = new BookResponseDTO();
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
        logger.trace("getting user orders...");
        //Fetching the user orders
        String username = getUserNameFromToken(request);
        List<Order> orders = orderRepo.findByUser_Username(username);

        if(orders.isEmpty())
            throw new BusinessException("803","Cant find Order list for the given id");

        List<OrderResponseDTO> orderResponseList = new ArrayList<>();

        orders.forEach(order->{
            BookResponseDTO bookResponse = new BookResponseDTO();
            OrderResponseDTO orderResponse = new OrderResponseDTO();
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
        logger.trace("updating the order");
        User user = userRepo.findByUsername(getUserNameFromToken(request));
        List<Order> orders = orderRepo.findByUser_Username(user.getUsername());

        Order matchingOrder = orders.stream()
        .filter(order -> order.getBook().getId() == orderDTO.getBookId())
        .findFirst()
        .orElseThrow(() -> new BusinessException("806", "Order not found for the given book ID"));
    

        BookStore book = matchingOrder.getBook();
        int newQuantity =  orderDTO.getQuantity();

        if(newQuantity > book.getStock()){
            throw new BusinessException("805","Insufficient stock");
        }
        
        // Updating the stock value of book based on the new quantity value.
        if(matchingOrder.getQuantity() >= newQuantity) 
            book.setStock(book.getStock() + (matchingOrder.getQuantity() - newQuantity));
        else 
            book.setStock(book.getStock() - (matchingOrder.getQuantity() - newQuantity));
        
        bookRepo.save(book);

        matchingOrder.setQuantity(newQuantity);
        matchingOrder.setTotalPrice(newQuantity * book.getPrice());

        logger.trace("Saving the updated order in db");
        Order savedOrder = orderRepo.save(matchingOrder);

        OrderResponseDTO orderResponse = new OrderResponseDTO();
        BookResponseDTO bookResponse = new BookResponseDTO();

        orderResponse.setId(savedOrder.getId());

        bookResponse.setId(book.getId());
        bookResponse.setBookName(book.getBookName());
        bookResponse.setAuthorName(book.getAuthorName());

        orderResponse.setBookResponse(bookResponse);

        orderResponse.setUserName(getUserNameFromToken(request));
        orderResponse.setTotalPrice(matchingOrder.getTotalPrice());
        orderResponse.setQuantity(matchingOrder.getQuantity());

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
        logger.trace("Cancelling the order in db");
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
