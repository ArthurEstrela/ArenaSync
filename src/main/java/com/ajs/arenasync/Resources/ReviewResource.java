package com.ajs.arenasync.Resources;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ajs.arenasync.Entities.Review;
import com.ajs.arenasync.Services.ReviewService;

@RestController
@RequestMapping("/reviews")
public class ReviewResource {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/{id}")
    public ResponseEntity<Review> findById(@PathVariable Long id) {
        Optional<Review> obj = reviewService.findById(id);
        return obj.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Review> insert(@RequestBody Review review) {
        Review savedReview = reviewService.save(review);
        return ResponseEntity.ok(savedReview);
    }

    @PutMapping("/{id}")
public ResponseEntity<Review> update(@PathVariable Long id, @RequestBody Review review) {
    Optional<Review> obj = reviewService.findById(id);
    if (obj.isEmpty()) {
        return ResponseEntity.notFound().build();
    }
    review.setId(id);
    Review updatedReview = reviewService.save(review);
    return ResponseEntity.ok(updatedReview);
}

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reviewService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}