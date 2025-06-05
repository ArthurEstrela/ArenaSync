package com.ajs.arenasync.DTO;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema; // Importe esta anotação

@Schema(description = "Detalhes para a criação ou atualização de um time") // Anotação na classe do DTO
public class TeamRequestDTO {

    @NotBlank(message = "O nome do time é obrigatório.")
    @Schema(description = "Nome do time", example = "Os Vingadores")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}