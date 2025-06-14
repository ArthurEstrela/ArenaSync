package com.ajs.arenasync.DTO;

import org.springframework.hateoas.RepresentationModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.v3.oas.annotations.media.Schema; // Importe esta anotação

@JsonInclude(Include.NON_NULL)
@Schema(description = "Detalhes da inscrição de time em torneio retornada pela API") // Anotação na classe do DTO
public class EnrollmentResponseDTO extends RepresentationModel<EnrollmentResponseDTO>{

    @Schema(description = "ID único da inscrição", example = "1")
    private Long id;

    @Schema(description = "ID do time inscrito", example = "1") // Adicionado teamId
    private Long teamId;

    @Schema(description = "ID do torneio da inscrição", example = "101") // Adicionado tournamentId
    private Long tournamentId;

    @Schema(description = "Nome do time inscrito", example = "Time Titans")
    private String teamName;

    @Schema(description = "Nome do torneio da inscrição", example = "Summer Cup")
    private String tournamentName;

    @Schema(description = "Status da inscrição (PENDING, APPROVED, REJECTED)", example = "APPROVED")
    private String status;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    // Novos getters e setters para teamId e tournamentId
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

    public String getTeamName() {
        return teamName;
    }
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
    public String getTournamentName() {
        return tournamentName;
    }
    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
