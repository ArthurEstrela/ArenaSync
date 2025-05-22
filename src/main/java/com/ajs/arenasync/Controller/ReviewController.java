package com.ajs.arenasync.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ajs.arenasync.Entities.Review;
import com.ajs.arenasync.Services.ReviewService;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // 🔹 Criar uma nova avaliação
    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody Review review) {
        Review savedReview = reviewService.save(review);
        return ResponseEntity.ok(savedReview);
    }

    // 🔹 Buscar avaliação por ID
    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable Long id) {
        Review review = reviewService.findById(id);
        return ResponseEntity.ok(review);
    }

    // 🔹 Listar todas as avaliações
    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews() {
        List<Review> reviews = reviewService.findAll();
        return ResponseEntity.ok(reviews);
    }

    // 🔹 Atualizar uma avaliação existente
    @PutMapping("/{id}")
    public ResponseEntity<Review> updateReview(@PathVariable Long id, @RequestBody Review updatedReview) {
        Review existingReview = reviewService.findById(id);
        updatedReview.setId(existingReview.getId());
        Review savedReview = reviewService.save(updatedReview);
        return ResponseEntity.ok(savedReview);
    }

    // 🔹 Deletar uma avaliação
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
