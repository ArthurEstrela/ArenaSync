package com.ajs.arenasync.Services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheConfig;   // Importe CacheConfig
import org.springframework.cache.annotation.Cacheable;  // Importe Cacheable
import org.springframework.cache.annotation.CacheEvict;  // Importe CacheEvict
import org.springframework.cache.annotation.CachePut;   // Importe CachePut


import com.ajs.arenasync.DTO.UserRequestDTO;
import com.ajs.arenasync.DTO.UserResponseDTO;
import com.ajs.arenasync.Entities.User;
import com.ajs.arenasync.Exceptions.BusinessException;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.ajs.arenasync.Repositories.UserRepository;

@Service
@CacheConfig(cacheNames = "users") // Define um nome padrão para o cache desta classe
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Buscar por ID
    @Cacheable(key = "#id") // Armazena em cache o resultado, usando o ID como chave
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return toResponseDTO(user);
    }

    // Buscar por e-mail (se for uma operação comum, também pode ser cacheadada)
    @Cacheable(key = "#email") // Armazena em cache, usando o e-mail como chave
    public UserResponseDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return toResponseDTO(user);
    }

    // Listar todos (cache de lista completa)
    // Cuidado: cachear listas completas pode ser complexo em sistemas grandes,
    // pois qualquer alteração em um item exige a revalidação de toda a lista.
    @Cacheable(key = "'allUsers'") // Usa uma chave estática para a lista completa
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Criar novo usuário
    // @CachePut atualiza o cache (adiciona o novo usuário ou atualiza se já existir no cache)
    // allEntries = true no @CacheEvict abaixo para remover o cache de 'allUsers'
    @CachePut(key = "#result.id") // Adiciona o usuário criado ao cache 'users', usando o ID retornado como chave
    @CacheEvict(key = "'allUsers'", allEntries = true) // Limpa o cache da lista completa
    public UserResponseDTO createUser(UserRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Já existe um usuário com este e-mail.");
        }

        User user = toEntity(dto);
        return toResponseDTO(userRepository.save(user));
    }

    // Atualizar
    @CachePut(key = "#id") // Atualiza o usuário no cache (se existir), depois de atualizar no banco
    @CacheEvict(key = "'allUsers'", allEntries = true) // Limpa o cache da lista completa
    public UserResponseDTO updateUser(Long id, UserRequestDTO dto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        if (!existingUser.getEmail().equals(dto.getEmail())
                && userRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Este e-mail já está em uso por outro usuário.");
        }

        existingUser.setName(dto.getName());
        existingUser.setEmail(dto.getEmail());
        existingUser.setPassword(dto.getPassword());
        existingUser.setAge(dto.getAge());

        return toResponseDTO(userRepository.save(existingUser));
    }

    // Deletar
    // @CacheEvict remove o usuário do cache quando ele é deletado do banco
    @CacheEvict(key = "#id", allEntries = true) // Limpa o cache para o ID específico e também o cache da lista completa
    public void deleteUser(Long id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        userRepository.delete(existingUser);
    }

    // Conversão: DTO → Entity (sem mudanças)
    private User toEntity(UserRequestDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setAge(dto.getAge());
        return user;
    }

    // Conversão: Entity → DTO (sem mudanças)
    private UserResponseDTO toResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setAge(user.getAge());
        return dto;
    }
}