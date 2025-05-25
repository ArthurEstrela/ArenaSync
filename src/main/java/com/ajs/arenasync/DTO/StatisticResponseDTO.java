package com.ajs.arenasync.DTO;

public class StatisticResponseDTO {

    private Long id;
    private String playerName;
    private String matchInfo;
    private int points;
    private int assists;
    private int rebounds;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getPlayerName() {
        return playerName;
    }
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    public String getMatchInfo() {
        return matchInfo;
    }
    public void setMatchInfo(String matchInfo) {
        this.matchInfo = matchInfo;
    }
    public int getPoints() {
        return points;
    }
    public void setPoints(int points) {
        this.points = points;
    }
    public int getAssists() {
        return assists;
    }
    public void setAssists(int assists) {
        this.assists = assists;
    }
    public int getRebounds() {
        return rebounds;
    }
    public void setRebounds(int rebounds) {
        this.rebounds = rebounds;
    }

    
    
}