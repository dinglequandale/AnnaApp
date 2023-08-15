package com.example.annaapp;

import java.util.ArrayList;

public interface DataLoadCallback {
    void onDataLoaded(ArrayList<ShowModel> data);
    void onDataLoadError(String errorMessage);
}