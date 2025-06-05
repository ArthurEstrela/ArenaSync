package com.ajs.arenasync.DTO;

import java.time.LocalDate;

import org.springframework.hateoas.RepresentationModel;

import com.ajs.arenasync.Entities.Enums.TournamentStatus;
import com.ajs.arenasync.Entities.Enums.TournamentType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.v3.oas.annotations.media.Schema; // Importe esta anotação

@JsonInclude(Include.NON_NULL)
@Schema(description = "Detalhes do torneio retornado pela API") // Anotação na classe do DTO de resposta
public class TournamentResponseDTO extends RepresentationModel<TournamentResponseDTO>{
    
    @Schema(description = "ID único do torneio", example = "1")
    private Long id;

    @Schema(description = "Nome do torneio", example = "Campeonato de Verão 2025")
    private String name;

    @Schema(description = "Modalidade do torneio", example = "Futebol")
    private String modality;

    @Schema(description = "Regras específicas do torneio", example = "Regras FIFA")
    private String rules;

    @Schema(description = "Data de início do torneio (formato YYYY-MM-DD)", example = "2025-07-01")
    private LocalDate startDate;

    @Schema(description = "Data de término do torneio (formato YYYY-MM-DD)", example = "2025-07-15")
    private LocalDate endDate;

    @Schema(description = "Tipo de torneio", example = "SPORT", allowableValues = {"SPORT", "ESPORT"})
    private TournamentType type;

    @Schema(description = "Status atual do torneio", example = "PENDING", allowableValues = {"PENDING", "ONGOING", "FINISHED"})
    private TournamentStatus status;

    @Schema(description = "Nome do organizador do torneio", example = "Arena Corp")
    private String organizerName;

    // Getters e Setters (já existentes, apenas para visualização)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public TournamentStatus getStatus() {
        return status;
    }

    public void setStatus(TournamentStatus status) {
        this.status = status;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }
}