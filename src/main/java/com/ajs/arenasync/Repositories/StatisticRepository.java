package com.ajs.arenasync.Repositories;
//Concluída
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ajs.arenasync.Entities.Statistic;

@Repository
public interface StatisticRepository extends JpaRepository<Statistic, Long> {

    List<Statistic> findByPlayerId(Long playerId);

    List<Statistic> findByMatchId(Long matchId);
}
