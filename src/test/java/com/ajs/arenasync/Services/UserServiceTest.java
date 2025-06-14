package com.ajs.arenasync.Services;

import com.ajs.arenasync.DTO.UserRequestDTO;
import com.ajs.arenasync.DTO.UserResponseDTO;
import com.ajs.arenasync.Entities.User;
import com.ajs.arenasync.Exceptions.BusinessException;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.ajs.arenasync.Repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserRequestDTO userRequestDTO;

    @BeforeEach
    void setUp() {
        // Configuração inicial para os testes
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setAge(30);
        user.setPassword("password");

        userRequestDTO = new UserRequestDTO();
        userRequestDTO.setName("Test User");
        userRequestDTO.setEmail("test@example.com");
        userRequestDTO.setAge(30);
        userRequestDTO.setPassword("password");
    }

    @Test
    void testGetUserById_Success() {
        // Configura o mock do repositório para retornar o usuário quando findById for chamado
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Chama o método do service
        UserResponseDTO responseDTO = userService.getUserById(1L);

        // Verifica as asserções
        assertNotNull(responseDTO);
        assertEquals(user.getName(), responseDTO.getName());
        assertEquals(user.getEmail(), responseDTO.getEmail());

        // Verifica se o método findById do repositório foi chamado uma vez com o ID correto
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUserById_NotFound() {
        // Configura o mock do repositório para retornar um Optional vazio
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Verifica se a exceção ResourceNotFoundException é lançada
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(1L);
        });

        // Verifica se o método findById do repositório foi chamado
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateUser_Success() {
        // Configura o mock para o existsByEmail
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        // Configura o mock para o save
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO responseDTO = userService.saveUser(userRequestDTO);

        assertNotNull(responseDTO);
        assertEquals(userRequestDTO.getName(), responseDTO.getName());
        verify(userRepository, times(1)).existsByEmail(userRequestDTO.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUser_EmailAlreadyExists() {
        // Configura o mock para o existsByEmail retornar true
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(BusinessException.class, () -> {
            userService.saveUser(userRequestDTO);
        });

        verify(userRepository, times(1)).existsByEmail(userRequestDTO.getEmail());
        // Garante que o save não foi chamado se o email já existe
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        List<UserResponseDTO> users = userService.getAllUsers();

        assertNotNull(users);
        assertFalse(users.isEmpty());
        assertEquals(1, users.size());
        assertEquals(user.getName(), users.get(0).getName());
        verify(userRepository, times(1)).findAll();
    }
    
    @Test
    void testUpdateUser_Success() {
        UserRequestDTO updateDto = new UserRequestDTO();
        updateDto.setName("Updated Name");
        updateDto.setEmail("updated@example.com");
        updateDto.setAge(35);
        updateDto.setPassword("newpassword");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName(updateDto.getName());
        updatedUser.setEmail(updateDto.getEmail());
        updatedUser.setAge(updateDto.getAge());


        when(userRepository.findById(1L)).thenReturn(Optional.of(user)); // Usuário original
        when(userRepository.existsByEmail(updateDto.getEmail())).thenReturn(false); // Novo email não existe
        when(userRepository.save(any(User.class))).thenReturn(updatedUser); // Salva o usuário atualizado

        UserResponseDTO responseDTO = userService.updateUser(1L, updateDto);

        assertNotNull(responseDTO);
        assertEquals("Updated Name", responseDTO.getName());
        assertEquals("updated@example.com", responseDTO.getEmail());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).existsByEmail(updateDto.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUser_NotFound() {
        UserRequestDTO updateDto = new UserRequestDTO();
        updateDto.setName("Updated Name");
        updateDto.setEmail("updated@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.updateUser(1L, updateDto);
        });

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void testUpdateUser_EmailAlreadyExistsForAnotherUser() {
        UserRequestDTO updateDto = new UserRequestDTO();
        updateDto.setName("Updated Name");
        updateDto.setEmail("another@example.com"); // Email que já pertence a outro usuário
        updateDto.setAge(35);
        updateDto.setPassword("newpassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user)); // Usuário original (test@example.com)
        when(userRepository.existsByEmail(updateDto.getEmail())).thenReturn(true); // O "novo" email já existe

        assertThrows(BusinessException.class, () -> {
            userService.updateUser(1L, updateDto);
        });

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).existsByEmail(updateDto.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void testDeleteUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        // O método delete não retorna nada, então não precisa de when().thenReturn() para ele.
        // Apenas verificaremos se ele é chamado.
        doNothing().when(userRepository).delete(user);

        assertDoesNotThrow(() -> {
            userService.deleteUser(1L);
        });

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void testDeleteUser_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.deleteUser(1L);
        });

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).delete(any(User.class));
    }
}