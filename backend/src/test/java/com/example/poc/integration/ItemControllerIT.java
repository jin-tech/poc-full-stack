import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import com.example.poc.controller.ItemController;
import com.example.poc.model.Item;
import com.example.poc.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

@WebMvcTest(ItemController.class)
public class ItemControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemService itemService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ItemController(itemService)).build();
    }

    @Test
    public void testGetAllItems() throws Exception {
        List<Item> items = Arrays.asList(new Item(1L, "Item1", "Description1"),
                                          new Item(2L, "Item2", "Description2"));
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
        Item item = new Item(1L, "Item1", "Description1");
        when(itemService.findById(1L)).thenReturn(item);

        mockMvc.perform(get("/api/items/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Item1")));
    }

    @Test
    public void testCreateItem() throws Exception {
        Item item = new Item(null, "NewItem", "NewDescription");
        when(itemService.save(any(Item.class))).thenReturn(new Item(1L, "NewItem", "NewDescription"));

        mockMvc.perform(post("/api/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"NewItem\",\"description\":\"NewDescription\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("NewItem")));
    }

    @Test
    public void testUpdateItem() throws Exception {
        Item updatedItem = new Item(1L, "UpdatedItem", "UpdatedDescription");
        when(itemService.update(anyLong(), any(Item.class))).thenReturn(updatedItem);

        mockMvc.perform(put("/api/items/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"UpdatedItem\",\"description\":\"UpdatedDescription\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("UpdatedItem")));
    }

    @Test
    public void testDeleteItem() throws Exception {
        doNothing().when(itemService).delete(1L);

        mockMvc.perform(delete("/api/items/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}