package com.ajs.arenasync.DTO;

import org.springframework.hateoas.RepresentationModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.v3.oas.annotations.media.Schema; // Importe esta anotação

@JsonInclude(Include.NON_NULL)
@Schema(description = "Detalhes do resultado da partida retornado pela API") // Anotação na classe do DTO
public class ResultResponseDTO extends RepresentationModel<ResultResponseDTO>{
    
    @Schema(description = "ID único do resultado", example = "1")
    private Long id;

    @Schema(description = "ID da partida associada a este resultado", example = "101")
    private Long matchId;

    @Schema(description = "Pontuação final do Time A", example = "3")
    private Integer scoreTeamA;

    @Schema(description = "Pontuação final do Time B", example = "1")
    private Integer scoreTeamB;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
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