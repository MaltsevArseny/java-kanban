package ru.practicum.shareit.Item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = new User(1L, "Owner", "owner@test.com");
        item = new Item(1L, "Drill", "Powerful drill", true, owner);
    }

    @Test
    void createItem_ValidItem_ShouldReturnSavedItem() {
        when(userService.getUserById(1L)).thenReturn(owner);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Item createdItem = itemService.createItem(item, 1L);

        assertNotNull(createdItem);
        assertEquals("Drill", createdItem.getName());
        assertEquals(owner, createdItem.getOwner());
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void createItem_InvalidUser_ShouldThrowException() {
        when(userService.getUserById(999L)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.createItem(item, 999L));
        verify(itemRepository, never()).save(any());
    }

    @Test
    void getItemById_ExistingItem_ShouldReturnItem() {
        when(itemRepository.findById(1L)).thenReturn(item);

        Item foundItem = itemService.getItemById(1L);

        assertNotNull(foundItem);
        assertEquals(1L, foundItem.getId());
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void getItemById_NonExistingItem_ShouldThrowException() {
        when(itemRepository.findById(999L)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> itemService.getItemById(999L));
        verify(itemRepository, times(1)).findById(999L);
    }

    @Test
    void getItemsByOwnerId_ValidOwner_ShouldReturnItems() {
        when(userService.getUserById(1L)).thenReturn(owner);
        when(itemRepository.findByOwnerId(1L)).thenReturn(List.of(item));

        List<Item> items = itemService.getItemsByOwnerId(1L);

        assertEquals(1, items.size());
        verify(itemRepository, times(1)).findByOwnerId(1L);
    }

    @Test
    void updateItem_ValidUpdate_ShouldReturnUpdatedItem() {
        Item existingItem = new Item(1L, "Old Drill", "Old description", true, owner);
        Item updateData = new Item(null, "New Drill", "New description", false, null);

        when(itemRepository.findById(1L)).thenReturn(existingItem);
        when(itemRepository.update(any(Item.class))).thenReturn(existingItem);

        Item updatedItem = itemService.updateItem(1L, updateData, 1L);

        assertEquals("New Drill", updatedItem.getName());
        assertEquals("New description", updatedItem.getDescription());
        assertFalse(updatedItem.getAvailable());
        verify(itemRepository, times(1)).update(existingItem);
    }

    @Test
    void updateItem_NotOwner_ShouldThrowException() {
        when(itemRepository.findById(1L)).thenReturn(item);

        Item updateData = new Item(null, "New Drill", null, null, null);

        assertThrows(NotFoundException.class, () -> itemService.updateItem(1L, updateData, 999L));
        verify(itemRepository, never()).update(any());
    }

    @Test
    void searchItems_ValidText_ShouldReturnItems() {
        when(itemRepository.search("drill")).thenReturn(List.of(item));

        List<Item> foundItems = itemService.searchItems("drill");

        assertEquals(1, foundItems.size());
        verify(itemRepository, times(1)).search("drill");
    }

    @Test
    void searchItems_BlankText_ShouldReturnEmptyList() {
        List<Item> foundItems = itemService.searchItems("");

        assertTrue(foundItems.isEmpty());
        verify(itemRepository, never()).search(any());
    }
}