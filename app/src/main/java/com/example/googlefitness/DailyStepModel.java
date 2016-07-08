package com.example.googlefitness;

/**
 * Created by oscarn on 2016-07-08.
 */
public class DailyStepModel {

    private String userID;
    private String date;
    private String steps;

    public DailyStepModel(String userID, String date, String steps) {
        this.userID = userID;
        this.date = date;
        this.steps = steps;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }
}
