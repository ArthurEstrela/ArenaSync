package com.ajs.arenasync.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.Schema; // Importe esta anotação

@Schema(description = "Detalhes para a criação de um prêmio") // Anotação na classe do DTO
public class PrizeRequestDTO {

    @NotNull(message = "A descrição do prêmio é obrigatória.")
    @Schema(description = "Descrição do prêmio (ex: Troféu de Ouro, Medalha)", example = "Troféu de Ouro")
    private String description;

    @NotNull(message = "O valor do prêmio é obrigatório.")
    @Positive(message = "O valor deve ser maior que zero.")
    @Schema(description = "Valor monetário do prêmio (deve ser maior que zero)", example = "5000.00")
    private Double value;

    @NotNull(message = "O ID do torneio é obrigatório.")
    @Schema(description = "ID do torneio ao qual este prêmio pertence", example = "1")
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