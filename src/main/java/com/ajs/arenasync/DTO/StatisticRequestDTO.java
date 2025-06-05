package com.ajs.arenasync.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema; // Importe esta anotação

@Schema(description = "Detalhes para a criação ou atualização de uma estatística") // Anotação na classe do DTO
public class StatisticRequestDTO {

    @NotNull(message = "O número de partidas jogadas é obrigatório.")
    @Min(value = 0, message = "O número de partidas jogadas não pode ser negativo.")
    @Schema(description = "Número de partidas jogadas", example = "25")
    private Integer gamesPlayed;

    @NotNull(message = "O número de vitórias é obrigatório.")
    @Min(value = 0, message = "O número de vitórias não pode ser negativo.")
    @Schema(description = "Número de vitórias", example = "15")
    private Integer wins;

    @NotNull(message = "A pontuação é obrigatória.")
    @Min(value = 0, message = "A pontuação não pode ser negativa.")
    @Schema(description = "Pontuação total do jogador", example = "1250")
    private Integer score;

    @NotNull(message = "O ID do jogador é obrigatório.")
    @Schema(description = "ID do jogador ao qual a estatística está associada", example = "1")
    private Long playerId;

    // Getters e Setters (já existentes)
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