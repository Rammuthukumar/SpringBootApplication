package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.OrderDTO;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.BookStore;
import com.example.demo.model.Order;
import com.example.demo.model.User;
import com.example.demo.repo.BookRepo;
//import com.example.demo.repo.UserRepo;
import com.example.demo.repo.OrderRepo;
import com.example.demo.repo.UserRepo;

@Service
public class OrderService {
    
    private BookRepo bookRepo;
    private OrderRepo orderRepo;
    private UserRepo userRepo;
    private BookService bookService;

    public OrderService(BookRepo bookRepo, OrderRepo orderRepo, UserRepo userRepo){
        this.bookRepo = bookRepo;
        this.orderRepo = orderRepo;
        this.userRepo = userRepo;
    }
    
    private List<Order> orders = new ArrayList<>();
    
    public List<Order> placeOrder(List<OrderDTO> ordersDTO){
        for(OrderDTO orderDTO : ordersDTO){
        
            BookStore book =  bookRepo.findById(orderDTO.getBookId()).orElseThrow(
                () -> new BusinessException("800","Cant find book in the database")
            );

            if(book.getStock() < orderDTO.getQuantity()) 
                throw new BusinessException("801","Insufficent Stock");

            User user = userRepo.findById(orderDTO.getUserId()).orElseThrow(
                () -> new BusinessException("802", "Cant find User in the database")
            );

            // orderRepo.save(entityMapper.orderToOrderDTO(orderDTO.))

            Order order = new Order();
            order.setUser(user);
            order.setBook(book);
            order.setQuantity(orderDTO.getQuantity());
            order.setTotalPrice(book.getPrice() * orderDTO.getQuantity());

            //After placing the order , Updating the stock of the book in db.
            book.setStock(book.getStock() - order.getQuantity());  
            bookRepo.save(book);

            //saving the order in db.
            orders.add(orderRepo.save(order));
        }

        return orders;
    }

    public List<Order> getOrders(int userId){
        System.out.println(userId);
        List<Order> orders = orderRepo.findAllByUserId(userId);
        if(orders.isEmpty())
            throw new BusinessException("803","Cant find Order list for the given user id");

        return orders;
    }

    public Order updateOrder(OrderDTO orderDTO){
        Order order = orderRepo.findById(orderDTO.getId()).orElseThrow(
            () -> new BusinessException("804","Cant find the Order for the given id")
        );

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

        return orderRepo.save(order);
    }
}
