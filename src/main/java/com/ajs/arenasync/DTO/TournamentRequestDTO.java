package com.ajs.arenasync.DTO;

import java.time.LocalDate;

import com.ajs.arenasync.Entities.Enums.TournamentType;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema; // Importe esta anotação

@Schema(description = "Detalhes para a criação ou atualização de um torneio") // Anotação na classe do DTO
public class TournamentRequestDTO {

    @NotNull(message = "O nome do torneio é obrigatório.")
    @Schema(description = "Nome do torneio", example = "Campeonato de Verão 2025")
    private String name;

    @NotNull(message = "A modalidade é obrigatória.")
    @Schema(description = "Modalidade do torneio (Ex: Futebol, Xadrez, LoL)", example = "Futebol")
    private String modality;

    @NotNull(message = "As regras do torneio são obrigatórias.")
    @Schema(description = "Regras específicas do torneio", example = "Regras FIFA, sistema de pontos, etc.")
    private String rules;

    @NotNull(message = "A data de início é obrigatória.")
    @FutureOrPresent(message = "A data de início deve ser hoje ou uma data futura.")
    @Schema(description = "Data de início do torneio (formato YYYY-MM-DD)", example = "2025-07-01")
    private LocalDate startDate;

    @NotNull(message = "A data de término é obrigatória.")
    @Schema(description = "Data de término do torneio (formato YYYY-MM-DD)", example = "2025-07-15")
    private LocalDate endDate;

    @NotNull(message = "O tipo de torneio (ESPORT ou SPORT) é obrigatório.")
    @Schema(description = "Tipo de torneio (ESPORT para eletrônicos, SPORT para físicos)", example = "SPORT", allowableValues = {"SPORT", "ESPORT"})
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