package com.example.demo.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Order;

@Repository
public interface OrderRepo extends JpaRepository<Order,Integer>{
    List<Order> findAllByUserId(int userId);
}
