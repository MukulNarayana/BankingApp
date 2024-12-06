package com.example.demo.Controllers;

import com.example.demo.Entity.User;
import com.example.demo.Exception.UserNotFoundException;
import com.example.demo.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new com.example.demo.Exception.GlobalExceptionHandler()) // Adding GlobalExceptionHandler
                .build();
    }

    @Test
    public void testShowUserById_UserFound() throws Exception {
        Long userId = 1L;
        User user = new User(userId, "John", "Doe", "john.doe@example.com", "password", "ROLE_USER", null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/user/fetch/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    public void testShowUserById_UserNotFound() throws Exception {
        Long nonExistentUserId = 1L;
        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/user/fetch/{id}", nonExistentUserId))
                .andExpect(status().isNotFound()) // Expect 404 Not Found
                .andExpect(content().string("User not found with ID: " + nonExistentUserId));

        verify(userRepository, times(1)).findById(nonExistentUserId);
    }



    @Test
    public void testAddUser() throws Exception {
        User user = new User(null, "Jane", "Doe", "jane.doe@example.com", "password", "ROLE_USER", null);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        // Instead of returning the same object, return a user with ID to simulate saving.
        when(userRepository.save(any(User.class))).thenReturn(new User(1L, "Jane", "Doe", "jane.doe@example.com", "encodedPassword", "ROLE_USER", null));

        mockMvc.perform(post("/user/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Jane\",\"lastName\":\"Doe\",\"email\":\"jane.doe@example.com\",\"password\":\"password\",\"role\":\"ROLE_USER\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().string("User added successfully"));

        // Verify that the save method was called
        verify(userRepository, times(1)).save(any(User.class));
    }


    @Test
    public void testUpdateUser() throws Exception {
        Long userId = 1L;
        User existingUser = new User(userId, "John", "Doe", "john.doe@example.com", "password", "ROLE_USER", null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedNewPassword");

        mockMvc.perform(put("/user/update/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"John\",\"lastName\":\"Smith\",\"email\":\"john.smith@example.com\",\"password\":\"newpassword\",\"role\":\"ROLE_USER\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("User updated successfully"));
    }

    @Test
    public void testDeleteUser() throws Exception {
        Long userId = 1L;
        User user = new User(userId, "John", "Doe", "john.doe@example.com", "password", "ROLE_USER", null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        mockMvc.perform(delete("/user/delete/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully"));
    }
}
