package com.example.annaapp;

public class ShowModel {
    private String showName;
    private String crewName;
    private String date1;
    private String date2;
    private String imageUri;

    private String access_code;

    public ShowModel(String access_code, String showName, String crewName, String date1, String date2, String imageUri) {
        this.showName = showName;
        this.crewName = crewName;
        this.date1 = date1;
        this.date2 = date2;
        this.imageUri = imageUri;
        this.access_code = access_code;
    }

    public ShowModel(String access_code, String showName, String crewName, String date1, String date2) {
        this.access_code = access_code;
        this.showName = showName;
        this.crewName = crewName;
        this.date1 = date1;
        this.date2 = date2;
    }
    public String getAccess_code() {
        return access_code;
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

    public String getImageUri() {
        return imageUri;
    }

    public void setAccess_code(String access_code) {
        this.access_code = access_code;
    }
}
