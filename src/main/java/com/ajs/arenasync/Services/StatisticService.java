package com.ajs.arenasync.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ajs.arenasync.Entities.Statistic;
import com.ajs.arenasync.Exceptions.BusinessException;
import com.ajs.arenasync.Exceptions.ResourceNotFoundException;
import com.ajs.arenasync.Repositories.StatisticRepository;

@Service
public class StatisticService {

    @Autowired
    private StatisticRepository statisticRepository;

    // Salvar com validações
    public Statistic save(Statistic statistic) {
        if (statistic.getPlayer() == null) {
            throw new BusinessException("Usuário da estatística é obrigatório.");
        }
        // Regras adicionais podem ser aplicadas aqui conforme sua lógica
        return statisticRepository.save(statistic);
    }

    // Buscar por ID com tratamento de erro
    public Statistic findById(Long id) {
        return statisticRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estatística", id));
    }

    // Atualizar com validação
    public Statistic update(Long id, Statistic statistic) {
        Statistic existing = findById(id); // dispara exceção se não achar

        // Atualizar os campos necessários
        existing.setGamesPlayed(statistic.getGamesPlayed());
        existing.setWins(statistic.getWins());
        existing.setScore(statistic.getScore());
        existing.setPlayer(statistic.getPlayer()); // ou evite atualizar user, dependendo da regra

        return statisticRepository.save(existing);
    }

    // Deletar com verificação
    public void deleteById(Long id) {
        Statistic existing = findById(id);
        statisticRepository.deleteById(existing.getId());
    }
}