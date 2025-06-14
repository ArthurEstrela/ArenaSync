package com.ajs.arenasync.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema; // Importe esta anotação

@Schema(description = "Detalhes para a criação de um jogador") // Anotação na classe do DTO
public class PlayerRequestDTO {

    @NotBlank(message = "O nome do jogador é obrigatório.")
    @Schema(description = "Nome completo do jogador", example = "Lionel Messi")
    private String name;

    @NotNull(message = "O e-mail é obrigatório.")
    @Email(message = "E-mail inválido.")
    @Schema(description = "Endereço de e-mail do jogador (deve ser único)", example = "lionel.messi@example.com")
    private String email;

    @Schema(description = "Posição do jogador (se aplicável, ex: Atacante, Defensor, Mid Laner)", example = "Atacante")
    private String position; // Adicionado o campo position

    @Schema(description = "ID do time ao qual o jogador pertence (opcional para agentes livres)", example = "10")
    private Long teamId; // Pode ser null para agentes livres

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

    // Getter e Setter para position
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }
}
