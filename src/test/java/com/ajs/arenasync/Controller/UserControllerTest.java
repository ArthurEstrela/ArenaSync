package com.ajs.arenasync.Controller;

import com.ajs.arenasync.DTO.UserRequestDTO;
import com.ajs.arenasync.DTO.UserResponseDTO;
import com.ajs.arenasync.Services.UserService;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException; // Importe suas exceções
import com.ajs.arenasync.Exceptions.BusinessException;    // Importe suas exceções
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


@WebMvcTest(UserController.class) // Especifica o controller que você quer testar
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc; // Para simular requisições HTTP

    @MockBean // Cria um mock do UserService e o injeta no contexto do Spring para este teste
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper; // Para converter objetos Java para JSON e vice-versa

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
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
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
                .andExpect(status().isCreated()) // Espera HTTP 201 Created
                .andExpect(jsonPath("$.name", is("Test User")));
    }
    
    @Test
    void testCreateUser_ValidationError() throws Exception {
        UserRequestDTO invalidRequestDTO = new UserRequestDTO(); // DTO inválido (ex: nome em branco)
        invalidRequestDTO.setEmail("test@example.com");
        // Não seta o nome para simular falha na validação @NotBlank

        // Não precisamos mockar o service aqui, pois a validação do DTO acontece antes
        // no framework. O Spring vai retornar um erro 400 Bad Request.

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestDTO)))
                .andExpect(status().isBadRequest()); // A validação @Valid falhará
    }


    @Test
    void testCreateUser_EmailAlreadyExists() throws Exception {
        when(userService.createUser(any(UserRequestDTO.class)))
                .thenThrow(new BusinessException("Já existe um usuário com este e-mail."));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isInternalServerError()); // GlobalExceptionHandler trata BusinessException como INTERNAL_SERVER_ERROR por padrão, mas você pode mudar isso
                                                            // Se você mapeou BusinessException para HttpStatus.BAD_REQUEST no GlobalExceptionHandler, use .andExpect(status().isBadRequest());
    }
    
    @Test
    void testGetAllUsers_Success() throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.singletonList(userResponseDTO));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Test User")));
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
                        .content(objectMapper.writeValueAsString(userRequestDTO))) // userRequestDTO pode ser o DTO com os dados de atualização
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
        doNothing().when(userService).deleteUser(1L); // Para métodos void

        mockMvc.perform(delete("/api/users/{id}", 1L))
                .andExpect(status().isNoContent()); // Espera HTTP 204 No Content
    }

    @Test
    void testDeleteUser_NotFound() throws Exception {
        // Configura o mock para lançar a exceção quando deleteUser for chamado
        // com um ID que não existe.
        // Nota: O GlobalExceptionHandler vai pegar ResourceNotFoundException e retornar NOT_FOUND.
        org.mockito.Mockito.doThrow(new ResourceNotFoundException("User", 1L)).when(userService).deleteUser(1L);


        mockMvc.perform(delete("/api/users/{id}", 1L))
                .andExpect(status().isNotFound());
    }
}