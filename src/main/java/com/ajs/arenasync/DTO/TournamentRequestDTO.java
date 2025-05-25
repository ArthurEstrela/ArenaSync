package com.ajs.arenasync.DTO;

import jakarta.validation.constraints.NotBlank;

public class TournamentRequestDTO {

    @NotBlank(message = "O nome do torneio é obrigatório.")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    
}