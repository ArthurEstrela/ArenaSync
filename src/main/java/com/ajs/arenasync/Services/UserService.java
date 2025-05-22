package com.ajs.arenasync.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ajs.arenasync.Entities.User;
import com.ajs.arenasync.Exceptions.BusinessException;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.ajs.arenasync.Repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Buscar usuário por ID
    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    // Buscar usuário por e-mail
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    // Listar todos os usuários
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Criar novo usuário com validação de e-mail único
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BusinessException("Já existe um usuário com este e-mail.");
        }

        return userRepository.save(user);
    }

    // Atualizar usuário com validações
    public User updateUser(Long id, User updatedUser) {
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", id));

        // Se o novo e-mail for diferente e já existir no sistema, dá erro
        if (!existingUser.getEmail().equals(updatedUser.getEmail()) &&
            userRepository.existsByEmail(updatedUser.getEmail())) {
            throw new BusinessException("Este e-mail já está em uso por outro usuário.");
        }

        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPassword(updatedUser.getPassword());
        existingUser.setAge(updatedUser.getAge());

        return userRepository.save(existingUser);
    }

    // Deletar usuário
    public void deleteUser(Long id) {
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", id));

        userRepository.delete(existingUser);
    }
}
