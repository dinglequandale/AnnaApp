package com.example.annaapp;

import java.util.Date;

public class DateModel implements Comparable<DateModel> {

    private int month;
    private int day;


    public DateModel(int month, int day) {
        this.month = month;
        this.day = day;
    }

    @Override
    public int compareTo(DateModel o) {
        if (month == o.month) {
            // If months are equal, compare based on days
            return day - o.day;
        } else {
            // Compare based on months
            return month - o.month;
        }
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }
}
