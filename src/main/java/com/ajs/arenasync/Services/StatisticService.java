package com.ajs.arenasync.Services;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ajs.arenasync.Entities.Statistic;
import com.ajs.arenasync.Repositories.StatisticRepository;

@Service
public class StatisticService {

    @Autowired
    private StatisticRepository statisticRepository;

    public Statistic save(Statistic statistic) {
        return statisticRepository.save(statistic);
    }

    public Optional<Statistic> findById(Long id) {
        return statisticRepository.findById(id);
    }

    public void deleteById(Long id) {
        statisticRepository.deleteById(id);
    }
}