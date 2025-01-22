package com.example.demo.Publisher;


import java.io.Serializable;

import lombok.Data;

@Data
public class PublisherDTO implements Serializable{
    private int id;
    private String publisherName;
}
