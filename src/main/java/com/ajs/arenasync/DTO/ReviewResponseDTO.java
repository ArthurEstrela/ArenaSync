package com.ajs.arenasync.DTO;

import org.springframework.hateoas.RepresentationModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.v3.oas.annotations.media.Schema; // Importe esta anotação

@JsonInclude(Include.NON_NULL)
@Schema(description = "Detalhes da avaliação retornada pela API") // Anotação na classe do DTO
public class ReviewResponseDTO extends RepresentationModel<ReviewResponseDTO> {

    @Schema(description = "ID único da avaliação", example = "1")
    private Long id;

    @Schema(description = "Nota da avaliação (de 1 a 5)", example = "5")
    private Integer rating;

    @Schema(description = "Comentário da avaliação", example = "Excelente partida, muito bem organizada!")
    private String comment;

    @Schema(description = "ID do usuário que fez a avaliação", example = "101") // Adicionado userId
    private Long userId;

    @Schema(description = "ID da partida avaliada", example = "201") // Adicionado matchId
    private Long matchId;

    @Schema(description = "ID do torneio avaliado", example = "301") // Adicionado tournamentId
    private Long tournamentId;

    @Schema(description = "Nome do usuário que fez a avaliação", example = "Nome do Usuário")
    private String userName;

    @Schema(description = "Informações sobre a partida (se aplicável)", example = "Partida ID: 201")
    private String matchInfo; // Informações sobre a partida ou torneio avaliado

    @Schema(description = "Nome do torneio avaliado (se aplicável)", example = "Campeonato Primavera")
    private String tournamentName; // Pode ser preenchido se a review for de um torneio

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Integer getRating() {
        return rating;
    }
    public void setRating(Integer rating) {
        this.rating = rating;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

    // Novos getters e setters para os IDs
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public Long getMatchId() {
        return matchId;
    }
    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }
    public Long getTournamentId() {
        return tournamentId;
    }
    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getMatchInfo() {
        return matchInfo;
    }
    public void setMatchInfo(String matchInfo) {
        this.matchInfo = matchInfo;
    }
    public String getTournamentName() {
        return tournamentName;
    }
    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }
}
