package com.ajs.arenasync.Repositories;
//Concluída
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ajs.arenasync.Entities.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByTournamentId(Long tournamentId);
}


