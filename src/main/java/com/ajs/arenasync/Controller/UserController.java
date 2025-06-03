package com.ajs.arenasync.Controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel; // Para coleções
import org.springframework.hateoas.Link; // Para Link
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder; // Para construir links
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*; // Import estático para linkTo e methodOn

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ajs.arenasync.DTO.UserRequestDTO;
import com.ajs.arenasync.DTO.UserResponseDTO;
import com.ajs.arenasync.Services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Buscar usuário por ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        UserResponseDTO user = userService.getUserById(id);
        // Adicionar link "self"
        user.add(linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel());
        // Exemplo: Adicionar link para todos os usuários (relação "collection" ou "all-users")
        user.add(linkTo(methodOn(UserController.class).getAllUsers()).withRel("all-users"));
        return ResponseEntity.ok(user);
    }

    // Buscar usuário por email
    @GetMapping("/search")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@RequestParam String email) {
        UserResponseDTO user = userService.getUserByEmail(email);
        // Adicionar link "self" para o recurso encontrado (poderia ser o /api/users/{id} se o email mapeasse para um ID)
        // Se não houver um endpoint direto de ID para o resultado da busca por email, um link "self" para o próprio /search pode ser menos útil.
        // Vamos assumir que após buscar por email, você tem o ID.
        if (user.getId() != null) {
            user.add(linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel());
        }
        user.add(linkTo(methodOn(UserController.class).getAllUsers()).withRel("all-users"));
        return ResponseEntity.ok(user);
    }

    // Listar todos os usuários
    @GetMapping
    public ResponseEntity<CollectionModel<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> usersList = userService.getAllUsers();
        if (usersList.isEmpty()) {
            // Link "self" para a coleção vazia
            Link selfLink = linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel();
            return ResponseEntity.ok(CollectionModel.empty(selfLink));
        }

        // Adicionar link "self" para cada usuário na lista
        for (UserResponseDTO user : usersList) {
            user.add(linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel());
        }
        // Criar link "self" para a coleção
        Link selfLink = linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel();
        CollectionModel<UserResponseDTO> collectionModel = CollectionModel.of(usersList, selfLink);
        return ResponseEntity.ok(collectionModel);
    }

    // Criar novo usuário
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO dto) {
        UserResponseDTO createdUser = userService.createUser(dto);
        // Adicionar link "self" para o recurso criado
        createdUser.add(linkTo(methodOn(UserController.class).getUserById(createdUser.getId())).withSelfRel());
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    // Atualizar usuário
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequestDTO dto) {
        UserResponseDTO updatedUser = userService.updateUser(id, dto);
        // Adicionar link "self"
        updatedUser.add(linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel());
        return ResponseEntity.ok(updatedUser);
    }

    // Deletar usuário - Respostas 204 No Content geralmente não têm corpo, então não há onde adicionar links.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}