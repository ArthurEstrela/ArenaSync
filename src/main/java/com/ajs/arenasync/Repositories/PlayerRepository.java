package com.ajs.arenasync.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ajs.arenasync.Entities.Player;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    List<Player> findByTeamId(Long teamId);

    boolean existsByEmail(String email);

    List<Player> findByTeamIsNull();
}
