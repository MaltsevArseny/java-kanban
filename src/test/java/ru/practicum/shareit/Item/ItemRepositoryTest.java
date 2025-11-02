package ru.practicum.shareit.Item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ItemRepositoryTest {

    private ItemRepository itemRepository;
    private User owner;

    @BeforeEach
    void setUp() {
        itemRepository = new ItemRepository();
        owner = new User(1L, "Owner", "owner@test.com");
    }

    @Test
    void save_NewItem_ShouldAssignIdAndSave() {
        Item item = new Item();
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(owner);

        Item savedItem = itemRepository.save(item);

        assertNotNull(savedItem.getId());
        assertEquals("Drill", savedItem.getName());
        assertEquals(owner, savedItem.getOwner());
    }

    @Test
    void findById_ExistingItem_ShouldReturnItem() {
        Item item = new Item();
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(owner);
        Item savedItem = itemRepository.save(item);

        Item foundItem = itemRepository.findById(savedItem.getId());

        assertNotNull(foundItem);
        assertEquals(savedItem.getId(), foundItem.getId());
        assertEquals("Drill", foundItem.getName());
    }

    @Test
    void findByOwnerId_ValidOwner_ShouldReturnItems() {
        Item item1 = new Item();
        item1.setName("Drill");
        item1.setDescription("Powerful drill");
        item1.setAvailable(true);
        item1.setOwner(owner);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("Hammer");
        item2.setDescription("Heavy hammer");
        item2.setAvailable(true);
        item2.setOwner(owner);
        itemRepository.save(item2);

        List<Item> items = itemRepository.findByOwnerId(1L);

        assertEquals(2, items.size());
    }

    @Test
    void findByOwnerId_DifferentOwner_ShouldReturnEmptyList() {
        User otherOwner = new User(2L, "Other", "other@test.com");
        Item item = new Item();
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(otherOwner);
        itemRepository.save(item);

        List<Item> items = itemRepository.findByOwnerId(1L);

        assertTrue(items.isEmpty());
    }

    @Test
    void search_WithMatchingText_ShouldReturnItems() {
        Item item = new Item();
        item.setName("Power Drill");
        item.setDescription("Very powerful drill");
        item.setAvailable(true);
        item.setOwner(owner);
        itemRepository.save(item);

        List<Item> foundItems = itemRepository.search("drill");

        assertEquals(1, foundItems.size());
        assertEquals("Power Drill", foundItems.get(0).getName());
    }

    @Test
    void search_WithMatchingDescription_ShouldReturnItems() {
        Item item = new Item();
        item.setName("Tool");
        item.setDescription("Electric drill for construction");
        item.setAvailable(true);
        item.setOwner(owner);
        itemRepository.save(item);

        List<Item> foundItems = itemRepository.search("electric");

        assertEquals(1, foundItems.size());
        assertEquals("Tool", foundItems.get(0).getName());
    }

    @Test
    void search_WithBlankText_ShouldReturnEmptyList() {
        Item item = new Item();
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(owner);
        itemRepository.save(item);

        List<Item> foundItems = itemRepository.search("");

        assertTrue(foundItems.isEmpty());
    }

    @Test
    void search_UnavailableItem_ShouldNotReturnItem() {
        Item item = new Item();
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(false);
        item.setOwner(owner);
        itemRepository.save(item);

        List<Item> foundItems = itemRepository.search("drill");

        assertTrue(foundItems.isEmpty());
    }

    @Test
    void update_ExistingItem_ShouldUpdateItem() {
        Item item = new Item();
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(owner);
        Item savedItem = itemRepository.save(item);

        Item updateData = new Item();
        updateData.setId(savedItem.getId());
        updateData.setName("Updated Drill");
        updateData.setDescription("Updated description");
        updateData.setAvailable(false);
        updateData.setOwner(owner);

        Item updatedItem = itemRepository.update(updateData);

        assertEquals("Updated Drill", updatedItem.getName());
        assertEquals("Updated description", updatedItem.getDescription());
        assertFalse(updatedItem.getAvailable());
    }

    @Test
    void existsById_ExistingItem_ShouldReturnTrue() {
        Item item = new Item();
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(owner);
        Item savedItem = itemRepository.save(item);

        boolean exists = itemRepository.existsById(savedItem.getId());

        assertTrue(exists);
    }
}