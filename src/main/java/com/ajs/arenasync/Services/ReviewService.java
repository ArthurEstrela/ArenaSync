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
import com.ajs.arenasync.Entities.Tournament; // Importe Tournament
import com.ajs.arenasync.Exceptions.BadRequestException;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.ajs.arenasync.Repositories.MatchRepository;
import com.ajs.arenasync.Repositories.ReviewRepository;
import com.ajs.arenasync.Repositories.UserRepository;
import com.ajs.arenasync.Repositories.TournamentRepository; // Injete TournamentRepository


@Service
@CacheConfig(cacheNames = "reviews") // define o nome do cache
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private TournamentRepository tournamentRepository; // Injete TournamentRepository

    @CacheEvict(value = "reviews", allEntries = true)
    public ReviewResponseDTO save(ReviewRequestDTO dto) {
        validateReview(dto); // Validações de negócio
        Review review = toEntity(dto); // Converte DTO para entidade, buscando as entidades relacionadas
        Review saved = reviewRepository.save(review); // Salva no banco de dados

        return toResponseDTO(saved); // Converte a entidade salva de volta para DTO de resposta
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

    // Método de validação centralizado para a lógica de negócio
    private void validateReview(ReviewRequestDTO dto) {
        // Regra 1: Deve informar matchId OU tournamentId, mas não ambos, e não nenhum
        if (dto.getMatchId() == null && dto.getTournamentId() == null) {
            throw new BadRequestException("É necessário informar o ID da partida ou do torneio para avaliar.");
        }
        if (dto.getMatchId() != null && dto.getTournamentId() != null) {
            throw new BadRequestException("Uma avaliação não pode estar associada a uma partida e a um torneio simultaneamente. Escolha um.");
        }

        // Regra 2: Validação da nota
        if (dto.getRating() == null || dto.getRating() < 1 || dto.getRating() > 5) {
            throw new BadRequestException("A nota da avaliação deve estar entre 1 e 5.");
        }

        // Regra 3: Validação de unicidade (um usuário só pode avaliar uma partida/torneio uma vez)
        if (dto.getMatchId() != null) {
            if (reviewRepository.existsByUserIdAndMatchId(dto.getUserId(), dto.getMatchId())) {
                throw new BadRequestException("Você já avaliou esta partida.");
            }
        } else if (dto.getTournamentId() != null) {
            if (reviewRepository.existsByUserIdAndTournamentId(dto.getUserId(), dto.getTournamentId())) {
                throw new BadRequestException("Você já avaliou este torneio.");
            }
        }
    }

    // Converte DTO de Requisição para Entidade
    private Review toEntity(ReviewRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new BadRequestException("Usuário não encontrado."));

        Match match = null;
        if (dto.getMatchId() != null) {
            match = matchRepository.findById(dto.getMatchId())
                    .orElseThrow(() -> new BadRequestException("Partida não encontrada."));
        }

        Tournament tournament = null;
        if (dto.getTournamentId() != null) {
            tournament = tournamentRepository.findById(dto.getTournamentId())
                    .orElseThrow(() -> new BadRequestException("Torneio não encontrado."));
        }

        Review review = new Review();
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setUser(user);
        review.setMatch(match);
        review.setTournament(tournament); // Popula o campo tournament na entidade Review

        return review;
    }

    // Converte Entidade para DTO de Resposta
    private ReviewResponseDTO toResponseDTO(Review review) {
        ReviewResponseDTO dto = new ReviewResponseDTO();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        
        // Popula os IDs no DTO de resposta
        dto.setUserId(review.getUser() != null ? review.getUser().getId() : null);
        dto.setMatchId(review.getMatch() != null ? review.getMatch().getId() : null);
        dto.setTournamentId(review.getTournament() != null ? review.getTournament().getId() : null);

        dto.setUserName(review.getUser() != null ? review.getUser().getName() : null);
        // Ajusta matchInfo e tournamentName para refletir qual foi avaliado
        if (review.getMatch() != null) {
            dto.setMatchInfo("Partida ID: " + review.getMatch().getId());
        } else {
            dto.setMatchInfo(null); // Garante que não há info de partida se não for uma review de partida
        }
        
        if (review.getTournament() != null) {
            dto.setTournamentName(review.getTournament().getName());
        } else {
            dto.setTournamentName(null); // Garante que não há nome de torneio se não for uma review de torneio
        }
        
        return dto;
    }
}
