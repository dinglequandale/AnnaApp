package com.example.annaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class AddorEditShow extends AppCompatActivity {

    int editId;
    TextView txtTitle;
    private EditText edtShowName;
    private EditText edtCrewName;
    private EditText edtDate1;
    private EditText edtDate2;
    private EditText edtUrl;

    private Button btnOk;
    private Button btnCancel;

    private ShowModel show;

    ArrayList<ShowModel> showModels;

    ArrayList<String> collaborators;

    private FirebaseUser user;
    private FirebaseAuth mAuth;

    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addor_edit_show);

        //TODO: Add a collaboration feature
        collaborators = new ArrayList<>();


        edtShowName = findViewById(R.id.edtShowName);
        edtCrewName = findViewById(R.id.edtCrewName);
        edtDate1 = findViewById(R.id.edtDate1);
        edtDate2 = findViewById(R.id.edtDate2);
        edtUrl = findViewById(R.id.edtURL);

        btnOk = findViewById(R.id.btnOk);
        btnCancel = findViewById(R.id.btnCancel);

        Intent intent = getIntent();
        editId = intent.getIntExtra("pos", -1);
        txtTitle = findViewById(R.id.titleAddorEdit);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        assert user != null;
        uid = user.getUid();


        loadData();

        if(editId >= 0) {
            Toast.makeText(this, "Target id:" + editId, Toast.LENGTH_SHORT).show();
            txtTitle.setText("Edit Show");

            show = showModels.get(editId);

            edtCrewName.setText(show.getCrewName());
            edtShowName.setText(show.getShowName());
            edtDate1.setText(show.getDate1());
            edtDate2.setText(show.getDate2());
            edtUrl.setText(show.getImageURL());
        }
        else{
            txtTitle.setText("Add Show");
        }

        btnOk.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                if(editId >= 0){
                    ShowModel showReplacement;
                    if (edtUrl.getText().toString().equals("")) {
                        showReplacement = new ShowModel(uid, edtShowName.getText().toString(), edtCrewName.getText().toString(),
                                edtDate1.getText().toString(), edtDate2.getText().toString());

                    }
                    else{
                        showReplacement = new ShowModel(uid, edtShowName.getText().toString(), edtCrewName.getText().toString(),
                                edtDate1.getText().toString(), edtDate2.getText().toString(), edtUrl.getText().toString());
                    }
                    showModels.set(editId, showReplacement);
                    saveData();
                    Toast.makeText(AddorEditShow.this, "New show model name" + showModels.get(editId).getShowName(), Toast.LENGTH_LONG).show();



                    openMainActivity();

                }
                else{

                    String startingDate = edtDate1.getText().toString();
                    String endingDate = edtDate2.getText().toString();



                    if ((String.valueOf(edtShowName.getText())).equals("")
                            || (String.valueOf(edtCrewName.getText())).equals("")) {
                        Toast.makeText(AddorEditShow.this, "There is missing info", Toast.LENGTH_SHORT).show();
                    }

                    else if ((String.valueOf(edtUrl.getText())).equals("")) {

                        Toast.makeText(AddorEditShow.this, "Current id:" + editId, Toast.LENGTH_LONG).show();


                        if ((String.valueOf(edtDate1.getText())).equals("")) {
                            startingDate = "TBA";
                        }
                        if ((String.valueOf(edtDate2.getText())).equals("")) {
                            endingDate = "TBA";
                        }
                        saveData(uid, String.valueOf(edtShowName.getText()),
                                String.valueOf(edtCrewName.getText()),
                                startingDate,
                                endingDate);

                        openMainActivity();
                    }

                    else{
                        if ((String.valueOf(edtDate1.getText())).equals("")) {
                            startingDate = "TBA";
                        }
                        if ((String.valueOf(edtDate2.getText())).equals("")) {
                            endingDate = "TBA";
                        }

                        saveData(uid, String.valueOf(edtShowName.getText()),
                                String.valueOf(edtCrewName.getText()),
                                startingDate,
                                endingDate,
                                String.valueOf(edtUrl.getText()));


                        openMainActivity();
                    }
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


    private void loadData() {
        SharedPreferences sharedPreferences = getApplicationContext().
                getSharedPreferences("DATA", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("show_data", null);
        Type type = new TypeToken<ArrayList<ShowModel>>(){

        }.getType();
        showModels = gson.fromJson(json,type);
        if(showModels == null){
            showModels = new ArrayList<>();
        }
    }

    private void saveData(String uid, String showName, String crewName,
                          String startingDate, String endingDate,
                          String url) {
        SharedPreferences sharedPreferences = getApplicationContext().
                getSharedPreferences("DATA", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        showModels.add(new ShowModel(uid, showName,
                crewName, startingDate, endingDate, url));
        String json = gson.toJson(showModels);
        editor.putString("show_data",json);
        editor.apply();
        loadData();
    }
    private void saveData(String uid, String showName, String crewName,
                          String startingDate, String endingDate) {
        SharedPreferences sharedPreferences = getApplicationContext().
                getSharedPreferences("DATA", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        showModels.add(new ShowModel(uid, showName, crewName,
                startingDate, endingDate));
        String json = gson.toJson(showModels);
        editor.putString("show_data",json);
        editor.apply();
        loadData();
    }
    private void saveData(){
        SharedPreferences sharedPreferences = getApplicationContext().
                getSharedPreferences("DATA", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();

        String json = gson.toJson(showModels);
        editor.putString("show_data",json);
        editor.apply();
        loadData();
    }
}