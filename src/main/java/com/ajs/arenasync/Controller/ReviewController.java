package com.ajs.arenasync.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ajs.arenasync.DTO.ReviewRequestDTO;
import com.ajs.arenasync.DTO.ReviewResponseDTO;
import com.ajs.arenasync.Services.ReviewService;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation; // Importe esta anotação
import io.swagger.v3.oas.annotations.tags.Tag; // Importe esta anotação
import io.swagger.v3.oas.annotations.Parameter; // Importe esta anotação para documentar PathVariable

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Review Management", description = "Operações para gerenciar avaliações de partidas e torneios") // Anotação na classe
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    @Operation(summary = "Criar nova avaliação", description = "Cria uma nova avaliação para uma partida ou torneio por um usuário")
    public ResponseEntity<ReviewResponseDTO> createReview(@Valid @RequestBody ReviewRequestDTO dto) {
        ReviewResponseDTO created = reviewService.save(dto);
        created.add(linkTo(methodOn(ReviewController.class).getReviewById(created.getId())).withSelfRel());
        
        // Adicionar link para o usuário que fez a review
        if (dto.getUserId() != null) {
            try {
                created.add(linkTo(methodOn(UserController.class).getUserById(dto.getUserId())).withRel("user"));
            } catch (Exception e) {
                System.err.println("Erro ao tentar gerar link para user em createReview: " + e.getMessage());
            }
        }
        // Adicionar link para a partida ou torneio avaliado
        if (dto.getMatchId() != null) {
             try {
                created.add(linkTo(methodOn(MatchController.class).findById(dto.getMatchId())).withRel("match"));
            } catch (Exception e) {
                System.err.println("Erro ao tentar gerar link para match em createReview: " + e.getMessage());
            }
        } else if (dto.getTournamentId() != null) {
            try {
                created.add(linkTo(methodOn(TournamentController.class).getTournamentById(dto.getTournamentId())).withRel("tournament"));
            } catch (Exception e) {
                System.err.println("Erro ao tentar gerar link para tournament em createReview: " + e.getMessage());
            }
        }
        created.add(linkTo(methodOn(ReviewController.class).getAllReviews()).withRel("all-reviews"));
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter avaliação por ID", description = "Retorna uma avaliação específica com base no seu ID")
    public ResponseEntity<ReviewResponseDTO> getReviewById(
            @Parameter(description = "ID da avaliação a ser buscada", required = true) @PathVariable Long id) {
        ReviewResponseDTO review = reviewService.findById(id);
        review.add(linkTo(methodOn(ReviewController.class).getReviewById(id)).withSelfRel());
        review.add(linkTo(methodOn(ReviewController.class).getAllReviews()).withRel("all-reviews"));
        review.add(linkTo(methodOn(ReviewController.class).deleteReview(id)).withRel("delete"));


        // Links para recursos relacionados (User, Match/Tournament)
        // Se ReviewResponseDTO tivesse os IDs do usuário, partida ou torneio, você poderia adicionar os links aqui.
        // Exemplo:
        // if (review.getUserId() != null) {
        //     try { review.add(linkTo(methodOn(UserController.class).getUserById(review.getUserId())).withRel("user")); } catch (Exception e) { }
        // }
        // if (review.getMatchId() != null) { // Supondo que você adicionasse getMatchId() ao ReviewResponseDTO
        //     try { review.add(linkTo(methodOn(MatchController.class).findById(review.getMatchId())).withRel("match")); } catch (Exception e) { }
        // } else if (review.getTournamentId() != null) { // Supondo que você adicionasse getTournamentId() ao ReviewResponseDTO
        //     try { review.add(linkTo(methodOn(TournamentController.class).getTournamentById(review.getTournamentId())).withRel("tournament")); } catch (Exception e) { }
        // }

        return ResponseEntity.ok(review);
    }

    @GetMapping
    @Operation(summary = "Listar todas as avaliações", description = "Retorna uma lista de todas as avaliações registradas")
    public ResponseEntity<CollectionModel<ReviewResponseDTO>> getAllReviews() {
        List<ReviewResponseDTO> reviewsList = reviewService.findAll();
        
        for (ReviewResponseDTO review : reviewsList) {
            review.add(linkTo(methodOn(ReviewController.class).getReviewById(review.getId())).withSelfRel());
            review.add(linkTo(methodOn(ReviewController.class).deleteReview(review.getId())).withRel("delete"));
            // Adicionar outros links relevantes para cada item, se necessário (user, match/tournament)
        }
        Link selfLink = linkTo(methodOn(ReviewController.class).getAllReviews()).withSelfRel();
        CollectionModel<ReviewResponseDTO> collectionModel = CollectionModel.of(reviewsList, selfLink);
        
        return ResponseEntity.ok(collectionModel);
    }

    // DELETE
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar avaliação", description = "Deleta uma avaliação do sistema pelo seu ID")
    public ResponseEntity<Void> deleteReview(
            @Parameter(description = "ID da avaliação a ser deletada", required = true) @PathVariable Long id) {
        reviewService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}