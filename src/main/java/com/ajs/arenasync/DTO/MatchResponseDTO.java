package com.ajs.arenasync.DTO;

import java.time.LocalDateTime;

import org.springframework.hateoas.RepresentationModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.v3.oas.annotations.media.Schema; // Importe esta anotação

@JsonInclude(Include.NON_NULL)
@Schema(description = "Detalhes da partida retornada pela API") // Anotação na classe do DTO
public class MatchResponseDTO extends RepresentationModel<MatchResponseDTO>{

    @Schema(description = "ID único da partida", example = "1")
    private Long id;

    @Schema(description = "ID da Equipe A", example = "1") // Adicionado teamAId
    private Long teamAId;

    @Schema(description = "ID da Equipe B", example = "2") // Adicionado teamBId
    private Long teamBId;

    @Schema(description = "ID do Torneio ao qual a partida pertence", example = "101") // Adicionado tournamentId
    private Long tournamentId;

    @Schema(description = "ID do local ou plataforma onde a partida será realizada", example = "201") // Adicionado locationPlatformId
    private Long locationPlatformId;

    @Schema(description = "Nome da Equipe A", example = "Time Alfa")
    private String teamAName;

    @Schema(description = "Nome da Equipe B", example = "Time Beta")
    private String teamBName;

    @Schema(description = "Nome do Torneio ao qual a partida pertence", example = "Campeonato de Inverno")
    private String tournamentName;

    @Schema(description = "Nome do local ou plataforma onde a partida será realizada", example = "Arena Virtual")
    private String locationPlatformName;

    @Schema(description = "Data e hora agendada da partida", example = "2025-07-01T18:00:00")
    private LocalDateTime scheduledDateTime;

    @Schema(description = "Pontuação da Equipe A", example = "3")
    private Integer scoreTeamA;

    @Schema(description = "Pontuação da Equipe B", example = "1")
    private Integer scoreTeamB;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    // Novos getters e setters para os IDs
    public Long getTeamAId() {
        return teamAId;
    }
    public void setTeamAId(Long teamAId) {
        this.teamAId = teamAId;
    }
    public Long getTeamBId() {
        return teamBId;
    }
    public void setTeamBId(Long teamBId) {
        this.teamBId = teamBId;
    }
    public Long getTournamentId() {
        return tournamentId;
    }
    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }
    public Long getLocationPlatformId() {
        return locationPlatformId;
    }
    public void setLocationPlatformId(Long locationPlatformId) {
        this.locationPlatformId = locationPlatformId;
    }

    public String getTeamAName() {
        return teamAName;
    }
    public void setTeamAName(String teamAName) {
        this.teamAName = teamAName;
    }
    public String getTeamBName() {
        return teamBName;
    }
    public void setTeamBName(String teamBName) {
        this.teamBName = teamBName;
    }
    public String getTournamentName() {
        return tournamentName;
    }
    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }
    public String getLocationPlatformName() {
        return locationPlatformName;
    }
    public void setLocationPlatformName(String locationPlatformName) {
        this.locationPlatformName = locationPlatformName;
    }
    public LocalDateTime getScheduledDateTime() {
        return scheduledDateTime;
    }
    public void setScheduledDateTime(LocalDateTime scheduledDateTime) {
        this.scheduledDateTime = scheduledDateTime;
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
