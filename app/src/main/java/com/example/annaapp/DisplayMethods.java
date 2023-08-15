package com.example.annaapp;

import android.widget.ImageView;
import android.widget.TextView;

public interface DisplayMethods {
    ShowModel getTopShow();
    void fillTopShow(TextView showName, TextView crewName, TextView date1, TextView date2, ImageView showImage);
    void loadFireBaseData(DataLoadCallback callback);


}
