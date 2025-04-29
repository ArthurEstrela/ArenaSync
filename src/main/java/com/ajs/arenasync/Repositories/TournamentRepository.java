package com.ajs.arenasync.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ajs.arenasync.Entities.Tournament;
import com.ajs.arenasync.Entities.Enums.TournamentType;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long>{

List<Tournament> findByType(TournamentType type);

List<Tournament> findByOrganizerId(Long organizerId);
}
