package com.ajs.arenasync.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ajs.arenasync.Entities.Prize;
import com.ajs.arenasync.Entities.Tournament;
import com.ajs.arenasync.Exceptions.BadRequestException;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.ajs.arenasync.Repositories.PrizeRepository;
import com.ajs.arenasync.Repositories.TournamentRepository;

@Service
public class PrizeService {

    @Autowired
    private PrizeRepository prizeRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    public Prize save(Prize prize) {
        validatePrize(prize);
        return prizeRepository.save(prize);
    }

    public Prize findById(Long id) {
        return prizeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prêmio", id));
    }

    public void deleteById(Long id) {
        if (!prizeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Prêmio", id);
        }
        prizeRepository.deleteById(id);
    }

    public List<Prize> findAll() {
        return prizeRepository.findAll();
    }

    private void validatePrize(Prize prize) {
        if (prize.getDescription() == null || prize.getDescription().trim().isEmpty()) {
            throw new BadRequestException("A descrição do prêmio é obrigatória.");
        }

        if (prize.getValue() == null || prize.getValue() <= 0) {
            throw new BadRequestException("O valor do prêmio deve ser maior que zero.");
        }

        if (prize.getTournament() == null || prize.getTournament().getId() == null) {
            throw new BadRequestException("O torneio associado ao prêmio deve ser informado.");
        }

        tournamentRepository.findById(prize.getTournament().getId())
                .orElseThrow(() -> new BadRequestException("Torneio associado ao prêmio não encontrado."));
    }
}
