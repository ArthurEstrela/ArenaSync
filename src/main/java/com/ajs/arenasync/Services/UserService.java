package com.ajs.arenasync.Services;

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

    // Buscar usuário por ID com tratamento de erro
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", id));
    }

    // Buscar por email com validação
    public User findByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("Usuário com e-mail " + email + " não encontrado.");
        }
        return user;
    }

    // Salvar usuário com regra de email único
    public User save(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new BusinessException("E-mail é obrigatório.");
        }

        Optional<User> existingUser = Optional.ofNullable(userRepository.findByEmail(user.getEmail()));
        if (existingUser.isPresent() && !existingUser.get().getId().equals(user.getId())) {
            throw new BusinessException("Já existe um usuário cadastrado com esse e-mail.");
        }

        return userRepository.save(user);
    }

    public void deleteById(Long id) {
        User user = findById(id);
        userRepository.deleteById(user.getId());
    }

    public User update(Long id, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", id));

        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());

        return userRepository.save(existingUser);
    }
}
