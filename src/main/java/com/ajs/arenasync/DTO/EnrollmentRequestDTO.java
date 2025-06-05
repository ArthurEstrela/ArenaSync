package com.ajs.arenasync.DTO;

import com.ajs.arenasync.Entities.Enums.Status;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema; // Importe esta anotação

@Schema(description = "Detalhes para a criação de uma inscrição de time em torneio") // Anotação na classe do DTO
public class EnrollmentRequestDTO {

    @NotNull(message = "O status da inscrição é obrigatório.")
    @Schema(description = "Status da inscrição (PENDING, APPROVED, REJECTED)", example = "PENDING", allowableValues = {"PENDING", "APPROVED", "REJECTED"})
    private Status status;

    @NotNull(message = "O ID do time é obrigatório.")
    @Schema(description = "ID do time que está se inscrevendo", example = "1")
    private Long teamId;

    @NotNull(message = "O ID do torneio é obrigatório.")
    @Schema(description = "ID do torneio no qual o time está se inscrevendo", example = "101")
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