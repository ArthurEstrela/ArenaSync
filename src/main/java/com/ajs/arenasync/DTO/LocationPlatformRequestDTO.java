package com.ajs.arenasync.DTO;

import com.ajs.arenasync.Entities.Enums.TournamentType;

import jakarta.validation.constraints.NotBlank;

public class LocationPlatformRequestDTO {

    @NotBlank(message = "O nome é obrigatório.")
    private String name;

    @NotBlank(message = "O tipo é obrigatório.")
    private TournamentType type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(TournamentType type) {
        this.type = type;
    }

    public TournamentType getType() {
        return type;
    }


    
}