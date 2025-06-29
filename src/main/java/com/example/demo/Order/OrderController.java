package com.example.demo.Order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {
    
    private OrderService orderService;
    private Logger logger = LoggerFactory.getLogger(OrderController.class);

    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }
    
    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody OrderDTO orderDTO,HttpServletRequest request){
        logger.trace("placeOrder Controller called");
        return new ResponseEntity<>(orderService.placeOrder(orderDTO,request),HttpStatus.OK);
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
