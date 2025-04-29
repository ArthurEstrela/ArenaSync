package com.ajs.arenasync.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ajs.arenasync.Entities.Result;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {

    Optional<Result> findByMatchId(Long matchId);
}

