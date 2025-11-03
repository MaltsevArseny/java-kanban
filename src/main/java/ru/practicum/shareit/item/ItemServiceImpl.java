package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserService userService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    @Override
    public Item createItem(Item item, Long ownerId) {
        User owner = userService.getUserById(ownerId);
        validateItem(item);
        item.setOwner(owner);
        return itemRepository.save(item);
    }

    @Override
    public Item getItemById(Long id) {
        Item item = itemRepository.findById(id);
        if (item == null) {
            throw new NotFoundException("Item with id " + id + " not found");
        }
        return item;
    }

    @Override
    public List<Item> getItemsByOwnerId(Long ownerId) {
        // Проверяем, что пользователь существует
        userService.getUserById(ownerId);
        return itemRepository.findByOwnerId(ownerId);
    }

    @Override
    public Item updateItem(Long id, Item item, Long ownerId) {
        Item existingItem = getItemById(id);

        // Проверяем, что пользователь является владельцем
        if (!existingItem.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("User is not the owner of this item");
        }

        if (item.getName() != null) {
            existingItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            existingItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            existingItem.setAvailable(item.getAvailable());
        }

        return itemRepository.update(existingItem);
    }

    @Override
    public List<Item> searchItems(String text) {
        return itemRepository.search(text);
    }

    private void validateItem(Item item) {
        if (item.getName() == null || item.getName().isBlank()) {
            throw new IllegalArgumentException("Item name cannot be empty");
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new IllegalArgumentException("Item description cannot be empty");
        }
        if (item.getAvailable() == null) {
            throw new IllegalArgumentException("Item availability must be specified");
        }
    }
}