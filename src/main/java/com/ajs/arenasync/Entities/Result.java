package com.ajs.arenasync.Entities;

import java.io.Serializable;

import jakarta.persistence.*;

@Entity
public class Result implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "score_team_a") 
    private Integer scoreTeamA;

    @Column(name = "score_team_b") 
    private Integer scoreTeamB;

    @OneToOne
    @JoinColumn(name = "match_id", unique = true)
    private Match match;

    public Result() {
    }

    public Result(Long id, Integer scoreTeamA, Integer scoreTeamB, Match match) {
        this.id = id;
        this.scoreTeamA = scoreTeamA;
        this.scoreTeamB = scoreTeamB;
        this.match = match;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getScoreTeamA() {
        return scoreTeamA;
    }

    public void setScoreTeamA(Integer scoreTeamA) {
        this.scoreTeamA = scoreTeamA;
    }

    public Integer getScoreTeamB() {
        return scoreTeamB;
    }

    public void setScoreTeamB(Integer scoreTeamB) {
        this.scoreTeamB = scoreTeamB;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    @Override
    public int hashCode() {
        return (id != null) ? id.hashCode() : 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Result))
            return false;
        Result other = (Result) obj;
        return id != null && id.equals(other.getId());
    }
}