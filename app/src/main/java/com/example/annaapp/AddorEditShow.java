package com.example.annaapp;

import androidx.annotation.NonNull;
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

import com.example.annaapp.ui.home.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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

    private FirebaseUser user;
    private FirebaseAuth mAuth;

    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addor_edit_show);


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

        if (editId >= 0) {
            Toast.makeText(this, "Target id:" + editId, Toast.LENGTH_SHORT).show();
            txtTitle.setText("Edit Show");

            show = showModels.get(editId);

            edtCrewName.setText(show.getCrewName());
            edtShowName.setText(show.getShowName());
            edtDate1.setText(show.getDate1());
            edtDate2.setText(show.getDate2());
            edtUrl.setText(show.getImageURL());
        } else {
            txtTitle.setText("Add Show");
        }

        btnOk.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if ((String.valueOf(edtShowName.getText())).equals("")
                        || (String.valueOf(edtCrewName.getText())).equals("")) {
                    Toast.makeText(AddorEditShow.this, "There is missing info", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (edtDate1.getText().toString().contains("/")) {
                    try {
                        int date1Slash = (edtDate1.getText().toString()).indexOf("/");
                        Integer.parseInt(edtDate1.getText().toString().substring(0, date1Slash)
                                + edtDate1.getText().toString().substring(date1Slash + 1));
                    } catch (NumberFormatException e) {
                        Toast.makeText(AddorEditShow.this, "\"Invalid date format. Day and month must be numbers.", Toast.LENGTH_SHORT).show();

                        return;
                    }
                }
                if (edtDate2.getText().toString().contains("/")) {
                    try {
                        int date2Slash = (edtDate2.getText().toString()).indexOf("/");
                        Integer.parseInt(edtDate2.getText().toString().substring(0, date2Slash)
                                + edtDate2.getText().toString().substring(date2Slash + 1));
                    } catch (NumberFormatException e) {
                        Toast.makeText(AddorEditShow.this, "\"Invalid date format. Day and month must be numbers.", Toast.LENGTH_SHORT).show();

                        return;
                    }
                } else if (!(edtDate1.getText().toString().contains("/")) || !(edtDate2.getText().toString().contains("/"))) {
                    if (!(edtDate1.getText().toString().isEmpty()) && !(edtDate2.getText().toString().isEmpty())) {
                        Toast.makeText(AddorEditShow.this, "Invalid date format. Must be of form M/D.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (editId >= 0) {
                    ShowModel showReplacement;
                    if (edtUrl.getText().toString().equals("")) {
                        showReplacement = new ShowModel(uid, edtShowName.getText().toString(), edtCrewName.getText().toString(),
                                edtDate1.getText().toString(), edtDate2.getText().toString());

                    } else {
                        showReplacement = new ShowModel(uid, edtShowName.getText().toString(), edtCrewName.getText().toString(),
                                edtDate1.getText().toString(), edtDate2.getText().toString(), edtUrl.getText().toString());
                    }
                    showModels.set(editId, showReplacement);
                    saveData();
                    Toast.makeText(AddorEditShow.this, "New show model name" + showModels.get(editId).getShowName(), Toast.LENGTH_LONG).show();


                    openMainActivity();

                } else {

                    String startingDate = edtDate1.getText().toString();
                    String endingDate = edtDate2.getText().toString();


                    if ((String.valueOf(edtUrl.getText())).equals("")) {

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

                        addShowToDatabase(String.valueOf(edtShowName.getText()),
                                String.valueOf(edtCrewName.getText()),
                                startingDate,
                                endingDate, "");

                        openMainActivity();
                    } else {
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

                        addShowToDatabase(String.valueOf(edtShowName.getText()),
                                String.valueOf(edtCrewName.getText()),
                                startingDate,
                                endingDate, String.valueOf(edtUrl.getText()));


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
        Type type = new TypeToken<ArrayList<ShowModel>>() {

        }.getType();
        showModels = gson.fromJson(json, type);
        if (showModels == null) {
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
        editor.putString("show_data", json);
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
        editor.putString("show_data", json);
        editor.apply();
        loadData();
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getApplicationContext().
                getSharedPreferences("DATA", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();

        String json = gson.toJson(showModels);
        editor.putString("show_data", json);
        editor.apply();
        loadData();
    }

    public void generateUniqueAccessCode(OnAccessCodeGeneratedListener listener) {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = upper.toLowerCase();
        String nums = "0123456789";
        String combination = upper + lower + nums;
        Random random = new Random();

        char[] access_code_array = new char[6];
        for (int i = 0; i < 6; i++) {
            access_code_array[i] = combination.charAt(random.nextInt(combination.length()));
        }
        final String access_code = new String(access_code_array);

        assert uid != null;

        DatabaseReference showListRef = FirebaseDatabase.getInstance().getReference().child("owned_shows").child(uid);

        Query accessCodeQuery = showListRef.orderByValue().equalTo(access_code);

        accessCodeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    generateUniqueAccessCode(listener);
                } else {
                    listener.onAccessCodeGenerated(access_code);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle the error if the query is canceled or encounters an error
                // Pass an error state to the listener if needed
            }
        });
    }

    // Define a listener interface to receive the generated access code
    interface OnAccessCodeGeneratedListener {
        void onAccessCodeGenerated(String accessCode);
    }

    public void addShowToDatabase(String showName, String crewName,
                                  String startingDate, String endingDate,
                                  String url) {

        assert uid != null;


        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        Map<String, Object> userData = new HashMap<>();

        //TODO: CHECK IF WORKS: IDEA: Only put shared information into database, i.e. the show which the user owns
        //userData.put("owner_id", uid);
        userData.put("showName", showName);
        userData.put("crewName", crewName);
        userData.put("startingDate", startingDate);
        userData.put("endingDate", endingDate);
        userData.put("url", url);
        // Add other user-specific data here if needed

        generateUniqueAccessCode(new OnAccessCodeGeneratedListener() {
            @Override
            public void onAccessCodeGenerated(String accessCode) {
                usersRef.child(uid).child("owned_shows").child(accessCode).setValue(userData);
            }
        });
    }
}