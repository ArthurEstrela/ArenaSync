package com.ajs.arenasync.Repositories;
//Concluída

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ajs.arenasync.Entities.Team;


@Repository
public interface TeamRepository extends JpaRepository<Team, Long>{

Team findByName(String name);

boolean existsByName(String name);
}
