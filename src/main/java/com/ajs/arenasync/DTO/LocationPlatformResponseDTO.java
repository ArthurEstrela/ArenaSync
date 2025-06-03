package com.ajs.arenasync.DTO;

import org.springframework.hateoas.RepresentationModel;

import com.ajs.arenasync.Entities.Enums.TournamentType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class LocationPlatformResponseDTO extends RepresentationModel<LocationPlatformResponseDTO>{

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