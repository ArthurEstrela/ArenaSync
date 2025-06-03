package com.ajs.arenasync.DTO;

import java.time.LocalDateTime;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class MatchResponseDTO extends RepresentationModel<MatchResponseDTO>{

    private Long id;
    private String teamAName;
    private String teamBName;
    private String tournamentName;
    private String locationPlatformName;
    private LocalDateTime scheduledDateTime;
    private Integer scoreTeamA;
    private Integer scoreTeamB;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTeamAName() {
        return teamAName;
    }
    public void setTeamAName(String teamAName) {
        this.teamAName = teamAName;
    }
    public String getTeamBName() {
        return teamBName;
    }
    public void setTeamBName(String teamBName) {
        this.teamBName = teamBName;
    }
    public String getTournamentName() {
        return tournamentName;
    }
    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }
    public String getLocationPlatformName() {
        return locationPlatformName;
    }
    public void setLocationPlatformName(String locationPlatformName) {
        this.locationPlatformName = locationPlatformName;
    }
    public LocalDateTime getScheduledDateTime() {
        return scheduledDateTime;
    }
    public void setScheduledDateTime(LocalDateTime scheduledDateTime) {
        this.scheduledDateTime = scheduledDateTime;
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

    
}
