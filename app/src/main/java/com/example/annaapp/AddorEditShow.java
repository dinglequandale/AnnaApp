package com.example.annaapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;

public class AddorEditShow extends AppCompatActivity {

    MyApplication myApplication = (MyApplication) this.getApplication();
    RecyclerView recyclerView;
    private EditText edtShowName;
    private EditText edtCrewName;
    private EditText edtDate1;
    private EditText edtDate2;
    private EditText edtUrl;
    private String showName;
    private String crewName;
    private String date1;
    private String date2;
    private String url;

    private Button btnOk;
    private Button btnCancel;

    ArrayList<ShowModel> showModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addor_edit_show);

        edtShowName = findViewById(R.id.edtShowName);
        edtCrewName = findViewById(R.id.edtCrewName);
        edtDate1 = findViewById(R.id.edtDate1);
        edtDate2 = findViewById(R.id.edtDate2);
        edtUrl = findViewById(R.id.edtURL);
        showName = String.valueOf(edtShowName.getText());
        crewName = String.valueOf(edtCrewName.getText());
        date1 = String.valueOf(edtDate1.getText());
        date2 = String.valueOf(edtDate2.getText());
        url = String.valueOf(edtUrl.getText());

        btnOk = findViewById(R.id.btnOk);
        btnCancel = findViewById(R.id.btnCancel);

        showModels = myApplication.getShowModels();

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((String.valueOf(edtShowName.getText())).equals("")
                        || (String.valueOf(edtCrewName.getText())).equals("")) {
                    Toast.makeText(AddorEditShow.this, "There is missing info", Toast.LENGTH_SHORT).show();
                }
                //No url and missing date case
                else if ((String.valueOf(edtDate1.getText())).equals("")
                        || (String.valueOf(edtDate2.getText())).equals("")
                        && ((String.valueOf(edtUrl.getText())).equals(""))) {
                    int nextId = myApplication.getNextId();

                    String startingDate = edtDate1.getText().toString();
                    String endingDate = edtDate2.getText().toString();
                    if ((String.valueOf(edtDate1.getText())).equals("")) {
                        startingDate = "TBA";
                    }
                    if ((String.valueOf(edtDate2.getText())).equals("")) {
                        endingDate = "TBA";
                    }

                    ShowModel showModel = new ShowModel(nextId,
                            String.valueOf(edtShowName.getText()),
                            String.valueOf(edtCrewName.getText()),
                            startingDate,
                            endingDate);
                    showModels.add(showModel);
                    myApplication.setNextId(nextId++);
                    openMainActivity();
                }
                //Handle missing date and present url case
                else if ((String.valueOf(edtDate1.getText())).equals("")
                        || (String.valueOf(edtDate2.getText())).equals("")) {
                    int nextId = myApplication.getNextId();

                    String startingDate = edtDate1.getText().toString();
                    String endingDate = edtDate2.getText().toString();
                    if ((String.valueOf(edtDate1.getText())).equals("")) {
                        startingDate = "TBA";
                    }
                    if ((String.valueOf(edtDate2.getText())).equals("")) {
                        endingDate = "TBA";
                    }

                    ShowModel showModel = new ShowModel(nextId,
                            String.valueOf(edtShowName.getText()),
                            String.valueOf(edtCrewName.getText()),
                            startingDate,
                            endingDate,
                            String.valueOf(edtUrl.getText()));
                    showModels.add(showModel);
                    myApplication.setNextId(nextId++);
                    openMainActivity();
                }
                else{
                    int nextId = myApplication.getNextId();
                    ShowModel showModel = new ShowModel(nextId,
                            String.valueOf(edtShowName.getText()),
                            String.valueOf(edtCrewName.getText()),
                            String.valueOf(edtDate1.getText()),
                            String.valueOf(edtDate2.getText()),
                            String.valueOf(edtUrl.getText()));
                    showModels.add(showModel);
                    myApplication.setNextId(nextId++);
                    openMainActivity();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
            }
        });
    }

    public void openMainActivity() {
        Intent intent = new Intent(AddorEditShow.this, MainActivity.class);
        startActivity(intent);
    }

/*
    //TODO: FILL THESE OUT FOR DATA STORAGE
    public void loadData() {

    }

    public ArrayList<ShowModel> saveData(ArrayList<ShowModel> showModels) {
        return ();
    }
    */
}