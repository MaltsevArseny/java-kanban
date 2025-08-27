package ru.yandex.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.filmorate.model.User;
import ru.yandex.filmorate.service.UserService;
import java.time.LocalDate;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    UserControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void shouldCreateUser() throws Exception {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("test login");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        when(userService.create(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("test login"));
    }

    @Test
    void shouldReturnBadRequestWhenUserInvalid() throws Exception {
        User user = new User(); // Invalid user without email and login

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetAllUsers() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setLogin("tester");

        when(userService.findAll()).thenReturn(List.of(user));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].login").value("tester"));
    }

    @Test
    void shouldGetUserById() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setLogin("tester");

        when(userService.findById(1L)).thenReturn(java.util.Optional.of(user));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("tester"));
    }

    @Test
    void shouldReturnNotFoundWhenUserNotExists() throws Exception {
        when(userService.findById(999L)).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldAddFriend() throws Exception {
        doNothing().when(userService).addFriend(1L, 2L);

        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isOk());

        verify(userService, times(1)).addFriend(1L, 2L);
    }

    @Test
    void shouldRemoveFriend() throws Exception {
        doNothing().when(userService).removeFriend(1L, 2L);

        mockMvc.perform(delete("/users/1/friends/2"))
                .andExpect(status().isOk());

        verify(userService, times(1)).removeFriend(1L, 2L);
    }

    @Test
    void shouldGetFriends() throws Exception {
        User friend = new User();
        friend.setId(2L);
        friend.setLogin("friend");

        when(userService.getFriends(1L)).thenReturn(List.of(friend));

        mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].login").value("friend"));
    }

    @Test
    void shouldGetCommonFriends() throws Exception {
        User commonFriend = new User();
        commonFriend.setId(3L);
        commonFriend.setLogin("common");

        when(userService.getCommonFriends(1L, 2L)).thenReturn(List.of(commonFriend));

        mockMvc.perform(get("/users/1/friends/common/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].login").value("common"));
    }
}