package com.ajs.arenasync.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema; // Importe esta anotação

@Schema(description = "Detalhes para a criação de um resultado de partida") // Anotação na classe do DTO
public class ResultRequestDTO {

    @NotNull(message = "O ID da partida é obrigatório.")
    @Schema(description = "ID da partida à qual este resultado pertence", example = "1")
    private Long matchId;

    @NotNull(message = "O placar do time A é obrigatório.")
    @Min(value = 0, message = "O placar do time A não pode ser negativo.")
    @Schema(description = "Pontuação do Time A na partida", example = "3")
    private Integer scoreTeamA;

    @NotNull(message = "O placar do time B é obrigatório.")
    @Min(value = 0, message = "O placar do time B não pode ser negativo.")
    @Schema(description = "Pontuação do Time B na partida", example = "1")
    private Integer scoreTeamB;

    // Getters e Setters (já existentes)
    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public Integer getScoreTeamA() {
        return scoreTeamA;
    }

    public void setScoreTeamA(Integer scoreTeamA) {
        this.scoreTeamA = scoreTeamA;
    }

    public Integer getScoreTeamB() {
        return scoreTeamB;
    }

    public void setScoreTeamB(Integer scoreTeamB) {
        this.scoreTeamB = scoreTeamB;
    }
}