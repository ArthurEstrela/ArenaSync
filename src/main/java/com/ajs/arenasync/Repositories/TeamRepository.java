package com.ajs.arenasync.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ajs.arenasync.Entities.Team;
import java.util.List;


@Repository
public interface TeamRepository extends JpaRepository<Team, Long>{

Team findByName(String name);

boolean existsByName(String name);
}
