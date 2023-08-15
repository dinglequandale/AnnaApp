package com.example.annaapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.Manifest;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

//TODO: Synchronize edit, onlick / onlongclick functionality

public class AddorEditShow extends AppCompatActivity implements DataLoadCallback {

    int editId;
    TextView txtTitle;

    private ActivityResultLauncher<String> photoLibraryLauncher;
    private ImageView displayShowImage;
    private EditText edtShowName;
    private EditText edtCrewName;
    private EditText edtDate1;
    private EditText edtDate2;
    //private EditText edtUrl;

    private Button btnOk;
    private Button btnCancel;

    private ShowModel show;

    ArrayList<ShowModel> showModels;

    private FirebaseUser user;
    private FirebaseAuth mAuth;

    private String uid;
    private FloatingActionButton fabUploadImage;
    //private boolean fabAdded;
    String image_url = "";

    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addor_edit_show);
        edtShowName = findViewById(R.id.edtShowName);
        edtCrewName = findViewById(R.id.edtCrewName);
        edtDate1 = findViewById(R.id.edtDate1);
        edtDate2 = findViewById(R.id.edtDate2);
        fabUploadImage = findViewById(R.id.fabUploadImage);
        displayShowImage = findViewById(R.id.displayShowImage);
        displayShowImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_square));

        btnOk = findViewById(R.id.btnOk);
        btnCancel = findViewById(R.id.btnCancel);

        Intent intent = getIntent();
        editId = intent.getIntExtra("pos", -1);
        txtTitle = findViewById(R.id.titleAddorEdit);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        assert user != null;
        uid = user.getUid();

        photoLibraryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                globalizeImageURL(uri);
                displayShowImage.setImageURI(uri);
            }
        });

        loadFireBaseData(this);
    }

    private void accessPhotoLibrary() {
        photoLibraryLauncher.launch("image/*");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with your logic
                accessPhotoLibrary();
            } else {
                Toast.makeText(this, "Access Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void openMainActivity() {
        Intent intent = new Intent(AddorEditShow.this, MainActivity.class);
        startActivity(intent);
    }


//    private void loadData() {
//        SharedPreferences sharedPreferences = getApplicationContext().
//                getSharedPreferences("DATA", MODE_PRIVATE);
//        Gson gson = new Gson();
//        String json = sharedPreferences.getString("show_data", null);
//        Type type = new TypeToken<ArrayList<ShowModel>>() {
//
//        }.getType();
//        showModels = gson.fromJson(json, type);
//        if (showModels == null) {
//            showModels = new ArrayList<>();
//        }
//    }
//
//    private void saveData(String access_code, String showName, String crewName,
//                          String startingDate, String endingDate,
//                          String uri) {
//        SharedPreferences sharedPreferences = getApplicationContext().
//                getSharedPreferences("DATA", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//        Gson gson = new Gson();
//        showModels.add(new ShowModel(access_code, showName,
//                crewName, startingDate, endingDate, uri));
//        String json = gson.toJson(showModels);
//        editor.putString("show_data", json);
//        editor.apply();
//        loadData();
//    }
//
//    private void saveData(String access_code, String showName, String crewName,
//                          String startingDate, String endingDate) {
//        SharedPreferences sharedPreferences = getApplicationContext().
//                getSharedPreferences("DATA", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//        Gson gson = new Gson();
//        showModels.add(new ShowModel(access_code, showName, crewName,
//                startingDate, endingDate));
//        String json = gson.toJson(showModels);
//        editor.putString("show_data", json);
//        editor.apply();
//        loadData();
//    }
//
//    private void saveData() {
//        SharedPreferences sharedPreferences = getApplicationContext().
//                getSharedPreferences("DATA", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//        Gson gson = new Gson();
//
//        String json = gson.toJson(showModels);
//        editor.putString("show_data", json);
//        editor.apply();
//        loadData();
//    }

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

        //TODO: Check that this works
        //TODO: Need to actually search through the ENTIRE database

        DatabaseReference showListRef = FirebaseDatabase.getInstance().getReference().child("users");

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

    @Override
    public void onDataLoaded(ArrayList<ShowModel> data) {
        showModels = data;
        if (editId >= 0) {
            Toast.makeText(this, "Target id:" + editId, Toast.LENGTH_SHORT).show();
            txtTitle.setText("Edit Show");
            Toast.makeText(this, String.valueOf(showModels.size()), Toast.LENGTH_SHORT).show();
            show = showModels.get(editId);

            edtCrewName.setText(show.getCrewName());
            edtShowName.setText(show.getShowName());
            edtDate1.setText(show.getDate1());
            edtDate2.setText(show.getDate2());
            if(show.getImageUri() != null) {
                if (!show.getImageUri().isEmpty()) {
                    Glide.with(this)
                            .load(show.getImageUri())
                            .apply(new RequestOptions().placeholder(R.drawable.ic_square)) // Placeholder image while loading
                            .into(displayShowImage);
                }
            }
        } else {
            txtTitle.setText("Add Show");
        }

        fabUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AddorEditShow.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    accessPhotoLibrary();
                } else {
                    // Permission not granted, request it from the user
                    ActivityCompat.requestPermissions(AddorEditShow.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
                    // requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
                }
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                //Checking for valid date
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
                        Toast.makeText(AddorEditShow.this, "Invalid date format. Day and month must be numbers.", Toast.LENGTH_SHORT).show();

                        return;
                    }
                    int date1Slash = (edtDate1.getText().toString()).indexOf("/");
                    if (date1Slash == edtDate1.getText().length() - 1 || date1Slash == 0) {
                        Toast.makeText(AddorEditShow.this, "Invalid date format. Day and month must be numbers.", Toast.LENGTH_SHORT).show();
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
                    int date2Slash = (edtDate2.getText().toString()).indexOf("/");
                    if (date2Slash == edtDate2.getText().length() - 1 || date2Slash == 0) {
                        Toast.makeText(AddorEditShow.this, "Invalid date format. Day and month must be numbers.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else if (!(edtDate1.getText().toString().contains("/")) || !(edtDate2.getText().toString().contains("/"))) {
                    if (!(edtDate1.getText().toString().isEmpty()) && !(edtDate2.getText().toString().isEmpty())) {
                        Toast.makeText(AddorEditShow.this, "Invalid date format. Must be of form M/D.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                //Editing a show
                if (editId >= 0) {
                    ShowModel showReplacement;
                    String inherited_accessCode = showModels.get(editId).getAccess_code();
                    //TODO: URI

                    if (image_url.isEmpty()) {
                        showReplacement = new ShowModel(inherited_accessCode, edtShowName.getText().toString(), edtCrewName.getText().toString(),
                                edtDate1.getText().toString(), edtDate2.getText().toString(), showModels.get(editId).getImageUri());

                    } else {
                        showReplacement = new ShowModel(inherited_accessCode, edtShowName.getText().toString(), edtCrewName.getText().toString(),
                                edtDate1.getText().toString(), edtDate2.getText().toString(), image_url);
                    }
                    showModels.set(editId, showReplacement);
//                    saveData();
                    editShowInDatabase(inherited_accessCode, edtShowName.getText().toString(), edtCrewName.getText().toString(),
                            edtDate1.getText().toString(), edtDate2.getText().toString(), image_url);

//                    openMainActivity();
                } else {
                    //Add a show

                    String startingDate = edtDate1.getText().toString();
                    String endingDate = edtDate2.getText().toString();


                    if (image_url.isEmpty()) {

                        Toast.makeText(AddorEditShow.this, "Current id:" + editId, Toast.LENGTH_LONG).show();


                        if ((String.valueOf(edtDate1.getText())).equals("")) {
                            startingDate = "TBA";
                        }
                        if ((String.valueOf(edtDate2.getText())).equals("")) {
                            endingDate = "TBA";
                        }
//                        saveData("", String.valueOf(edtShowName.getText()),
//                                String.valueOf(edtCrewName.getText()),
//                                startingDate,
//                                endingDate);

                        addShowToDatabase(String.valueOf(edtShowName.getText()),
                                String.valueOf(edtCrewName.getText()),
                                startingDate,
                                endingDate, "");

//                        openMainActivity();
                    } else {
                        if ((String.valueOf(edtDate1.getText())).equals("")) {
                            startingDate = "TBA";
                        }
                        if ((String.valueOf(edtDate2.getText())).equals("")) {
                            endingDate = "TBA";
                        }

                        //TODO: Image URI

//                        saveData("", String.valueOf(edtShowName.getText()),
//                                String.valueOf(edtCrewName.getText()),
//                                startingDate,
//                                endingDate,
//                                image_url);

                        addShowToDatabase(String.valueOf(edtShowName.getText()),
                                String.valueOf(edtCrewName.getText()),
                                startingDate,
                                endingDate, image_url);


//                        openMainActivity();
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

    @Override
    public void onDataLoadError(String errorMessage) {

    }

    interface OnAccessCodeGeneratedListener {
        void onAccessCodeGenerated(String accessCode);
    }
// TODO: Sychronize this
    public void addShowToDatabase(String showName, String crewName,
                                  String startingDate, String endingDate, String url) {

        assert uid != null;

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        Map<String, Object> userData = new HashMap<>();

        userData.put("showName", showName);
        userData.put("crewName", crewName);
        userData.put("startingDate", startingDate);
        userData.put("endingDate", endingDate);
        userData.put("url",url);

        generateUniqueAccessCode(new OnAccessCodeGeneratedListener() {
            @Override
            public void onAccessCodeGenerated(String accessCode) {
                usersRef.child(uid).child("owned_shows").child(accessCode).setValue(userData);

                openMainActivity();
            }
        });
    }

    public void editShowInDatabase(String access_code, String showName, String crewName,
                                   String startingDate, String endingDate,
                                   String url) {

        assert uid != null;
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

        Map<String, Object> updates = new HashMap<>();
        updates.put("showName", showName);
        updates.put("crewName", crewName);
        updates.put("startingDate", startingDate);
        updates.put("endingDate", endingDate);
        updates.put("url", url);

        usersRef.child("owned_shows").child(access_code).updateChildren(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Update successful
                        // Handle any additional actions if needed
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Update failed
                        // Handle the error if needed
                    }
                });

        openMainActivity();
    }
    public void globalizeImageURL(Uri imageUri) {

        assert uid != null;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        String filename = "profile_image_" + System.currentTimeMillis() + ".jpg";

        StorageReference imageRef = storageRef.child("profile_images/" + filename);

        UploadTask uploadTask = imageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {

            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                image_url = uri.toString();
                Toast.makeText(this, "download url: " + image_url, Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                // Handle any errors that occurred while getting the download URL
                Toast.makeText(this, "Failed to get image download URL", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            // Handle any errors that occurred during image upload
            Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
        });
    }

    public void loadFireBaseData(DataLoadCallback callback) {
        ArrayList<ShowModel> shows = new ArrayList<>();
        assert user != null;
        String uid = user.getUid();
        DatabaseReference nodeRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        nodeRef.child("owned_shows").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot showNode : dataSnapshot.getChildren()) {
                    String showAccessCode = showNode.getKey();
                    String showName = showNode.child("showName").getValue(String.class);
                    String crewName = showNode.child("crewName").getValue(String.class);
                    String startingDate = showNode.child("startingDate").getValue(String.class);
                    String endingDate = showNode.child("endingDate").getValue(String.class);
                    String url = showNode.child("url").getValue(String.class);

                    ShowModel show;
                    assert url != null;
                    if(url.isEmpty()) {
                        show = new ShowModel(showAccessCode, showName, crewName, startingDate, endingDate);
                    }
                    else{
                        show = new ShowModel(showAccessCode, showName, crewName, startingDate, endingDate, url);
                    }
                    shows.add(show);

                    //adapter.notifyItemInserted(showModels.size()-1);

                    //saveData();
                }
                showModels = shows;
                callback.onDataLoaded(showModels);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataLoadError(databaseError.getMessage());
            }
        });
    }

}