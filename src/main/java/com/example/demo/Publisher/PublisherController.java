package com.example.demo.Publisher;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/publisher")
public class PublisherController {

    private PublisherService service;

    public PublisherController(PublisherService service){
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> getAllPublishers(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size 
    ){
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<Page<Publisher>>(service.getAllPublisher(pageable),HttpStatus.OK);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PublisherDTO> getPublisher(@PathVariable int id){
        return new ResponseEntity<>(service.getPublisher(id),HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> addPublisher(@Valid @RequestBody PublisherDTO publisherDTO){
        return new ResponseEntity<>(service.addPublisher(publisherDTO),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePublisher(@PathVariable int id){
        return new ResponseEntity<>(service.deletePublisher(id),HttpStatus.OK);
    }
}
