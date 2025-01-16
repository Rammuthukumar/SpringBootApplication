package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.OrderDTO;
import com.example.demo.service.OrderService;

@RestController
@RequestMapping("/order")
public class OrderController {
    
    @Autowired private OrderService orderService;
    
    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody List<OrderDTO> ordersDTO){
        return new ResponseEntity<>(orderService.placeOrder(ordersDTO),HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getOrders(){
        return new ResponseEntity<>(orderService.getOrders(),HttpStatus.OK);
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getUserOrders(@PathVariable String username){
        return new ResponseEntity<>(orderService.getUserOrders(username),HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<?> updateOrder(@RequestBody OrderDTO orderDTO){
        return new ResponseEntity<>(orderService.updateOrder(orderDTO),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelOrder(@PathVariable int id){
        return new ResponseEntity<>(orderService.cancelOrder(id),HttpStatus.OK);
    }
}
