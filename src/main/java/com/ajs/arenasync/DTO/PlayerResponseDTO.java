package com.ajs.arenasync.DTO;

import org.springframework.hateoas.RepresentationModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.v3.oas.annotations.media.Schema; // Importe esta anotação

@JsonInclude(Include.NON_NULL)
@Schema(description = "Detalhes do jogador retornado pela API") // Anotação na classe do DTO
public class PlayerResponseDTO extends RepresentationModel<PlayerResponseDTO>{

    @Schema(description = "ID único do jogador", example = "1")
    private Long id;

    @Schema(description = "Nome completo do jogador", example = "Lionel Messi")
    private String name;

    @Schema(description = "Endereço de e-mail do jogador", example = "lionel.messi@example.com")
    private String email;

    @Schema(description = "Posição do jogador (se aplicável)", example = "Atacante")
    private String position; // Adicionado o campo position no DTO de resposta

    @Schema(description = "Nome do time ao qual o jogador pertence (será nulo se for agente livre)", example = "Paris Saint-Germain")
    private String teamName; // Pode ser null se for agente livre

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
}
