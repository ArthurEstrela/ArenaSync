package com.ajs.arenasync.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PlayerRequestDTO {

    @NotBlank(message = "O nome do jogador é obrigatório.")
    private String name;

    @NotNull(message = "O ID do time é obrigatório.")
    private Long teamId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    
}