package com.ajs.arenasync.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class OrganizerRequestDTO {

    @NotBlank(message = "Nome é obrigatório.")
    private String name;

    @NotBlank(message = "E-mail é obrigatório.")
    @Email(message = "E-mail inválido.")
    private String email;

    @NotBlank(message = "Telefone é obrigatório.")
    private String phoneNumber;

    private String organizationName;
    private String bio;
    private String socialLinks;

    // Getters e Setters
}
