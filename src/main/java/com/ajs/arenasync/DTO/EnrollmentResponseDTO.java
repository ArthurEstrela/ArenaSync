package com.ajs.arenasync.DTO;

public class EnrollmentResponseDTO {

    private Long id;
    private String teamName;
    private String tournamentName;
    private String status;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTeamName() {
        return teamName;
    }
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
    public String getTournamentName() {
        return tournamentName;
    }
    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    
}