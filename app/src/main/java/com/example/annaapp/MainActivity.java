package com.example.annaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.annaapp.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Array;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements RecyclerViewInterface {
    ArrayList<ShowModel> showModels;
    private RecyclerView recyclerView;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user;

    private String currUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = mAuth.getCurrentUser();
        assert user != null;
        currUid = user.getUid();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.appBarMain.addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddorEditShow.class);
                startActivity(intent);
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation item clicks here
                int itemId = item.getItemId();

                if (itemId == R.id.nav_profile) {
                    // Perform navigation to the profile screen
                    NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main);
                    navController.navigate(R.id.nav_profile);
                }

                return true;
            }
        });


        SharedPreferences sharedPreferences = getApplicationContext().
                getSharedPreferences("DATA", MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sharedPreferences.getString("show_data", null);

        Type type = new TypeToken<ArrayList<ShowModel>>(){

        }.getType();
        showModels = gson.fromJson(json,type);

        recyclerView = findViewById(R.id.rvShows);
        recyclerView.setHasFixedSize(true);
        Show_RecyclerViewAdapter adapter = new Show_RecyclerViewAdapter(this, showModels, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(R.id.action_settings == item.getItemId()){
            Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onItemClick(int position) {

        SharedPreferences sharedPreferences = getApplicationContext().
                getSharedPreferences("DATA", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("show_data", null);
        Type type = new TypeToken<ArrayList<ShowModel>>(){

        }.getType();
        showModels = gson.fromJson(json,type);
        Toast.makeText(this, showModels.get(position).getUid() + ", " + currUid, Toast.LENGTH_SHORT).show();
        if(showModels.get(position).getUid().equals(currUid)) {

            Intent intent = new Intent(MainActivity.this, AddorEditShow.class);
            intent.putExtra("pos", position);

            startActivity(intent);
        }
    }

    @Override
    public void onDragClick(int position) {

    }
}