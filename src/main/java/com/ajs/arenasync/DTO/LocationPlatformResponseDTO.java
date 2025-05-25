package com.ajs.arenasync.DTO;

import com.ajs.arenasync.Entities.Enums.TournamentType;

public class LocationPlatformResponseDTO {

    private Long id;
    private String name;
    private TournamentType type;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public TournamentType getType() {
        return type;
    }
    public void setType(TournamentType type) {
        this.type = type;
    }

  
}