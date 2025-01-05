package com.example.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Order;

@Repository
public interface OrderRepo extends JpaRepository<Order,Integer>{
    
}
