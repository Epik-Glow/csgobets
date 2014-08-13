package com.davenwu.csgobets;

import android.graphics.Bitmap;

public class Match {
    private String time, additionalInfo, event, teamOneName, teamTwoName, teamOnePercentage, teamTwoPercentage, matchUrl;
    private Bitmap eventBackground, teamOneImage, teamTwoImage;
    private boolean matchOver = false, teamOneWin = false, teamTwoWin = false;

    public boolean isMatchOver() {
        return matchOver;
    }

    public void setMatchOver(boolean matchOver) {
        this.matchOver = matchOver;
    }

    public boolean isTeamOneWin() {
        return teamOneWin;
    }

    public void setTeamOneWin(boolean teamOneWin) {
        this.teamOneWin = teamOneWin;
    }

    public boolean isTeamTwoWin() {
        return teamTwoWin;
    }

    public void setTeamTwoWin(boolean teamTwoWin) {
        this.teamTwoWin = teamTwoWin;
    }



    public String getMatchUrl() {
        return matchUrl;
    }

    public void setMatchUrl(String matchUrl) {
        this.matchUrl = matchUrl;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getTeamOneName() {
        return teamOneName;
    }

    public void setTeamOneName(String teamOneName) {
        this.teamOneName = teamOneName;
    }

    public String getTeamTwoName() {
        return teamTwoName;
    }

    public void setTeamTwoName(String teamTwoName) {
        this.teamTwoName = teamTwoName;
    }

    public String getTeamOnePercentage() {
        return teamOnePercentage;
    }

    public void setTeamOnePercentage(String teamOnePercentage) {
        this.teamOnePercentage = teamOnePercentage;
    }

    public String getTeamTwoPercentage() {
        return teamTwoPercentage;
    }

    public void setTeamTwoPercentage(String teamTwoPercentage) {
        this.teamTwoPercentage = teamTwoPercentage;
    }

    public Bitmap getEventBackground() {
        return eventBackground;
    }

    public void setEventBackground(Bitmap eventBackground) {
        this.eventBackground = eventBackground;
    }

    public Bitmap getTeamOneImage() {
        return teamOneImage;
    }

    public void setTeamOneImage(Bitmap teamOneImage) {
        this.teamOneImage = teamOneImage;
    }

    public Bitmap getTeamTwoImage() {
        return teamTwoImage;
    }

    public void setTeamTwoImage(Bitmap teamTwoImage) {
        this.teamTwoImage = teamTwoImage;
    }
}
