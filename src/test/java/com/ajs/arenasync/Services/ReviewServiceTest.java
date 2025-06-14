package com.ajs.arenasync.Services;

import com.ajs.arenasync.DTO.ReviewRequestDTO;
import com.ajs.arenasync.DTO.ReviewResponseDTO;
import com.ajs.arenasync.Entities.Match;
import com.ajs.arenasync.Entities.Review;
import com.ajs.arenasync.Entities.User;
import com.ajs.arenasync.Entities.Tournament; // Importe Tournament
import com.ajs.arenasync.Exceptions.BadRequestException;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.ajs.arenasync.Repositories.MatchRepository;
import com.ajs.arenasync.Repositories.ReviewRepository;
import com.ajs.arenasync.Repositories.UserRepository;
import com.ajs.arenasync.Repositories.TournamentRepository; // Importe TournamentRepository
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

    @Mock
    private TournamentRepository tournamentRepository; // Mock do TournamentRepository

    @InjectMocks
    private ReviewService reviewService;

    private User user;
    private Match match;
    private Tournament tournament; // Objeto Tournament para testes
    private Review reviewMatch; // Review para partida
    private Review reviewTournament; // Review para torneio
    private ReviewRequestDTO reviewRequestDTOForMatch;
    private ReviewRequestDTO reviewRequestDTOForTournament;
    private Long userId = 1L;
    private Long matchId = 1L;
    private Long tournamentId = 2L; // ID diferente para torneio
    private Long reviewMatchId = 1L;
    private Long reviewTournamentId = 2L;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(userId);
        user.setName("Test User");

        match = new Match();
        match.setId(matchId);

        tournament = new Tournament(); // Inicializa o objeto Tournament
        tournament.setId(tournamentId);
        tournament.setName("Test Tournament");

        reviewMatch = new Review();
        reviewMatch.setId(reviewMatchId);
        reviewMatch.setRating(5);
        reviewMatch.setComment("Great match!");
        reviewMatch.setUser(user);
        reviewMatch.setMatch(match);
        reviewMatch.setTournament(null); // Explicitamente nulo para review de partida

        reviewTournament = new Review();
        reviewTournament.setId(reviewTournamentId);
        reviewTournament.setRating(4);
        reviewTournament.setComment("Good tournament!");
        reviewTournament.setUser(user);
        reviewTournament.setMatch(null); // Explicitamente nulo para review de torneio
        reviewTournament.setTournament(tournament);

        reviewRequestDTOForMatch = new ReviewRequestDTO();
        reviewRequestDTOForMatch.setUserId(userId);
        reviewRequestDTOForMatch.setMatchId(matchId);
        reviewRequestDTOForMatch.setRating(5);
        reviewRequestDTOForMatch.setComment("Great DTO comment!");
        reviewRequestDTOForMatch.setTournamentId(null); // Garante que apenas matchId é setado

        reviewRequestDTOForTournament = new ReviewRequestDTO();
        reviewRequestDTOForTournament.setUserId(userId);
        reviewRequestDTOForTournament.setTournamentId(tournamentId);
        reviewRequestDTOForTournament.setRating(4);
        reviewRequestDTOForTournament.setComment("Good tournament DTO comment!");
        reviewRequestDTOForTournament.setMatchId(null); // Garante que apenas tournamentId é setado
    }

    // Testes para o método save (criação)

    @Test
    void testSave_Success_ForMatch() {
        // Mocks para save de review de partida
        when(reviewRepository.existsByUserIdAndMatchId(userId, matchId)).thenReturn(false);
        when(reviewRepository.existsByUserIdAndTournamentId(anyLong(), anyLong())).thenReturn(false); // Para garantir que não conflita com torneio
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(tournamentRepository.findById(anyLong())).thenReturn(Optional.empty()); // Garante que não tenta buscar torneio
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> {
            Review saved = invocation.getArgument(0);
            saved.setId(reviewMatchId);
            return saved;
        });

        ReviewResponseDTO responseDTO = reviewService.save(reviewRequestDTOForMatch);

        assertNotNull(responseDTO);
        assertEquals(reviewRequestDTOForMatch.getRating(), responseDTO.getRating());
        assertEquals(user.getName(), responseDTO.getUserName());
        assertTrue(responseDTO.getMatchInfo().contains("Partida ID: " + matchId));
        assertNull(responseDTO.getTournamentName()); // Deve ser nulo para review de partida
        verify(userRepository, times(1)).findById(userId);
        verify(matchRepository, times(1)).findById(matchId);
        verify(tournamentRepository, never()).findById(anyLong()); // Não deve chamar findById para torneio
        verify(reviewRepository, times(1)).existsByUserIdAndMatchId(userId, matchId);
        verify(reviewRepository, times(1)).existsByUserIdAndTournamentId(anyLong(), anyLong()); // Verifica que este também foi chamado
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void testSave_Success_ForTournament() {
        // Mocks para save de review de torneio
        when(reviewRepository.existsByUserIdAndMatchId(anyLong(), anyLong())).thenReturn(false); // Para garantir que não conflita com partida
        when(reviewRepository.existsByUserIdAndTournamentId(userId, tournamentId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(matchRepository.findById(anyLong())).thenReturn(Optional.empty()); // Garante que não tenta buscar partida
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> {
            Review saved = invocation.getArgument(0);
            saved.setId(reviewTournamentId);
            return saved;
        });

        ReviewResponseDTO responseDTO = reviewService.save(reviewRequestDTOForTournament);

        assertNotNull(responseDTO);
        assertEquals(reviewRequestDTOForTournament.getRating(), responseDTO.getRating());
        assertEquals(user.getName(), responseDTO.getUserName());
        assertEquals(tournament.getName(), responseDTO.getTournamentName()); // Deve ter o nome do torneio
        assertNull(responseDTO.getMatchInfo()); // Deve ser nulo para review de torneio
        verify(userRepository, times(1)).findById(userId);
        verify(matchRepository, never()).findById(anyLong()); // Não deve chamar findById para partida
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(reviewRepository, times(1)).existsByUserIdAndMatchId(anyLong(), anyLong()); // Verifica que este também foi chamado
        verify(reviewRepository, times(1)).existsByUserIdAndTournamentId(userId, tournamentId);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void testSave_UserNotFound() {
        // Mocks
        when(reviewRepository.existsByUserIdAndMatchId(anyLong(), anyLong())).thenReturn(false); // Para evitar NeverWantedButInvoked
        when(reviewRepository.existsByUserIdAndTournamentId(anyLong(), anyLong())).thenReturn(false); // Para evitar NeverWantedButInvoked
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> {
            reviewService.save(reviewRequestDTOForMatch);
        });
        verify(userRepository, times(1)).findById(userId);
        verify(matchRepository, never()).findById(anyLong());
        verify(tournamentRepository, never()).findById(anyLong());
        verify(reviewRepository, times(1)).existsByUserIdAndMatchId(anyLong(), anyLong());
        verify(reviewRepository, times(1)).existsByUserIdAndTournamentId(anyLong(), anyLong());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void testSave_MatchNotFound() {
        // Mocks
        when(reviewRepository.existsByUserIdAndMatchId(anyLong(), anyLong())).thenReturn(false);
        when(reviewRepository.existsByUserIdAndTournamentId(anyLong(), anyLong())).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(matchRepository.findById(matchId)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> {
            reviewService.save(reviewRequestDTOForMatch);
        });
        verify(userRepository, times(1)).findById(userId);
        verify(matchRepository, times(1)).findById(matchId);
        verify(tournamentRepository, never()).findById(anyLong());
        verify(reviewRepository, times(1)).existsByUserIdAndMatchId(anyLong(), anyLong());
        verify(reviewRepository, times(1)).existsByUserIdAndTournamentId(anyLong(), anyLong());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void testSave_TournamentNotFound() {
        // Mocks
        when(reviewRepository.existsByUserIdAndMatchId(anyLong(), anyLong())).thenReturn(false);
        when(reviewRepository.existsByUserIdAndTournamentId(anyLong(), anyLong())).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> {
            reviewService.save(reviewRequestDTOForTournament);
        });
        verify(userRepository, times(1)).findById(userId);
        verify(matchRepository, never()).findById(anyLong());
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(reviewRepository, times(1)).existsByUserIdAndMatchId(anyLong(), anyLong());
        verify(reviewRepository, times(1)).existsByUserIdAndTournamentId(anyLong(), anyLong());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void testSave_NeitherMatchIdNorTournamentIdProvided() {
        reviewRequestDTOForMatch.setMatchId(null);
        reviewRequestDTOForMatch.setTournamentId(null); // Nenhum ID fornecido

        assertThrows(BadRequestException.class, () -> {
            reviewService.save(reviewRequestDTOForMatch);
        });
        verify(reviewRepository, never()).existsByUserIdAndMatchId(anyLong(), anyLong()); // Não deve chegar aqui
        verify(reviewRepository, never()).existsByUserIdAndTournamentId(anyLong(), anyLong()); // Não deve chegar aqui
        verify(userRepository, never()).findById(anyLong());
        verify(matchRepository, never()).findById(anyLong());
        verify(tournamentRepository, never()).findById(anyLong());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void testSave_BothMatchIdAndTournamentIdProvided() {
        reviewRequestDTOForMatch.setTournamentId(tournamentId); // Ambos os IDs fornecidos

        assertThrows(BadRequestException.class, () -> {
            reviewService.save(reviewRequestDTOForMatch);
        });
        verify(reviewRepository, never()).existsByUserIdAndMatchId(anyLong(), anyLong());
        verify(reviewRepository, never()).existsByUserIdAndTournamentId(anyLong(), anyLong());
        verify(userRepository, never()).findById(anyLong());
        verify(matchRepository, never()).findById(anyLong());
        verify(tournamentRepository, never()).findById(anyLong());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void testSave_UserAlreadyReviewedMatch() {
        // Mocks
        when(reviewRepository.existsByUserIdAndMatchId(userId, matchId)).thenReturn(true);
        // Não precisa mockar existsByUserIdAndTournamentId se já falha aqui

        assertThrows(BadRequestException.class, () -> {
            reviewService.save(reviewRequestDTOForMatch);
        });
        verify(reviewRepository, times(1)).existsByUserIdAndMatchId(userId, matchId);
        verify(reviewRepository, never()).existsByUserIdAndTournamentId(anyLong(), anyLong()); // Não deve chamar este
        verify(userRepository, never()).findById(userId);
        verify(matchRepository, never()).findById(matchId);
        verify(tournamentRepository, never()).findById(anyLong());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void testSave_UserAlreadyReviewedTournament() {
        // Mocks
        when(reviewRepository.existsByUserIdAndMatchId(anyLong(), anyLong())).thenReturn(false); // Passa na primeira checagem
        when(reviewRepository.existsByUserIdAndTournamentId(userId, tournamentId)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            reviewService.save(reviewRequestDTOForTournament);
        });
        verify(reviewRepository, times(1)).existsByUserIdAndMatchId(anyLong(), anyLong());
        verify(reviewRepository, times(1)).existsByUserIdAndTournamentId(userId, tournamentId);
        verify(userRepository, never()).findById(userId);
        verify(matchRepository, never()).findById(anyLong());
        verify(tournamentRepository, never()).findById(anyLong());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void testSave_InvalidRatingTooLow() {
        reviewRequestDTOForMatch.setRating(0);
        
        assertThrows(BadRequestException.class, () -> {
            reviewService.save(reviewRequestDTOForMatch);
        });
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void testSave_InvalidRatingTooHigh() {
        reviewRequestDTOForMatch.setRating(6);
        
        assertThrows(BadRequestException.class, () -> {
            reviewService.save(reviewRequestDTOForMatch);
        });
        verify(reviewRepository, never()).save(any(Review.class));
    }

    // Testes para o método findById

    @Test
    void testFindById_Success_ForMatchReview() {
        when(reviewRepository.findById(reviewMatchId)).thenReturn(Optional.of(reviewMatch));

        ReviewResponseDTO responseDTO = reviewService.findById(reviewMatchId);

        assertNotNull(responseDTO);
        assertEquals(reviewMatch.getRating(), responseDTO.getRating());
        assertEquals(user.getName(), responseDTO.getUserName());
        assertTrue(responseDTO.getMatchInfo().contains("Partida ID: " + matchId));
        assertNull(responseDTO.getTournamentName()); // Nulo para review de partida
        assertEquals(matchId, responseDTO.getMatchId()); // Verifica que o ID da partida está no DTO
        assertNull(responseDTO.getTournamentId()); // Verifica que o ID do torneio é nulo
        verify(reviewRepository, times(1)).findById(reviewMatchId);
    }

    @Test
    void testFindById_Success_ForTournamentReview() {
        when(reviewRepository.findById(reviewTournamentId)).thenReturn(Optional.of(reviewTournament));

        ReviewResponseDTO responseDTO = reviewService.findById(reviewTournamentId);

        assertNotNull(responseDTO);
        assertEquals(reviewTournament.getRating(), responseDTO.getRating());
        assertEquals(user.getName(), responseDTO.getUserName());
        assertEquals(tournament.getName(), responseDTO.getTournamentName()); // Verifica nome do torneio
        assertNull(responseDTO.getMatchInfo()); // Nulo para review de torneio
        assertNull(responseDTO.getMatchId()); // Verifica que o ID da partida é nulo
        assertEquals(tournamentId, responseDTO.getTournamentId()); // Verifica que o ID do torneio está no DTO
        verify(reviewRepository, times(1)).findById(reviewTournamentId);
    }

    @Test
    void testFindById_NotFound() {
        when(reviewRepository.findById(reviewMatchId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            reviewService.findById(reviewMatchId);
        });
        verify(reviewRepository, times(1)).findById(reviewMatchId);
    }

    // Testes para o método findAll

    @Test
    void testFindAll_Success() {
        List<Review> reviews = List.of(reviewMatch, reviewTournament);
        when(reviewRepository.findAll()).thenReturn(reviews);

        List<ReviewResponseDTO> responseDTOs = reviewService.findAll();

        assertNotNull(responseDTOs);
        assertFalse(responseDTOs.isEmpty());
        assertEquals(2, responseDTOs.size());

        // Verifica a review da partida
        ReviewResponseDTO matchReviewDTO = responseDTOs.stream()
            .filter(r -> r.getId().equals(reviewMatchId))
            .findFirst().orElseThrow();
        assertEquals(reviewMatch.getComment(), matchReviewDTO.getComment());
        assertTrue(matchReviewDTO.getMatchInfo().contains("Partida ID: " + matchId));
        assertNull(matchReviewDTO.getTournamentName());

        // Verifica a review do torneio
        ReviewResponseDTO tournamentReviewDTO = responseDTOs.stream()
            .filter(r -> r.getId().equals(reviewTournamentId))
            .findFirst().orElseThrow();
        assertEquals(reviewTournament.getComment(), tournamentReviewDTO.getComment());
        assertNull(tournamentReviewDTO.getMatchInfo());
        assertEquals(tournament.getName(), tournamentReviewDTO.getTournamentName());

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

    // Testes para o método deleteById

    @Test
    void testDeleteById_Success() {
        when(reviewRepository.existsById(reviewMatchId)).thenReturn(true);
        doNothing().when(reviewRepository).deleteById(reviewMatchId);

        assertDoesNotThrow(() -> reviewService.deleteById(reviewMatchId));

        verify(reviewRepository, times(1)).existsById(reviewMatchId);
        verify(reviewRepository, times(1)).deleteById(reviewMatchId);
    }

    @Test
    void testDeleteById_NotFound() {
        when(reviewRepository.existsById(reviewMatchId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            reviewService.deleteById(reviewMatchId);
        });
        verify(reviewRepository, times(1)).existsById(reviewMatchId);
        verify(reviewRepository, never()).deleteById(anyLong());
    }
}
