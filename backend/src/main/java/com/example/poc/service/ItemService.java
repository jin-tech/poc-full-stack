package com.example.poc.service;

import com.example.poc.exception.ResourceNotFoundException;
import com.example.poc.model.Item;
import com.example.poc.repository.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ItemService {

    private static final Logger logger = LoggerFactory.getLogger(ItemService.class);
    private final ItemRepository itemRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Transactional(readOnly = true)
    public List<Item> findAll() {
        logger.info("Fetching all items");
        return itemRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Item> findAll(Pageable pageable) {
        logger.info("Fetching items with pagination: {}", pageable);
        return itemRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Item findById(Long id) {
        logger.info("Fetching item with id: {}", id);
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item", "id", id));
    }

    @Transactional(readOnly = true)
    public List<Item> search(String searchTerm) {
        logger.info("Searching items with term: {}", searchTerm);
        return itemRepository.findByNameOrDescriptionContaining(searchTerm);
    }

    @Transactional(readOnly = true)
    public Page<Item> search(String searchTerm, Pageable pageable) {
        logger.info("Searching items with term: {} and pagination: {}", searchTerm, pageable);
        return itemRepository.findByNameOrDescriptionContaining(searchTerm, pageable);
    }

    public Item save(Item item) {
        logger.info("Creating new item: {}", item.getName());
        return itemRepository.save(item);
    }

    public Item update(Long id, Item itemDetails) {
        logger.info("Updating item with id: {}", id);
        Item existingItem = findById(id); // This will throw exception if not found
        
        existingItem.setName(itemDetails.getName());
        existingItem.setDescription(itemDetails.getDescription());
        
        Item updatedItem = itemRepository.save(existingItem);
        logger.info("Successfully updated item with id: {}", id);
        return updatedItem;
    }

    public void delete(Long id) {
        logger.info("Deleting item with id: {}", id);
        Item item = findById(id); // This will throw exception if not found
        itemRepository.delete(item);
        logger.info("Successfully deleted item with id: {}", id);
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return itemRepository.existsById(id);
    }
}