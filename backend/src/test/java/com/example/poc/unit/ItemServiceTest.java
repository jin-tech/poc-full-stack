package com.example.poc.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.poc.exception.ResourceNotFoundException;
import com.example.poc.model.Item;
import com.example.poc.repository.ItemRepository;
import com.example.poc.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class ItemServiceTest {

    @InjectMocks
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        Item item1 = new Item("Item 1", "Description 1");
        item1.setId(1L);
        Item item2 = new Item("Item 2", "Description 2");
        item2.setId(2L);
        
        when(itemRepository.findAll()).thenReturn(Arrays.asList(item1, item2));

        List<Item> items = itemService.findAll();

        assertEquals(2, items.size());
        assertEquals("Item 1", items.get(0).getName());
        assertEquals("Item 2", items.get(1).getName());
    }

    @Test
    void testFindById() {
        Item item = new Item("Test Item", "Test Description");
        item.setId(1L);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        Item result = itemService.findById(1L);

        assertEquals("Test Item", result.getName());
        assertEquals("Test Description", result.getDescription());
    }

    @Test
    void testFindByIdNotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> itemService.findById(1L));
    }

    @Test
    void testSave() {
        Item newItem = new Item("New Item", "New Description");
        Item savedItem = new Item("New Item", "New Description");
        savedItem.setId(1L);
        
        when(itemRepository.save(newItem)).thenReturn(savedItem);

        Item result = itemService.save(newItem);

        assertEquals("New Item", result.getName());
        assertEquals(1L, result.getId());
    }

    @Test
    void testUpdate() {
        Item existingItem = new Item("Old Item", "Old Description");
        existingItem.setId(1L);
        Item updatedItem = new Item("Updated Item", "Updated Description");
        
        when(itemRepository.findById(1L)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(ArgumentMatchers.any(Item.class))).thenReturn(existingItem);

        Item result = itemService.update(1L, updatedItem);

        assertEquals("Updated Item", result.getName());
        assertEquals("Updated Description", result.getDescription());
    }

    @Test
    void testDelete() {
        Item existingItem = new Item("Item to Delete", "Description");
        existingItem.setId(1L);
        
        when(itemRepository.findById(1L)).thenReturn(Optional.of(existingItem));

        assertDoesNotThrow(() -> itemService.delete(1L));
        
        verify(itemRepository, times(1)).delete(existingItem);
    }
}
