package com.ajs.arenasync.Entities;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class Player extends User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String position;

    @ManyToOne
    @JoinColumn(name = "team_id")
    @JsonIgnore
    private Team team;

    public Player(){};

    public Player(String position, Team team) {
        this.position = position;
        this.team = team;
    }


    public Player(Long id, @NotNull(message = "O nome é obrigatório") String name,
            @NotNull(message = "A idade é obrigatório") Integer age,
            @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres") String password,
            @Email(message = "Email inválido") String email, List<Tournament> tournaments, List<Review> reviews,
            String position, Team team) {
        super(id, name, age, password, email, tournaments, reviews);
        this.position = position;
        this.team = team;
    }



    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((position == null) ? 0 : position.hashCode());
        result = prime * result + ((team == null) ? 0 : team.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        Player other = (Player) obj;
        if (position == null) {
            if (other.position != null)
                return false;
        } else if (!position.equals(other.position))
            return false;
        if (team == null) {
            if (other.team != null)
                return false;
        } else if (!team.equals(other.team))
            return false;
        return true;
    }



}
