package com.example.demo.Publisher;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.exception.BusinessException;
import com.example.demo.utils.EntityMapper;

@Service
public class PublisherService {
    
    private PublisherRepo repo;
    private EntityMapper entityMapper;

    public PublisherService(PublisherRepo repo,EntityMapper entityMapper){
        this.repo = repo;
        this.entityMapper = entityMapper;
    }

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

    public Publisher addPublisher(PublisherDTO publisherDTO){
        Publisher publisher = entityMapper.publisherDTOtoPublisher(publisherDTO);
        return repo.save(publisher);

    }

    public String deletePublisher(int id){
        if(!repo.existsById(id))
            throw new BusinessException("702","Cant find Publisher for the given id "+id);
        repo.deleteById(id);
        return "Successfully Deleted.";
    }
}
