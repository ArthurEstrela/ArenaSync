package com.ajs.arenasync.Services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import com.ajs.arenasync.DTO.PrizeRequestDTO;
import com.ajs.arenasync.DTO.PrizeResponseDTO;
import com.ajs.arenasync.Entities.Prize;
import com.ajs.arenasync.Entities.Tournament;
import com.ajs.arenasync.Exceptions.BadRequestException;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.ajs.arenasync.Repositories.PrizeRepository;
import com.ajs.arenasync.Repositories.TournamentRepository;

@Service
@CacheConfig(cacheNames = "prizes") // define o nome do cache
public class PrizeService {

    @Autowired
    private PrizeRepository prizeRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @CacheEvict(value = "prizes", allEntries = true)
    public PrizeResponseDTO save(PrizeRequestDTO dto) {
        validatePrize(dto);

        Tournament tournament = tournamentRepository.findById(dto.getTournamentId())
                .orElseThrow(() -> new BadRequestException("Torneio associado ao prêmio não encontrado."));

        Prize prize = new Prize();
        prize.setDescription(dto.getDescription());
        prize.setValue(dto.getValue());
        prize.setTournament(tournament);

        return toResponseDTO(prizeRepository.save(prize));
    }

    @Cacheable(key = "#id")
    public PrizeResponseDTO findById(Long id) {
        Prize prize = prizeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prêmio", id));
        return toResponseDTO(prize);
    }

    @Cacheable
    public List<PrizeResponseDTO> findAll() {
        return prizeRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Caching(evict = {
        @CacheEvict(key = "#id"),
        @CacheEvict(allEntries = true)
    })
    public void deleteById(Long id) {
        if (!prizeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Prêmio", id);
        }
        prizeRepository.deleteById(id);
    }

    private PrizeResponseDTO toResponseDTO(Prize prize) {
        PrizeResponseDTO dto = new PrizeResponseDTO();
        dto.setId(prize.getId());
        dto.setDescription(prize.getDescription());
        dto.setValue(prize.getValue());
        // Popula o ID do torneio no DTO de resposta
        dto.setTournamentId(prize.getTournament().getId());
        dto.setTournamentName(prize.getTournament().getName());
        return dto;
    }

    private void validatePrize(PrizeRequestDTO dto) {
        if (dto.getDescription() == null || dto.getDescription().trim().isEmpty()) {
            throw new BadRequestException("A descrição do prêmio é obrigatória.");
        }

        if (dto.getValue() == null || dto.getValue() <= 0) {
            throw new BadRequestException("O valor do prêmio deve ser maior que zero.");
        }

        if (dto.getTournamentId() == null) {
            throw new BadRequestException("O torneio associado ao prêmio deve ser informado.");
        }
    }
}
