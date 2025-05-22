package com.ajs.arenasync.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ajs.arenasync.Entities.Match;
import com.ajs.arenasync.Entities.Result;
import com.ajs.arenasync.Exceptions.BadRequestException;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.ajs.arenasync.Repositories.MatchRepository;
import com.ajs.arenasync.Repositories.ResultRepository;

@Service
public class ResultService {

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private MatchRepository matchRepository;

    public Result save(Result result) {
        validateResult(result);
        return resultRepository.save(result);
    }

    public Result findById(Long id) {
        return resultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resultado", id));
    }

    public void deleteById(Long id) {
        if (!resultRepository.existsById(id)) {
            throw new ResourceNotFoundException("Resultado", id);
        }
        resultRepository.deleteById(id);
    }

    public List<Result> findAll() {
        return resultRepository.findAll();
    }

    private void validateResult(Result result) {
        if (result.getDetails() == null || result.getDetails().trim().isEmpty()) {
            throw new BadRequestException("O campo de detalhes do resultado é obrigatório.");
        }

        if (result.getMatch() == null || result.getMatch().getId() == null) {
            throw new BadRequestException("A partida associada ao resultado deve ser informada.");
        }

        Match match = matchRepository.findById(result.getMatch().getId())
                .orElseThrow(() -> new BadRequestException("Partida associada ao resultado não encontrada."));

        // Validação opcional: impedir duplicidade de resultado por partida
        if (resultRepository.existsByMatch(match) && (result.getId() == null)) {
            throw new BadRequestException("Essa partida já possui um resultado registrado.");
        }
    }
}
