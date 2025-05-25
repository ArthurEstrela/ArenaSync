package com.ajs.arenasync.DTO;

import com.ajs.arenasync.Entities.Enums.Status;
import jakarta.validation.constraints.NotNull;

public class EnrollmentRequestDTO {

    @NotNull(message = "O status da inscrição é obrigatório.")
    private Status status;

    @NotNull
    private Long teamId;

    @NotNull
    private Long tournamentId;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public Long getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }

    
}