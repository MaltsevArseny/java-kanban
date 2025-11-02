package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.CreateUserDto;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody CreateUserDto createUserDto) {
        User user = UserMapper.toUserFromCreateDto(createUserDto);
        User createdUser = userService.createUser(user);
        return UserMapper.toUserDto(createdUser);
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return UserMapper.toUserDto(user);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User updatedUser = userService.updateUser(id, user);
        return UserMapper.toUserDto(updatedUser);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}