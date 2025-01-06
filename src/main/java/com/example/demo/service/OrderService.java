package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.BookResponseDTO;
import com.example.demo.dto.OrderDTO;
import com.example.demo.dto.OrderResponseDTO;
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
    
    private List<OrderResponseDTO> ordersList = new ArrayList<>();

    private OrderResponseDTO orderResponse = new OrderResponseDTO();
    
    public List<OrderResponseDTO> placeOrder(List<OrderDTO> ordersDTO){
        BookResponseDTO bookResponse = new BookResponseDTO();
        System.out.println("IN SERVICE LAYER");
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
           
            order.setQuantity(orderDTO.getQuantity());
            order.setTotalPrice(book.getPrice() * orderDTO.getQuantity());

            //After placing the order , Updating the stock of the book in db.
            book.setStock(book.getStock() - order.getQuantity());
            order.setBook(book);  
            System.out.println("Saving book");
            System.out.println(book);
           // BookStore savedBook = bookRepo.save(book);

            //saving the order in db.
            System.out.println("Saving order");
            System.out.println(order);
            Order savedOrder = orderRepo.save(order);
            System.out.println("saved");
            System.out.println(savedOrder);

            orderResponse.setId(savedOrder.getId());

            bookResponse.setId(savedOrder.getBook().getId());
            bookResponse.setBookName(savedOrder.getBook().getBookName());
            bookResponse.setAuthorName(savedOrder.getBook().getAuthorName());

            orderResponse.setBookResponse(bookResponse);

            orderResponse.setUserId(savedOrder.getUser().getId());
            orderResponse.setQuantity(savedOrder.getQuantity());
            orderResponse.setTotalPrice(savedOrder.getTotalPrice());
            
            ordersList.add(orderResponse);
            
        }

        return ordersList;
    }

    public List<OrderResponseDTO> getOrders(int userId){
        List<Order> orders = orderRepo.findAllByUserId(userId);
        if(orders.isEmpty())
            throw new BusinessException("803","Cant find Order list for the given user id");

        List<OrderResponseDTO> orderResponseList = new ArrayList<>();

        OrderResponseDTO orderResponse = new OrderResponseDTO();
        BookResponseDTO bookResponse = new BookResponseDTO();
    
        for(Order order : orders){
            orderResponse.setId(order.getId());

            bookResponse.setId(order.getBook().getId());
            bookResponse.setBookName(order.getBook().getBookName());
            bookResponse.setAuthorName(order.getBook().getAuthorName());

            orderResponse.setBookResponse(bookResponse);
            orderResponse.setUserId(userId);
            orderResponse.setQuantity(order.getQuantity());
            orderResponse.setTotalPrice(order.getTotalPrice());

            orderResponseList.add(orderResponse);
        }

        return orderResponseList;


    }

    public OrderResponseDTO updateOrder(OrderDTO orderDTO){
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

        Order savedOrder = orderRepo.save(order);
        OrderResponseDTO orderResponse = new OrderResponseDTO();
        BookResponseDTO bookResponse = new BookResponseDTO();

        orderResponse.setId(savedOrder.getId());

        bookResponse.setId(book.getId());
        bookResponse.setBookName(book.getBookName());
        bookResponse.setAuthorName(book.getAuthorName());

        orderResponse.setBookResponse(bookResponse);

        orderResponse.setUserId(orderDTO.getUserId());
        orderResponse.setTotalPrice(order.getTotalPrice());
        orderResponse.setQuantity(order.getQuantity());

        return orderResponse;
    }

    public String cancelOrder(int orderId){
        if(orderRepo.existsById(orderId)){
            throw new BusinessException("806","Cant find order for the given id");
        }

        orderRepo.deleteById(orderId);

        return "Successfully Deleted.";
    }
}
