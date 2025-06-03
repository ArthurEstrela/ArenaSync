package com.ajs.arenasync.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class StatisticRequestDTO {

    @NotNull(message = "O número de partidas jogadas é obrigatório.")
    @Min(value = 0, message = "O número de partidas jogadas não pode ser negativo.")
    private Integer gamesPlayed;

    @NotNull(message = "O número de vitórias é obrigatório.")
    @Min(value = 0, message = "O número de vitórias não pode ser negativo.")
    private Integer wins;

    @NotNull(message = "A pontuação é obrigatória.")
    @Min(value = 0, message = "A pontuação não pode ser negativa.")
    private Integer score;

    @NotNull(message = "O ID do jogador é obrigatório.")
    private Long playerId;

    // Getters e Setters
    public Integer getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(Integer gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public Integer getWins() {
        return wins;
    }

    public void setWins(Integer wins) {
        this.wins = wins;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }
}
