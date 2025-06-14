package com.ajs.arenasync.DTO;

import org.springframework.hateoas.RepresentationModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.v3.oas.annotations.media.Schema; // Importe esta anotação

@JsonInclude(Include.NON_NULL)
@Schema(description = "Detalhes do prêmio retornado pela API") // Anotação na classe do DTO
public class PrizeResponseDTO extends RepresentationModel<PrizeResponseDTO> {

    @Schema(description = "ID único do prêmio", example = "1")
    private Long id;

    @Schema(description = "ID do torneio ao qual este prêmio pertence", example = "1") // Adicionado tournamentId
    private Long tournamentId;

    @Schema(description = "Descrição do prêmio", example = "Troféu de Ouro")
    private String description;

    @Schema(description = "Valor monetário do prêmio", example = "5000.00")
    private Double value;

    @Schema(description = "Nome do torneio ao qual este prêmio pertence", example = "Campeonato de Verão")
    private String tournamentName;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    // Novo getter e setter para tournamentId
    public Long getTournamentId() {
        return tournamentId;
    }
    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }

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
    public String getTournamentName() {
        return tournamentName;
    }
    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }
}
