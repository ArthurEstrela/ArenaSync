package com.ajs.arenasync.DTO;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class MatchRequestDTO {

    @NotNull(message = "A data e hora da partida são obrigatórias.")
    @Future(message = "A data deve ser futura.")
    private LocalDateTime scheduledDateTime;

    @NotNull(message = "O ID da equipe A é obrigatório.")
    private Long teamAId;

    @NotNull(message = "O ID da equipe B é obrigatório.")
    private Long teamBId;

    @NotNull(message = "O ID do torneio é obrigatório.")
    private Long tournamentId;

    @NotNull(message = "O ID do local/plataforma é obrigatório.")
    private Long locationPlatformId;

    private Integer scoreTeamA;
    private Integer scoreTeamB;
    public LocalDateTime getScheduledDateTime() {
        return scheduledDateTime;
    }
    public void setScheduledDateTime(LocalDateTime scheduledDateTime) {
        this.scheduledDateTime = scheduledDateTime;
    }
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
