package com.example.demo.utils;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.example.demo.BookStore.BookStore;
import com.example.demo.BookStore.BookStoreDTO;
import com.example.demo.Publisher.Publisher;
import com.example.demo.Publisher.PublisherDTO;

@Component
public class EntityMapper {

    private ModelMapper modelMapper;

    public EntityMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

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
