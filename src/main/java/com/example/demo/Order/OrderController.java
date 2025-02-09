package com.example.demo.Order;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/order")
public class OrderController {
    
    private OrderService orderService;

    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }
    
    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody List<OrderDTO> ordersDTO,HttpServletRequest request){
        return new ResponseEntity<>(orderService.placeOrder(ordersDTO,request),HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getOrders(){
        return new ResponseEntity<>(orderService.getOrders(),HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserOrders(HttpServletRequest request){
        return new ResponseEntity<>(orderService.getUserOrders(request),HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<?> updateOrder(@RequestBody OrderDTO orderDTO,HttpServletRequest request){
        return new ResponseEntity<>(orderService.updateOrder(orderDTO,request),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelOrder(@PathVariable int id,HttpServletRequest request){
        orderService.cancelOrder(id,request);
        return new ResponseEntity<>("Sucessfully Deleted",HttpStatus.OK);
    }
}
