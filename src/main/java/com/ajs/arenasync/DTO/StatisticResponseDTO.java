package com.ajs.arenasync.DTO;

import org.springframework.hateoas.RepresentationModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.v3.oas.annotations.media.Schema; // Importe esta anotação

@JsonInclude(Include.NON_NULL)
@Schema(description = "Detalhes da estatística retornada pela API") // Anotação na classe do DTO
public class StatisticResponseDTO extends RepresentationModel<StatisticResponseDTO> {

    @Schema(description = "ID único da estatística", example = "1")
    private Long id;

    @Schema(description = "Nome do jogador associado à estatística", example = "Player Teste")
    private String playerName;

    @Schema(description = "Informações sobre a partida (se aplicável)", example = "Partida ID: 10")
    private String matchInfo; // Certifique-se de que este campo é preenchido pelo seu serviço se for relevante

    @Schema(description = "Total de jogos jogados", example = "25")
    private int gamesPlayed;

    @Schema(description = "Número de vitórias", example = "15")
    private int wins;

    @Schema(description = "Pontuação total", example = "1250")
    private int score;

    // Note: os campos 'points', 'assists', 'rebounds' estavam no DTO mas não no Service/Entity que você forneceu anteriormente.
    // Deixei-os aqui, mas se não forem usados, você pode removê-los.
    @Schema(description = "Pontos feitos pelo jogador (opcional)", example = "100")
    private int points;
    @Schema(description = "Assistências (opcional)", example = "15")
    private int assists;
    @Schema(description = "Rebotes (opcional)", example = "20")
    private int rebounds;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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