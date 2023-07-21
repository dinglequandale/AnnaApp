package com.example.annaapp;

public class ShowModel {
    private String showName;
    private String crewName;
    private String date1;
    private String date2;
    private String imageURL;

    private String uid;

    public ShowModel(String uid, String showName, String crewName, String date1, String date2, String imageURL) {
        this.showName = showName;
        this.crewName = crewName;
        this.date1 = date1;
        this.date2 = date2;
        this.imageURL = imageURL;
        this.uid = uid;
    }

    public ShowModel(String uid, String showName, String crewName, String date1, String date2) {
        this.uid = uid;
        this.showName = showName;
        this.crewName = crewName;
        this.date1 = date1;
        this.date2 = date2;
    }
    public String getUid() {
        return uid;
    }

    public CharSequence getShowName() {
        return showName;
    }

    public String getCrewName() {
        return crewName;
    }

    public String getDate1() {
        return date1;
    }

    public String getDate2() {
        return date2;
    }

    public String getImageURL() {
        return imageURL;
    }
}
