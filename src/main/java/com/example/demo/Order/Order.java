package com.example.demo.Order;

import com.example.demo.BookStore.BookStore;
import com.example.demo.User.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
@Table(name = "orders")
public class Order {
    
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "book_id", referencedColumnName = "id" )
    private BookStore book;

    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username" )
    private User user;

    @Min(1)
    private int quantity;
    
    private double totalPrice;
  //  private String orderStatus;

}
