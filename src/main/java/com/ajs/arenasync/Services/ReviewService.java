package com.ajs.arenasync.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ajs.arenasync.Entities.Review;
import com.ajs.arenasync.Exceptions.BadRequestException;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.ajs.arenasync.Repositories.ReviewRepository;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public Review save(Review review) {
        validateReview(review);
        return reviewRepository.save(review);
    }

    public Review findById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Avaliação", id));
    }

    public void deleteById(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new ResourceNotFoundException("Avaliação", id);
        }
        reviewRepository.deleteById(id);
    }

    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    private void validateReview(Review review) {
        if (review.getRating() == null) {
            throw new BadRequestException("A nota da avaliação é obrigatória.");
        }

        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new BadRequestException("A nota deve estar entre 1 e 5.");
        }

        if (review.getUser() == null) {
            throw new BadRequestException("O usuário da avaliação deve ser informado.");
        }

        if (review.getTournament() == null) {
            throw new BadRequestException("O torneio da avaliação deve ser informado.");
        }
    }
}
