package com.ajs.arenasync.Repositories;
//Concluída
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ajs.arenasync.Entities.Enrollment;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByTournamentId(Long tournamentId);

    List<Enrollment> findByTeamId(Long teamId);

    Enrollment findByTournamentIdAndTeamId(Long tournamentId, Long teamId);
}
