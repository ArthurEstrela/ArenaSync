package com.ajs.arenasync.Entities;

import java.io.Serializable;
import java.util.List;

import com.ajs.arenasync.Entities.Enums.TournamentType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;

@Entity
public class LocationPlatform implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "O nome é obrigatório")  // Não pode ser nulo ou vazio
    private String name;
    @Enumerated(EnumType.STRING) 
    private TournamentType type;
    private String description;
    @OneToMany(mappedBy = "locationPlatform")

    private List<Match> matches;

    public LocationPlatform() {
    }

    public LocationPlatform(Long id, String name, TournamentType type, String description, List<Match> matches) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.matches = matches;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Match> getMatches() {
        return matches;
    }

    

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LocationPlatform other = (LocationPlatform) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }

}
