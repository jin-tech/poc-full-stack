package com.example.poc.integration;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import com.example.poc.controller.ItemController;
import com.example.poc.model.Item;
import com.example.poc.service.ItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

@WebMvcTest(ItemController.class)
public class ItemControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllItems() throws Exception {
        Item item1 = new Item("Item1", "Description1");
        item1.setId(1L);
        Item item2 = new Item("Item2", "Description2");
        item2.setId(2L);
        
        List<Item> items = Arrays.asList(item1, item2);
        when(itemService.findAll()).thenReturn(items);

        mockMvc.perform(get("/api/items")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Item1")))
                .andExpect(jsonPath("$[1].name", is("Item2")));
    }

    @Test
    public void testGetItemById() throws Exception {
        Item item = new Item("Item1", "Description1");
        item.setId(1L);
        when(itemService.findById(1L)).thenReturn(item);

        mockMvc.perform(get("/api/items/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Item1")));
    }

    @Test
    public void testCreateItem() throws Exception {
        Item newItem = new Item("New Item", "New Description");
        Item savedItem = new Item("New Item", "New Description");
        savedItem.setId(1L);
        
        when(itemService.save(ArgumentMatchers.any(Item.class))).thenReturn(savedItem);

        mockMvc.perform(post("/api/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newItem)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("New Item")));
    }

    @Test
    public void testUpdateItem() throws Exception {
        Item existingItem = new Item("Updated Item", "Updated Description");
        existingItem.setId(1L);
        
        when(itemService.update(eq(1L), ArgumentMatchers.any(Item.class))).thenReturn(existingItem);

        mockMvc.perform(put("/api/items/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(existingItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Item")));
    }

    @Test
    public void testDeleteItem() throws Exception {
        doNothing().when(itemService).delete(1L);

        mockMvc.perform(delete("/api/items/1"))
                .andExpect(status().isNoContent());
    }
}
