package com.example.demo.Order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepo extends JpaRepository<Order,Integer>{
 //   List<Order> findAllByUserId(int userId);

    List<Order> findByUser_Username(String username);
}
