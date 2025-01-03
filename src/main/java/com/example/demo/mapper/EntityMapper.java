package com.example.demo.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.DTO.BookStoreDTO;
import com.example.demo.model.BookStore;

@Component
public class EntityMapper {

    @Autowired private ModelMapper modelMapper;

    public BookStoreDTO bookStoreToBookStoreDTO(BookStore bookStore){
        BookStoreDTO bookStoreDTO = modelMapper.map(bookStore,BookStoreDTO.class);
        return bookStoreDTO;
    }

    public BookStore bookStoreDTOtoBookStore(BookStoreDTO bookStoreDTO){
        BookStore bookStore = modelMapper.map(bookStoreDTO,BookStore.class);
        return bookStore;
    } 
}
