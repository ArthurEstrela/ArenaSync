package com.ajs.arenasync.Repositories;
//Concluída

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ajs.arenasync.Entities.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {


    boolean existsByUserIdAndMatchId(Long userId, Long matchId);
}


