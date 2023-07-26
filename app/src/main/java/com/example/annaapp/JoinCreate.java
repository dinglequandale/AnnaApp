package com.example.annaapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class JoinCreate extends AppCompatActivity implements AccessCodeDialog.AccessCodeListener {

    private Button btnJoin;
    private Button btnCreate;
    private Button btnDone;

    private boolean joinorCreate;
    private boolean clicked;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_create);
        btnJoin = findViewById(R.id.btnJoin);
        btnCreate = findViewById(R.id.btnCreate);
        btnDone = findViewById(R.id.btnDone);
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
                    Intent intent = new Intent(JoinCreate.this,AddorEditShow.class);
                    startActivity(intent);

                }
            }
        });

    }

    @Override
    public void applyTexts(String accessCode) {
        Toast.makeText(this, accessCode, Toast.LENGTH_SHORT).show();
    }
}