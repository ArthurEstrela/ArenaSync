package com.ajs.arenasync.Services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ajs.arenasync.DTO.ResultRequestDTO;
import com.ajs.arenasync.DTO.ResultResponseDTO;
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

    public ResultResponseDTO save(ResultRequestDTO dto) {
        validateResult(dto);

        Match match = matchRepository.findById(dto.getMatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Partida", dto.getMatchId()));

        Result result = new Result();
        result.setMatch(match);
        result.setScoreTeamA(dto.getScoreTeamA());
        result.setScoreTeamB(dto.getScoreTeamB());

        Result saved = resultRepository.save(result);
        return toResponseDTO(saved);
    }

    public ResultResponseDTO findById(Long id) {
        Result result = resultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resultado", id));
        return toResponseDTO(result);
    }

    public List<ResultResponseDTO> findAll() {
        return resultRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        if (!resultRepository.existsById(id)) {
            throw new ResourceNotFoundException("Resultado", id);
        }
        resultRepository.deleteById(id);
    }

    private ResultResponseDTO toResponseDTO(Result result) {
        ResultResponseDTO dto = new ResultResponseDTO();
        dto.setId(result.getId());
        dto.setMatchId(result.getMatch().getId());
        dto.setScoreTeamA(result.getScoreTeamA());
        dto.setScoreTeamB(result.getScoreTeamB());
        return dto;
    }

    private void validateResult(ResultRequestDTO dto) {
        if (dto.getMatchId() == null) {
            throw new BadRequestException("ID da partida é obrigatório.");
        }

        if (dto.getScoreTeamA() == null || dto.getScoreTeamA() < 0) {
            throw new BadRequestException("Pontuação da equipe A inválida.");
        }

        if (dto.getScoreTeamB() == null || dto.getScoreTeamB() < 0) {
            throw new BadRequestException("Pontuação da equipe B inválida.");
        }

        if (resultRepository.existsByMatchId(dto.getMatchId())) {
            throw new BadRequestException("Já existe um resultado registrado para esta partida.");
        }
    }
}
