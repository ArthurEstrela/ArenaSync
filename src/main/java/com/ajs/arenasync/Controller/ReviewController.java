package com.ajs.arenasync.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*; // Import estático

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ajs.arenasync.DTO.ReviewRequestDTO;
import com.ajs.arenasync.DTO.ReviewResponseDTO;
import com.ajs.arenasync.Services.ReviewService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponseDTO> createReview(@Valid @RequestBody ReviewRequestDTO dto) {
        ReviewResponseDTO created = reviewService.save(dto);
        // Adicionar link "self"
        created.add(linkTo(methodOn(ReviewController.class).getReviewById(created.getId())).withSelfRel());
        
        // Adicionar link para o usuário que fez a review
        // Para isso, precisaríamos do ID do usuário. Se ReviewRequestDTO tem userId:
        if (dto.getUserId() != null) {
            try {
                created.add(linkTo(methodOn(UserController.class).getUserById(dto.getUserId())).withRel("user"));
            } catch (Exception e) {
                System.err.println("Erro ao tentar gerar link para user em createReview: " + e.getMessage());
            }
        }
        // Adicionar link para a partida ou torneio avaliado
        // Se ReviewRequestDTO tem matchId ou tournamentId:
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
    public ResponseEntity<ReviewResponseDTO> getReviewById(@PathVariable Long id) {
        ReviewResponseDTO review = reviewService.findById(id);
        // Adicionar link "self"
        review.add(linkTo(methodOn(ReviewController.class).getReviewById(id)).withSelfRel());
        review.add(linkTo(methodOn(ReviewController.class).getAllReviews()).withRel("all-reviews"));

        // Para adicionar links ao usuário e à partida/torneio aqui,
        // o ReviewResponseDTO precisaria conter os IDs ou o ReviewService precisaria
        // fornecer informações para construir esses links.
        // Exemplo simplificado (supondo que ReviewResponseDTO tenha esses IDs):
        // if (review.getUserId() != null) {
        //     review.add(linkTo(methodOn(UserController.class).getUserById(review.getUserId())).withRel("user"));
        // }
        // if ("match".equals(review.getReviewedEntityType()) && review.getReviewedEntityId() != null) {
        //     review.add(linkTo(methodOn(MatchController.class).findById(review.getReviewedEntityId())).withRel("match"));
        // }

        return ResponseEntity.ok(review);
    }

    @GetMapping
    public ResponseEntity<CollectionModel<ReviewResponseDTO>> getAllReviews() {
        List<ReviewResponseDTO> reviewsList = reviewService.findAll();
        
        for (ReviewResponseDTO review : reviewsList) {
            review.add(linkTo(methodOn(ReviewController.class).getReviewById(review.getId())).withSelfRel());
            // Adicionar outros links relevantes para cada item, se necessário (user, match/tournament)
        }
        Link selfLink = linkTo(methodOn(ReviewController.class).getAllReviews()).withSelfRel();
        CollectionModel<ReviewResponseDTO> collectionModel = CollectionModel.of(reviewsList, selfLink);
        
        return ResponseEntity.ok(collectionModel);
    }

    // DELETE - Respostas 204 não têm corpo para links.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}