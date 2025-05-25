package com.ajs.arenasync.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PrizeRequestDTO {

    @NotBlank(message = "A descrição do prêmio é obrigatória.")
    private String description;

    @NotNull(message = "O valor do prêmio é obrigatório.")
    @Positive(message = "O valor deve ser maior que zero.")
    private Double value;

    @NotNull(message = "O ID do torneio é obrigatório.")
    private Long tournamentId;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Long getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }
    
}