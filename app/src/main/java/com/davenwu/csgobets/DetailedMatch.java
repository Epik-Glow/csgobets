package com.davenwu.csgobets;

import android.graphics.Bitmap;

public class DetailedMatch {
    private String approximateTime, bestOf, exactTime, teamOneName, teamOnePercentage,
            teamTwoName, teamTwoPercentage, teamOnePotentialReward, teamTwoPotentialReward,
            streamUrl;
    private Bitmap teamOneImage, teamTwoImage;
    private boolean matchOver;

    public String getApproximateTime() {
        return approximateTime;
    }

    public void setApproximateTime(String approximateTime) {
        this.approximateTime = approximateTime;
    }

    public String getBestOf() {
        return bestOf;
    }

    public void setBestOf(String bestOf) {
        this.bestOf = bestOf;
    }

    public String getExactTime() {
        return exactTime;
    }

    public void setExactTime(String exactTime) {
        this.exactTime = exactTime;
    }

    public String getTeamOneName() {
        return teamOneName;
    }

    public void setTeamOneName(String teamOneName) {
        this.teamOneName = teamOneName;
    }

    public String getTeamOnePercentage() {
        return teamOnePercentage;
    }

    public void setTeamOnePercentage(String teamOnePercentage) {
        this.teamOnePercentage = teamOnePercentage;
    }

    public String getTeamTwoName() {
        return teamTwoName;
    }

    public void setTeamTwoName(String teamTwoName) {
        this.teamTwoName = teamTwoName;
    }

    public String getTeamTwoPercentage() {
        return teamTwoPercentage;
    }

    public void setTeamTwoPercentage(String teamTwoPercentage) {
        this.teamTwoPercentage = teamTwoPercentage;
    }

    public String getTeamOnePotentialReward() {
        return teamOnePotentialReward;
    }

    public void setTeamOnePotentialReward(String teamOnePotentialReward) {
        this.teamOnePotentialReward = teamOnePotentialReward;
    }

    public String getTeamTwoPotentialReward() {
        return teamTwoPotentialReward;
    }

    public void setTeamTwoPotentialReward(String teamTwoPotentialReward) {
        this.teamTwoPotentialReward = teamTwoPotentialReward;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
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

    public boolean isMatchOver() {
        return matchOver;
    }

    public void setMatchOver(boolean matchOver) {
        this.matchOver = matchOver;
    }

}
