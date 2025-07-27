package com.example.poc.repository;

import com.example.poc.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    
    // Find items by name containing a string (case-insensitive)
    List<Item> findByNameContainingIgnoreCase(String name);
    
    // Find items by description containing a string (case-insensitive)
    List<Item> findByDescriptionContainingIgnoreCase(String description);
    
    // Custom query to search by name or description
    @Query("SELECT i FROM Item i WHERE " +
           "LOWER(i.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(i.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Item> findByNameOrDescriptionContaining(@Param("searchTerm") String searchTerm);
    
    // Paginated search
    @Query("SELECT i FROM Item i WHERE " +
           "LOWER(i.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(i.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Item> findByNameOrDescriptionContaining(@Param("searchTerm") String searchTerm, Pageable pageable);
}