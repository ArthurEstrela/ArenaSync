package com.ajs.arenasync.DTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReviewRequestDTO {

    @NotNull(message = "A nota é obrigatória.")
    @Min(value = 1, message = "A nota mínima é 1.")
    @Max(value = 5, message = "A nota máxima é 5.")
    private Integer rating;

    @Size(max = 255, message = "O comentário pode ter no máximo 255 caracteres.")
    private String comment;

    @NotNull(message = "O ID do usuário é obrigatório.")
    private Long userId;

    private Long matchId;     // Avaliação da partida (opcional)
    private Long tournamentId; // Avaliação do torneio (opcional)


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

   
}
