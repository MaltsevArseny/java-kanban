package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    Item createItem(Item item, Long ownerId);
    Item getItemById(Long id);
    List<Item> getItemsByOwnerId(Long ownerId);
    Item updateItem(Long id, Item item, Long ownerId);
    List<Item> searchItems(String text);
}