package com.example.demo.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Publisher {
    
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull(message = "Publisher name cant be null")
    private String publisherName;

     /* The JsonManagedReference and JsonBackReference(used in BookStore entity for the publisher field) 
     *  are used to avoid infinte recursion mapping in JSON response. Infinite recursion mapping 
     * happens because of bidirectional relationship between the Publisher and BookStore entity.
     * 
     * JsonManagedReference have to be used with parent entity.
     * JsonBackReference have to be used with child entity.
     */
    @OneToMany(mappedBy = "publisher" , cascade = CascadeType.PERSIST) 
    @JsonBackReference
    //The Publisher table does not store a list of books directly; 
    //this is handled by the BookStore table using the foreign key.

    // the main reason for adding the list of BookStore entry is to take advantage of OOP and Bidirectional navigation.
    private List<BookStore> books;
}
