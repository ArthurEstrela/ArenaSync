package com.ajs.arenasync.Repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ajs.arenasync.Entities.Match;
import com.ajs.arenasync.Entities.Result;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {

    Result findByMatchId(Long matchId);

    boolean existsByMatch(Match match);

    boolean existsByMatchId(Long matchId);

}

