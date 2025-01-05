package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.dto.PublisherDTO;
import com.example.demo.exception.BusinessException;
import com.example.demo.mapper.EntityMapper;
import com.example.demo.model.Publisher;
import com.example.demo.repo.PublisherRepo;

@Service
public class PublisherService {
    
    @Autowired private PublisherRepo repo;
    @Autowired private EntityMapper entityMapper;

    public Page<Publisher> getAllPublisher(Pageable pageable){
        Page<Publisher> publishers = repo.findAll(pageable);
        if(publishers.isEmpty())
            throw new BusinessException("701","Publishers table is empty");
        return publishers;
    }
    
    public PublisherDTO getPublisher(int id){
        Publisher publisher = repo.findById(id).orElseThrow(
            () -> new BusinessException("702","Cant find Publisher for the given id "+ id)
        );

        return entityMapper.publisherToPublisherDTO(publisher);
    }

    public PublisherDTO addPublisher(PublisherDTO publisherDTO){
        Publisher publisher = entityMapper.publisherDTOtoPublisher(publisherDTO);
        return entityMapper.publisherToPublisherDTO(repo.save(publisher));

    }

    public String deletePublisher(int id){
        if(!repo.existsById(id))
            throw new BusinessException("702","Cant find Publisher for the given id "+id);
        repo.deleteById(id);
        return "Successfully Deleted.";
    }
}
