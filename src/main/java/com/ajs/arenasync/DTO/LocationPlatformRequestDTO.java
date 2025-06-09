package com.ajs.arenasync.DTO;

import com.ajs.arenasync.Entities.Enums.TournamentType; // Importe este enum

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull; // Importe NotNull
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Detalhes para a criação ou atualização de um local/plataforma")
public class LocationPlatformRequestDTO {

    @NotNull(message = "O nome é obrigatório.")
    @Schema(description = "Nome do local ou plataforma", example = "Arena Gaming X")
    private String name;

    @NotNull(message = "O tipo é obrigatório.") // CORRIGIDO: de @NotBlank para @NotNull
    @Schema(description = "Tipo do local/plataforma (ESPORT ou SPORT)", example = "ESPORT", allowableValues = {"SPORT", "ESPORT"}) // Adicionado allowableValues para enums no Swagger
    private TournamentType type; // CORRIGIDO: O tipo é TournamentType (enum)

    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TournamentType getType() { 
        return type;
    }

    public void setType(TournamentType type) { 
        this.type = type;
    }
}