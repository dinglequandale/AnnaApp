package com.example.annaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class JoinCreate extends AppCompatActivity implements AccessCodeDialog.AccessCodeListener {

    private Button btnJoin;
    private Button btnCreate;

    private boolean joinorCreate;
    private boolean clicked;

    private FirebaseUser user;
    private boolean dontCont = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_create);
        btnJoin = findViewById(R.id.btnJoin);
        btnCreate = findViewById(R.id.btnCreate);
        Button btnDone = findViewById(R.id.btnDone);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinorCreate = true;
                clicked = true;
                btnCreate.setBackgroundTintList(ContextCompat.getColorStateList(JoinCreate.this, R.color.purple_700));
                btnJoin.setBackgroundTintList(ContextCompat.getColorStateList(JoinCreate.this, R.color.purple_400));
            }
        });
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinorCreate = false;
                clicked = true;
                btnJoin.setBackgroundTintList(ContextCompat.getColorStateList(JoinCreate.this, R.color.purple_700));
                btnCreate.setBackgroundTintList(ContextCompat.getColorStateList(JoinCreate.this, R.color.purple_400));
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (joinorCreate) {
                    AccessCodeDialog accessCodeDialog = new AccessCodeDialog();
                    accessCodeDialog.show(getSupportFragmentManager(), "accesscode dialog");

                } else if (clicked) {
                    Intent intent = new Intent(JoinCreate.this, AddorEditShow.class);
                    startActivity(intent);

                }
            }
        });
    }

    //TODO: Check if this works: ALSO CHANGE THE NAME
    //TODO: Check if this works: ALSO CHANGE THE NAME
    //TODO: Check if this works: ALSO CHANGE THE NAME

    @Override
    public void applyTexts(String accessCode) {
        dontCont = false;
        assert user != null;
        String uid = user.getUid();
        DatabaseReference showListRef = FirebaseDatabase.getInstance()
                .getReference().child("users");

        DatabaseReference usersRef = showListRef.child(uid);
        usersRef.child("joined_shows").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String key = childSnapshot.getKey();
                    assert key != null;
                    if (key.equals(accessCode)) {
                            Toast.makeText(JoinCreate.this, "You already joined this show!", Toast.LENGTH_SHORT).show();
                            dontCont = true;
                            return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });



        showListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dontCont){
                    return;
                }

                for (DataSnapshot idNode : dataSnapshot.getChildren()) {
                    String userId = idNode.getKey();

                    assert userId != null;
                    DatabaseReference searchRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

                        searchRef.child("owned_shows").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                String key = childSnapshot.getKey();
                                assert key != null;
                                if (key.equals(accessCode)) {
                                    Toast.makeText(JoinCreate.this, "Supppp!! " + userId + " vs " + uid, Toast.LENGTH_SHORT).show();
                                    if (userId.equals(uid)) {
                                        Toast.makeText(JoinCreate.this, "You are trying to join your own show!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    String showName = childSnapshot.child("showName").getValue(String.class);
                                    String crewName = childSnapshot.child("crewName").getValue(String.class);
                                    String startingDate = childSnapshot.child("startingDate").getValue(String.class);
                                    String endingDate = childSnapshot.child("endingDate").getValue(String.class);
                                    String url = childSnapshot.child("url").getValue(String.class);

                                    childSnapshot.getRef().child("member_list").child(uid).setValue("username");


                                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference()
                                            .child("users").child(uid);

                                    DatabaseReference location_JoinedShows = usersRef.child("joined_shows");

                                    Map<String, Object> showData = new HashMap<>();

                                    showData.put("showName", showName);
                                    showData.put("crewName", crewName);
                                    showData.put("startingDate", startingDate);
                                    showData.put("endingDate", endingDate);
                                    showData.put("url", url);

                                    location_JoinedShows.child(accessCode).setValue(showData);


                                    Intent intent = new Intent(JoinCreate.this, MainActivity.class);
                                    startActivity(intent);

                                    return;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(JoinCreate.this, "User already owns this show", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
