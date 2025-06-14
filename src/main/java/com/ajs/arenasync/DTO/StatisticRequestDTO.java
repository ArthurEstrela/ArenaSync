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

    @NotNull(message = "O número de assistências é obrigatório.") // Adicionado NotNull e mensagem
    @Min(value = 0, message = "O número de assistências não pode ser negativo.") // Adicionado Min e mensagem
    @Schema(description = "Número de assistências concedidas pelo jogador", example = "10")
    private Integer assists; // Adicionado o campo assists

    @NotNull(message = "O ID do jogador é obrigatório.")
    @Schema(description = "ID do jogador ao qual a estatística está associada", example = "1")
    private Long playerId;

    @Schema(description = "ID da partida à qual a estatística está associada (opcional)", example = "201")
    private Long matchId;

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

    // Getter e Setter para assists
    public Integer getAssists() {
        return assists;
    }

    public void setAssists(Integer assists) {
        this.assists = assists;
    }

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
}
