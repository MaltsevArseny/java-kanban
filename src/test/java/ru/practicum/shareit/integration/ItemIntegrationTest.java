package ru.practicum.shareit.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Test
    void createAndGetItem_ShouldWorkCorrectly() {
        User owner = createUser("Owner", "owner@test.com");
        Item item = createItem("Drill", "Powerful drill", true, owner.getId());

        Item foundItem = itemService.getItemById(item.getId());

        assertEquals(item.getId(), foundItem.getId());
        assertEquals("Drill", foundItem.getName());
    }

    @Test
    void getItemsByOwner_ShouldReturnOwnerItems() {
        User owner = createUser("Owner", "owner@test.com");
        createItem("Drill", "Powerful drill", true, owner.getId());
        createItem("Hammer", "Heavy hammer", true, owner.getId());

        List<Item> ownerItems = itemService.getItemsByOwnerId(owner.getId());

        assertEquals(2, ownerItems.size());
    }

    @Test
    void searchItems_ShouldFindByText() {
        User owner = createUser("Owner", "owner@test.com");
        createItem("Power Drill", "Very powerful", true, owner.getId());
        createItem("Hammer", "Heavy tool", true, owner.getId());

        List<Item> foundItems = itemService.searchItems("drill");

        assertEquals(1, foundItems.size());
        assertEquals("Power Drill", foundItems.get(0).getName());
    }

    @Test
    void updateItem_ShouldModifyItem() {
        User owner = createUser("Owner", "owner@test.com");
        Item item = createItem("Old Drill", "Old description", true, owner.getId());

        Item updateData = new Item();
        updateData.setName("New Drill");
        updateData.setDescription("New description");
        updateData.setAvailable(false);

        Item updatedItem = itemService.updateItem(item.getId(), updateData, owner.getId());

        assertEquals("New Drill", updatedItem.getName());
        assertEquals("New description", updatedItem.getDescription());
        assertFalse(updatedItem.getAvailable());
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userService.createUser(user);
    }

    private Item createItem(String name, String description, Boolean available, Long ownerId) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        return itemService.createItem(item, ownerId);
    }
}