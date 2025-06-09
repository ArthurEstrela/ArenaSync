package com.ajs.arenasync.Services;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MatchRepository matchRepository;

    @InjectMocks
    private ReviewService reviewService;

    private User user;
    private Match match;
    private Review review;
    private ReviewRequestDTO reviewRequestDTO;
    private Long userId = 1L;
    private Long matchId = 1L;
    private Long reviewId = 1L;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(userId);
        user.setName("Test User");

        match = new Match();
        match.setId(matchId);

        review = new Review();
        review.setId(reviewId);
        review.setRating(5);
        review.setComment("Great match!");
        review.setUser(user);
        review.setMatch(match);

        reviewRequestDTO = new ReviewRequestDTO();
        reviewRequestDTO.setUserId(userId);
        reviewRequestDTO.setMatchId(matchId);
        reviewRequestDTO.setRating(5);
        reviewRequestDTO.setComment("Great DTO comment!");
    }

    @Test
    void testSave_Success() {
        // CORREÇÃO: existeByUserIdAndMatchId deve ser mockado para falso, pois a validação é executada antes da busca de user/match
        when(reviewRepository.existsByUserIdAndMatchId(userId, matchId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> {
            Review saved = invocation.getArgument(0);
            saved.setId(reviewId);
            return saved;
        });

        ReviewResponseDTO responseDTO = reviewService.save(reviewRequestDTO);

        assertNotNull(responseDTO);
        assertEquals(reviewRequestDTO.getRating(), responseDTO.getRating());
        assertEquals(user.getName(), responseDTO.getUserName());
        assertTrue(responseDTO.getMatchInfo().contains("Partida ID: " + matchId));
        verify(userRepository, times(1)).findById(userId);
        verify(matchRepository, times(1)).findById(matchId);
        verify(reviewRepository, times(1)).existsByUserIdAndMatchId(userId, matchId);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void testSave_UserNotFound() {
        // CORREÇÃO: existsByUserIdAndMatchId deve ser mockado para falso
        when(reviewRepository.existsByUserIdAndMatchId(userId, matchId)).thenReturn(false); // Adicionado para evitar NeverWantedButInvoked
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> {
            reviewService.save(reviewRequestDTO);
        });
        verify(userRepository, times(1)).findById(userId);
        verify(matchRepository, never()).findById(anyLong());
        verify(reviewRepository, times(1)).existsByUserIdAndMatchId(userId, matchId); // Agora é esperado
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void testSave_MatchNotFound() {
        // CORREÇÃO: existsByUserIdAndMatchId deve ser mockado para falso
        when(reviewRepository.existsByUserIdAndMatchId(userId, matchId)).thenReturn(false); // Adicionado para evitar NeverWantedButInvoked
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(matchRepository.findById(matchId)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> {
            reviewService.save(reviewRequestDTO);
        });
        verify(userRepository, times(1)).findById(userId);
        verify(matchRepository, times(1)).findById(matchId);
        verify(reviewRepository, times(1)).existsByUserIdAndMatchId(userId, matchId); // Agora é esperado
        verify(reviewRepository, never()).save(any(Review.class));
    }
    
    @Test
    void testSave_MatchIdNull() {
        reviewRequestDTO.setMatchId(null);
        
        assertThrows(BadRequestException.class, () -> {
            reviewService.save(reviewRequestDTO);
        });

        verify(userRepository, never()).findById(anyLong());
        verify(matchRepository, never()).findById(anyLong());
        verify(reviewRepository, never()).existsByUserIdAndMatchId(anyLong(), anyLong());
        verify(reviewRepository, never()).save(any(Review.class));
    }


    @Test
    void testSave_UserAlreadyReviewedMatch() {
        when(reviewRepository.existsByUserIdAndMatchId(userId, matchId)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            reviewService.save(reviewRequestDTO);
        });
        verify(userRepository, never()).findById(userId);
        verify(matchRepository, never()).findById(matchId);
        verify(reviewRepository, times(1)).existsByUserIdAndMatchId(userId, matchId);
        verify(reviewRepository, never()).save(any(Review.class));
    }


    @Test
    void testSave_InvalidRatingTooLow() {
        reviewRequestDTO.setRating(0);
        
        assertThrows(BadRequestException.class, () -> {
            reviewService.save(reviewRequestDTO);
        });
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void testSave_InvalidRatingTooHigh() {
        reviewRequestDTO.setRating(6);
        
        assertThrows(BadRequestException.class, () -> {
            reviewService.save(reviewRequestDTO);
        });
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void testFindById_Success() {
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        ReviewResponseDTO responseDTO = reviewService.findById(reviewId);

        assertNotNull(responseDTO);
        assertEquals(review.getRating(), responseDTO.getRating());
        assertEquals(user.getName(), responseDTO.getUserName());
        verify(reviewRepository, times(1)).findById(reviewId);
    }

    @Test
    void testFindById_NotFound() {
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            reviewService.findById(reviewId);
        });
        verify(reviewRepository, times(1)).findById(reviewId);
    }

    @Test
    void testFindAll_Success() {
        when(reviewRepository.findAll()).thenReturn(Collections.singletonList(review));

        List<ReviewResponseDTO> reviews = reviewService.findAll();

        assertNotNull(reviews);
        assertFalse(reviews.isEmpty());
        assertEquals(1, reviews.size());
        assertEquals(review.getComment(), reviews.get(0).getComment());
        verify(reviewRepository, times(1)).findAll();
    }
    
    @Test
    void testFindAll_Empty() {
        when(reviewRepository.findAll()).thenReturn(Collections.emptyList());

        List<ReviewResponseDTO> reviews = reviewService.findAll();

        assertNotNull(reviews);
        assertTrue(reviews.isEmpty());
        verify(reviewRepository, times(1)).findAll();
    }


    @Test
    void testDeleteById_Success() {
        when(reviewRepository.existsById(reviewId)).thenReturn(true);
        doNothing().when(reviewRepository).deleteById(reviewId);

        assertDoesNotThrow(() -> reviewService.deleteById(reviewId));

        verify(reviewRepository, times(1)).existsById(reviewId);
        verify(reviewRepository, times(1)).deleteById(reviewId);
    }

    @Test
    void testDeleteById_NotFound() {
        when(reviewRepository.existsById(reviewId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            reviewService.deleteById(reviewId);
        });
        verify(reviewRepository, times(1)).existsById(reviewId);
        verify(reviewRepository, never()).deleteById(anyLong());
    }
}