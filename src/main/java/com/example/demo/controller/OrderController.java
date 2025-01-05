package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.OrderDTO;
import com.example.demo.service.OrderService;

@RestController
@RequestMapping("/order")
public class OrderController {
    
    @Autowired private OrderService orderService;
    
    @PostMapping("/place")
    public ResponseEntity<?> placeOrder(@RequestBody OrderDTO orderDTO){
        return new ResponseEntity<>(orderService.placeOrder(orderDTO),HttpStatus.OK);
    }
}
