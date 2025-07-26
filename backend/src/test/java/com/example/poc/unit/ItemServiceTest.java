import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.poc.model.Item;
import com.example.poc.repository.ItemRepository;
import com.example.poc.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
        Item item1 = new Item(1L, "Item 1", "Description 1");
        Item item2 = new Item(2L, "Item 2", "Description 2");
        when(itemRepository.findAll()).thenReturn(Arrays.asList(item1, item2));

        List<Item> items = itemService.findAll();

        assertEquals(2, items.size());
        assertEquals("Item 1", items.get(0).getName());
        assertEquals("Item 2", items.get(1).getName());
    }

    @Test
    void testFindById() {
        Item item = new Item(1L, "Item 1", "Description 1");
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        Item foundItem = itemService.findById(1L);

        assertNotNull(foundItem);
        assertEquals("Item 1", foundItem.getName());
    }

    @Test
    void testSave() {
        Item item = new Item(null, "Item 1", "Description 1");
        Item savedItem = new Item(1L, "Item 1", "Description 1");
        when(itemRepository.save(item)).thenReturn(savedItem);

        Item result = itemService.save(item);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testUpdate() {
        Item existingItem = new Item(1L, "Item 1", "Description 1");
        Item updatedItem = new Item(1L, "Updated Item", "Updated Description");
        when(itemRepository.findById(1L)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(updatedItem)).thenReturn(updatedItem);

        Item result = itemService.update(1L, updatedItem);

        assertNotNull(result);
        assertEquals("Updated Item", result.getName());
    }

    @Test
    void testDelete() {
        doNothing().when(itemRepository).deleteById(1L);

        assertDoesNotThrow(() -> itemService.delete(1L));
        verify(itemRepository, times(1)).deleteById(1L);
    }
}