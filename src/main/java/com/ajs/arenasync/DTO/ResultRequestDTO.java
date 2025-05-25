package com.ajs.arenasync.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ResultRequestDTO {

    @NotBlank(message = "Detalhes são obrigatórios.")
    private String details;

    @NotNull
    private Long matchId;

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    
}