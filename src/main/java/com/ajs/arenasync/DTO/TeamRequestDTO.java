package com.ajs.arenasync.DTO;

import jakarta.validation.constraints.NotBlank;

public class TeamRequestDTO {

    @NotBlank(message = "O nome do time é obrigatório.")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    
}