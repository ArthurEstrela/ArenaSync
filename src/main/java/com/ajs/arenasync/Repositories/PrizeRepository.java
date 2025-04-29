package com.ajs.arenasync.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ajs.arenasync.Entities.Prize;

@Repository
public interface PrizeRepository extends JpaRepository<Prize, Long> {

    List<Prize> findByTournamentId(Long tournamentId);
}

