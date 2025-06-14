package com.ajs.arenasync.DTO;

import org.springframework.hateoas.RepresentationModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(Include.NON_NULL)
@Schema(description = "Detalhes da estatística retornada pela API")
public class StatisticResponseDTO extends RepresentationModel<StatisticResponseDTO> {

    @Schema(description = "ID único da estatística", example = "1")
    private Long id;

    @Schema(description = "ID do jogador associado à estatística", example = "1")
    private Long playerId;

    @Schema(description = "ID da partida associada à estatística (se aplicável)", example = "10")
    private Long matchId;

    @Schema(description = "Nome do jogador associado à estatística", example = "Player Teste")
    private String playerName;

    @Schema(description = "Informações sobre a partida (se aplicável)", example = "Partida ID: 10")
    private String matchInfo;

    @Schema(description = "Total de jogos jogados", example = "25")
    private int gamesPlayed;

    @Schema(description = "Número de vitórias", example = "15")
    private int wins;

    @Schema(description = "Pontuação total", example = "1250")
    private int score;

    @Schema(description = "Assistências concedidas pelo jogador", example = "15") // Schema atualizado
    private int assists; // Mantido, pois existe na entidade

    // Os campos 'points' e 'rebounds' foram removidos para consistência com a entidade Statistic.


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getMatchInfo() {
        return matchInfo;
    }

    public void setMatchInfo(String matchInfo) {
        this.matchInfo = matchInfo;
    }

    public int getAssists() { // Getter para assists
        return assists;
    }

    public void setAssists(int assists) { // Setter para assists
        this.assists = assists;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
