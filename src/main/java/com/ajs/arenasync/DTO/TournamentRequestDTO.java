package com.ajs.arenasync.DTO;

import java.time.LocalDate;

import com.ajs.arenasync.Entities.Enums.TournamentType;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TournamentRequestDTO {

    @NotBlank(message = "O nome do torneio é obrigatório.")
    private String name;

    @NotBlank(message = "A modalidade é obrigatória.")
    private String modality;

    @NotBlank(message = "As regras do torneio são obrigatórias.")
    private String rules;

    @NotNull(message = "A data de início é obrigatória.")
    @FutureOrPresent(message = "A data de início deve ser hoje ou uma data futura.")
    private LocalDate startDate;

    @NotNull(message = "A data de término é obrigatória.")
    private LocalDate endDate;

    @NotNull(message = "O tipo de torneio (ESPORT ou SPORT) é obrigatório.")
    private TournamentType type;

    // Getters e Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModality() {
        return modality;
    }

    public void setModality(String modality) {
        this.modality = modality;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public TournamentType getType() {
        return type;
    }

    public void setType(TournamentType type) {
        this.type = type;
    }
}
