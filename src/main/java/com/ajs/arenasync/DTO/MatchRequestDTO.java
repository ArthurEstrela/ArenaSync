package com.ajs.arenasync.DTO;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema; // Importe esta anotação

@Schema(description = "Detalhes para a criação de uma partida") // Anotação na classe do DTO
public class MatchRequestDTO {

    @NotNull(message = "A data e hora da partida são obrigatórias.")
    @Future(message = "A data deve ser futura.")
    @Schema(description = "Data e hora agendada da partida (deve ser futura)", example = "2025-07-01T18:00:00")
    private LocalDateTime scheduledDateTime;

    @NotNull(message = "O ID da equipe A é obrigatório.")
    @Schema(description = "ID da Equipe A", example = "1")
    private Long teamAId;

    @NotNull(message = "O ID da equipe B é obrigatório.")
    @Schema(description = "ID da Equipe B", example = "2")
    private Long teamBId;

    @NotNull(message = "O ID do torneio é obrigatório.")
    @Schema(description = "ID do Torneio ao qual a partida pertence", example = "101")
    private Long tournamentId;

    @NotNull(message = "O ID do local/plataforma é obrigatório.")
    @Schema(description = "ID do local ou plataforma onde a partida será realizada", example = "201")
    private Long locationPlatformId;

    @Schema(description = "Pontuação da Equipe A (opcional, pode ser nulo antes da partida)", example = "3")
    private Integer scoreTeamA;

    @Schema(description = "Pontuação da Equipe B (opcional, pode ser nulo antes da partida)", example = "1")
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