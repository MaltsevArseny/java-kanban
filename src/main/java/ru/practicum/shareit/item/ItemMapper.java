package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.user.User;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        if (item == null) {
            return null;
        }
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    public static Item toItem(ItemDto itemDto, User owner) {
        if (itemDto == null) {
            return null;
        }
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                owner
        );
    }

    public static Item toItemFromCreateDto(CreateItemDto createItemDto, User owner) {
        if (createItemDto == null) {
            return null;
        }
        Item item = new Item();
        item.setName(createItemDto.getName());
        item.setDescription(createItemDto.getDescription());
        item.setAvailable(createItemDto.getAvailable());
        item.setOwner(owner);
        return item;
    }
}