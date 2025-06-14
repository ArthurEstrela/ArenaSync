package com.ajs.arenasync.Repositories;

//Concluída
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ajs.arenasync.Entities.LocationPlatform;

@Repository
public interface LocationPlatformRepository extends JpaRepository<LocationPlatform, Long> {

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    boolean existsByNameIgnoreCase(String name);

}
