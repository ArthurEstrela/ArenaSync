package com.ajs.arenasync.DTO;

import jakarta.validation.constraints.NotNull;

public class StatisticRequestDTO {

    @NotNull(message = "O ID do jogador é obrigatório.")
    private Long playerId;

    @NotNull(message = "O ID da partida é obrigatório.")
    private Long matchId;

    private int points;
    private int assists;
    private int rebounds;
    
    public Long getPlayerId() {
        return playerId;
    }
    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }
    public Long getMatchId() {
        return matchId;
    }
    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }
    public int getPoints() {
        return points;
    }
    public void setPoints(int points) {
        this.points = points;
    }
    public int getAssists() {
        return assists;
    }
    public void setAssists(int assists) {
        this.assists = assists;
    }
    public int getRebounds() {
        return rebounds;
    }
    public void setRebounds(int rebounds) {
        this.rebounds = rebounds;
    }

    
}