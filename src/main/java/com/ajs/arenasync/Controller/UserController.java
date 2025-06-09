package com.ajs.arenasync.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ajs.arenasync.DTO.UserRequestDTO;
import com.ajs.arenasync.DTO.UserResponseDTO;
import com.ajs.arenasync.Services.UserService;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation; // Importe esta anotação
import io.swagger.v3.oas.annotations.tags.Tag; // Importe esta anotação
import io.swagger.v3.oas.annotations.Parameter; // Importe esta anotação para documentar PathVariable/RequestParam

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "Operações relacionadas a usuários no ArenaSync") // Anotação na classe
public class UserController {

    @Autowired
    private UserService userService;

    // Buscar usuário por ID
    @GetMapping("/{id}")
    @Operation(summary = "Obter usuário por ID", description = "Retorna um usuário específico com base no seu ID") // Anotação
                                                                                                                   // no
                                                                                                                   // método
    public ResponseEntity<UserResponseDTO> getUserById(
            @Parameter(description = "ID do usuário a ser buscado", required = true) @PathVariable Long id) {
        UserResponseDTO user = userService.getUserById(id);
        user.add(linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel());
        user.add(linkTo(methodOn(UserController.class).getAllUsers()).withRel("all-users"));
        return ResponseEntity.ok(user);
    }

    // Buscar usuário por email
    @GetMapping("/search")
    @Operation(summary = "Buscar usuário por e-mail", description = "Retorna um usuário específico com base no seu endereço de e-mail")
    public ResponseEntity<UserResponseDTO> getUserByEmail(
            @Parameter(description = "Endereço de e-mail do usuário", required = true) @RequestParam String email) {
        UserResponseDTO user = userService.getUserByEmail(email);
        if (user.getId() != null) {
            user.add(linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel());
        }
        user.add(linkTo(methodOn(UserController.class).getAllUsers()).withRel("all-users"));
        return ResponseEntity.ok(user);
    }

    // Listar todos os usuários
    @GetMapping
    @Operation(summary = "Listar todos os usuários", description = "Retorna uma lista de todos os usuários registrados")
    public ResponseEntity<CollectionModel<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> usersList = userService.getAllUsers();
        if (usersList.isEmpty()) {
            Link selfLink = linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel();
            return ResponseEntity.ok(CollectionModel.empty(selfLink));
        }

        for (UserResponseDTO user : usersList) {
            user.add(linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel());
        }
        Link selfLink = linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel();
        CollectionModel<UserResponseDTO> collectionModel = CollectionModel.of(usersList, selfLink);
        return ResponseEntity.ok(collectionModel);
    }

    // Criar novo usuário
    @PostMapping
    @Operation(summary = "Criar novo usuário", description = "Cria um novo usuário no sistema com as informações fornecidas")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO dto) {
        UserResponseDTO createdUser = userService.createUser(dto);
        createdUser.add(linkTo(methodOn(UserController.class).getUserById(createdUser.getId())).withSelfRel());
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    // Atualizar usuário
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário existente", description = "Atualiza as informações de um usuário existente pelo seu ID")
    public ResponseEntity<UserResponseDTO> updateUser(
            @Parameter(description = "ID do usuário a ser atualizado", required = true) @PathVariable Long id,
            @Valid @RequestBody UserRequestDTO dto) {
        UserResponseDTO updatedUser = userService.updateUser(id, dto);
        updatedUser.add(linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel());
        return ResponseEntity.ok(updatedUser);
    }

    // Deletar usuário
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar usuário", description = "Deleta um usuário do sistema pelo seu ID")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID do usuário a ser deletado", required = true) @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}