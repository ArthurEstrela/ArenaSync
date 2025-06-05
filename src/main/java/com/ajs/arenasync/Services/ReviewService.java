package com.ajs.arenasync.Services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import com.ajs.arenasync.DTO.ReviewRequestDTO;
import com.ajs.arenasync.DTO.ReviewResponseDTO;
import com.ajs.arenasync.Entities.Match;
import com.ajs.arenasync.Entities.Review;
import com.ajs.arenasync.Entities.User;
import com.ajs.arenasync.Exceptions.BadRequestException;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.ajs.arenasync.Repositories.MatchRepository;
import com.ajs.arenasync.Repositories.ReviewRepository;
import com.ajs.arenasync.Repositories.UserRepository;

@Service
@CacheConfig(cacheNames = "reviews") // define o nome do cache
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MatchRepository matchRepository;

    @CacheEvict(value = "reviews", allEntries = true)
    public ReviewResponseDTO save(ReviewRequestDTO dto) {
        validateReview(dto);

        Review review = toEntity(dto);
        Review saved = reviewRepository.save(review);

        return toResponseDTO(saved);
    }

    @Cacheable(key = "#id")
    public ReviewResponseDTO findById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Avaliação", id));
        return toResponseDTO(review);
    }

    @Cacheable
    public List<ReviewResponseDTO> findAll() {
        return reviewRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Caching(evict = {
        @CacheEvict(key = "#id"),
        @CacheEvict(allEntries = true)
    })
    public void deleteById(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new ResourceNotFoundException("Avaliação", id);
        }
        reviewRepository.deleteById(id);
    }

    private void validateReview(ReviewRequestDTO dto) {
        if (dto.getMatchId() == null) {
            throw new BadRequestException("É necessário informar o ID da partida para avaliar.");
        }

        if (reviewRepository.existsByUserIdAndMatchId(dto.getUserId(), dto.getMatchId())) {
            throw new BadRequestException("Você já avaliou esta partida.");
        }

        if (dto.getRating() == null || dto.getRating() < 1 || dto.getRating() > 5) {
            throw new BadRequestException("A nota da avaliação deve estar entre 1 e 5.");
        }
    }

    private Review toEntity(ReviewRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new BadRequestException("Usuário não encontrado."));

        Match match = matchRepository.findById(dto.getMatchId())
                .orElseThrow(() -> new BadRequestException("Partida não encontrada."));

        Review review = new Review();
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setUser(user);
        review.setMatch(match);

        return review;
    }

    private ReviewResponseDTO toResponseDTO(Review review) {
        ReviewResponseDTO dto = new ReviewResponseDTO();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setUserName(review.getUser().getName());
        dto.setMatchInfo("Partida ID: " + review.getMatch().getId());
        return dto;
    }
}
