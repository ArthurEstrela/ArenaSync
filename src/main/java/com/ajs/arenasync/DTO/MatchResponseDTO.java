package com.ajs.arenasync.DTO;

public class MatchResponseDTO {

    private Long id;
    private String teamAName;
    private String teamBName;
    private String tournamentName;
    private String locationPlatformName;
    private String scheduledDateTime;
    
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
    public String getScheduledDateTime() {
        return scheduledDateTime;
    }
    public void setScheduledDateTime(String scheduledDateTime) {
        this.scheduledDateTime = scheduledDateTime;
    }

    
}