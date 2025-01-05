package com.example.demo.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.dto.BookStoreDTO;
import com.example.demo.dto.PublisherDTO;
import com.example.demo.model.BookStore;
import com.example.demo.model.Publisher;

@Component
public class EntityMapper {

    @Autowired private ModelMapper modelMapper;

    public BookStoreDTO bookStoreToBookStoreDTO(BookStore bookStore){
        return modelMapper.map(bookStore,BookStoreDTO.class);
    }

    public BookStore bookStoreDTOtoBookStore(BookStoreDTO bookStoreDTO){
        return modelMapper.map(bookStoreDTO,BookStore.class); 
    } 

    public PublisherDTO publisherToPublisherDTO(Publisher publisher){
        return modelMapper.map(publisher,PublisherDTO.class);
    }

    public Publisher publisherDTOtoPublisher(PublisherDTO publisherDTO){
        return modelMapper.map(publisherDTO,Publisher.class);
    }
}
