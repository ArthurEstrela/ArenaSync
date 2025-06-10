package com.ajs.arenasync.Controller;

import com.ajs.arenasync.DTO.UserRequestDTO;
import com.ajs.arenasync.DTO.UserResponseDTO;
import com.ajs.arenasync.Services.UserService;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.ajs.arenasync.Exceptions.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;


@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserResponseDTO userResponseDTO;
    private UserRequestDTO userRequestDTO;

    @BeforeEach
    void setUp() {
        userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(1L);
        userResponseDTO.setName("Test User");
        userResponseDTO.setEmail("test@example.com");
        userResponseDTO.setAge(30);

        userRequestDTO = new UserRequestDTO();
        userRequestDTO.setName("Test User");
        userRequestDTO.setEmail("test@example.com");
        userRequestDTO.setAge(30);
        userRequestDTO.setPassword("password");
    }

    @Test
    void testGetUserById_Success() throws Exception {
        when(userService.getUserById(1L)).thenReturn(userResponseDTO);

        mockMvc.perform(get("/api/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType("application/hal+json")))
                .andExpect(jsonPath("$.name", is("Test User")))
                .andExpect(jsonPath("$.email", is("test@example.com")));
    }

    @Test
    void testGetUserById_NotFound() throws Exception {
        when(userService.getUserById(1L)).thenThrow(new ResourceNotFoundException("User", 1L));

        mockMvc.perform(get("/api/users/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateUser_Success() throws Exception {
        when(userService.createUser(any(UserRequestDTO.class))).thenReturn(userResponseDTO);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Test User")));
    }
    
    @Test
    void testCreateUser_ValidationError() throws Exception {
        UserRequestDTO invalidRequestDTO = new UserRequestDTO();
        invalidRequestDTO.setEmail("test@example.com");
        invalidRequestDTO.setName(""); // Viola @NotBlank
        invalidRequestDTO.setAge(0); // Viola @Min
        invalidRequestDTO.setPassword("123"); // Viola @Size

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestDTO)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void testCreateUser_EmailAlreadyExists() throws Exception {
        when(userService.createUser(any(UserRequestDTO.class)))
                .thenThrow(new BusinessException("Já existe um usuário com este e-mail."));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testGetAllUsers_Success() throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.singletonList(userResponseDTO));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType("application/hal+json")))
                .andExpect(jsonPath("$._embedded.userResponseDTOList", hasSize(1)))
                .andExpect(jsonPath("$._embedded.userResponseDTOList[0].name", is("Test User")));
    }

    @Test
    void testUpdateUser_Success() throws Exception {
        UserResponseDTO updatedUserResponseDTO = new UserResponseDTO();
        updatedUserResponseDTO.setId(1L);
        updatedUserResponseDTO.setName("Updated User");
        updatedUserResponseDTO.setEmail("updated@example.com");
        updatedUserResponseDTO.setAge(31);

        when(userService.updateUser(anyLong(), any(UserRequestDTO.class))).thenReturn(updatedUserResponseDTO);

        mockMvc.perform(put("/api/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated User")))
                .andExpect(jsonPath("$.email", is("updated@example.com")));
    }
    
    @Test
    void testUpdateUser_NotFound() throws Exception {
        when(userService.updateUser(anyLong(), any(UserRequestDTO.class)))
            .thenThrow(new ResourceNotFoundException("User", 1L));

        mockMvc.perform(put("/api/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteUser_NotFound() throws Exception {
        org.mockito.Mockito.doThrow(new ResourceNotFoundException("User", 1L)).when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/{id}", 1L))
                .andExpect(status().isNotFound());
    }
}