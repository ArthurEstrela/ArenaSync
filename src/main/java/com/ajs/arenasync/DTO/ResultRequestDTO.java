package com.ajs.arenasync.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ResultRequestDTO {

    @NotNull(message = "O ID da partida é obrigatório.")
    private Long matchId;

    @NotNull(message = "O placar do time A é obrigatório.")
    @Min(value = 0, message = "O placar do time A não pode ser negativo.")
    private Integer scoreTeamA;

    @NotNull(message = "O placar do time B é obrigatório.")
    @Min(value = 0, message = "O placar do time B não pode ser negativo.")
    private Integer scoreTeamB;

    // Getters e Setters

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
