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

    public OrderService(BookRepo bookRepo, OrderRepo orderRepo, UserRepo userRepo){
        this.bookRepo = bookRepo;
        this.orderRepo = orderRepo;
        this.userRepo = userRepo;
    }
    
    //private List<Order> orders = new ArrayList<>();
    
    public Order placeOrder(OrderDTO orderDTO){
        System.out.println(orderDTO.getBookId());
        
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
        order.setTotalPrice(orderDTO.getTotalPrice());

        return orderRepo.save(order);
    }
}
