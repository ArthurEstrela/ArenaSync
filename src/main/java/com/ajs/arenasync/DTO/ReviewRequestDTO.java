package com.ajs.arenasync.DTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema; // Importe esta anotação

@Schema(description = "Detalhes para a criação de uma avaliação") // Anotação na classe do DTO
public class ReviewRequestDTO {

    @NotNull(message = "A nota é obrigatória.")
    @Min(value = 1, message = "A nota mínima é 1.")
    @Max(value = 5, message = "A nota máxima é 5.")
    @Schema(description = "Nota da avaliação (de 1 a 5)", example = "5")
    private Integer rating;

    @Size(max = 255, message = "O comentário pode ter no máximo 255 caracteres.")
    @Schema(description = "Comentário da avaliação (opcional, máximo 255 caracteres)", example = "Excelente partida, muito bem organizada!")
    private String comment;

    @NotNull(message = "O ID do usuário é obrigatório.")
    @Schema(description = "ID do usuário que está fazendo a avaliação", example = "101")
    private Long userId;

    @Schema(description = "ID da partida que está sendo avaliada (informe matchId OU tournamentId, não ambos)", example = "201")
    private Long matchId;     // Avaliação da partida (opcional)

    @Schema(description = "ID do torneio que está sendo avaliado (informe matchId OU tournamentId, não ambos)", example = "301")
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