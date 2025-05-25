package com.ajs.arenasync.DTO;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class MatchRequestDTO {

    @NotNull
    @Future(message = "A data da partida deve ser futura.")
    private LocalDateTime scheduledDateTime;

    @NotNull
    private Long teamAId;

    @NotNull
    private Long teamBId;

    @NotNull
    private Long tournamentId;

    @NotNull
    private Long locationPlatformId;

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

    
}