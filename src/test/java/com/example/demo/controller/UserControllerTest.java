package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        // Clear database before each test to ensure a clean state
        userRepository.deleteAll();
    }

    @Test
    public void shouldCreateUser() throws Exception {
        String userJson = """
                {
                    "username": "johndoe",
                    "firstName": "John",
                    "lastName": "Doe",
                    "email": "john.doe@example.com",
                    "phoneNumber": "1234567890"
                }
                """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("johndoe")));
    }

    @Test
    public void shouldGetAllUsers() throws Exception {
        User user1 = new User();
        user1.setUsername("user1");
        user1.setFirstName("First");
        user1.setLastName("User");
        user1.setEmail("user1@example.com");
        user1.setPhoneNumber("1234567890");

        User user2 = new User();
        user2.setUsername("user2");
        user2.setFirstName("Second");
        user2.setLastName("User");
        user2.setEmail("user2@example.com");
        user2.setPhoneNumber("0987654321");

        userRepository.save(user1);
        userRepository.save(user2);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)));
    }

    @Test
    public void shouldGetUserById() throws Exception {
        User user = new User();
        user.setUsername("uniqueuser");
        user.setFirstName("Unique");
        user.setLastName("User");
        user.setEmail("uniqueuser@example.com");
        user.setPhoneNumber("5551234567");

        user = userRepository.save(user);

        mockMvc.perform(get("/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("uniqueuser")));
    }

    @Test
    public void shouldUpdateUser() throws Exception {
        User user = new User();
        user.setUsername("updatableuser");
        user.setFirstName("Update");
        user.setLastName("User");
        user.setEmail("updateuser@example.com");
        user.setPhoneNumber("5559876543");

        user = userRepository.save(user);

        String updatedUserJson = """
                {
                    "username": "updateduser",
                    "firstName": "Updated",
                    "lastName": "User",
                    "email": "updateduser@example.com",
                    "phoneNumber": "5551239876"
                }
                """;

        mockMvc.perform(put("/users/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedUserJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("updateduser")))
                .andExpect(jsonPath("$.email", is("updateduser@example.com")));
    }

    @Test
    public void shouldDeleteUser() throws Exception {
        User user = new User();
        user.setUsername("deletableuser");
        user.setFirstName("Delete");
        user.setLastName("User");
        user.setEmail("deleteuser@example.com");
        user.setPhoneNumber("5556667777");

        user = userRepository.save(user);

        mockMvc.perform(delete("/users/{id}", user.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/users/{id}", user.getId()))
                .andExpect(status().isNotFound());
    }
}
