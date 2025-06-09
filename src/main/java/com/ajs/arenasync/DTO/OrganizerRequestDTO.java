package com.ajs.arenasync.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema; // Importe esta anotação

@Schema(description = "Detalhes para a criação ou atualização de um organizador") // Anotação na classe do DTO
public class OrganizerRequestDTO {

    @NotNull(message = "O nome é obrigatório.")
    @Schema(description = "Nome do organizador", example = "Empresa Eventos Ltda.")
    private String name;

    @NotNull(message = "O e-mail é obrigatório.")
    @Email(message = "E-mail inválido.")
    @Schema(description = "Endereço de e-mail do organizador (deve ser único)", example = "contato@empresaeventos.com")
    private String email;

    @NotNull(message = "O número de telefone é obrigatório.")
    @Schema(description = "Número de telefone do organizador", example = "5511987654321")
    private String phoneNumber;

    @NotNull(message = "O nome da organização é obrigatório.")
    @Schema(description = "Nome da organização que o organizador representa", example = "Mega Eventos S.A.")
    private String organizationName;

    @Schema(description = "Biografia curta ou descrição do organizador", example = "Especialistas em eventos esportivos e de e-sports.")
    private String bio;

    @Schema(description = "Links para redes sociais ou website do organizador", example = "linkedin.com/empresaeventos, twitter.com/megaeventos")
    private String socialLinks;

    // Getters e Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getSocialLinks() {
        return socialLinks;
    }

    public void setSocialLinks(String socialLinks) {
        this.socialLinks = socialLinks;
    }
}