package com.example.annaapp.ui.manager_homepage;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.annaapp.AddorEditShow;

import com.example.annaapp.DataLoadCallback;
import com.example.annaapp.DateModel;
import com.example.annaapp.DisplayMethods;
import com.example.annaapp.JoinCreate;
import com.example.annaapp.R;
import com.example.annaapp.RecyclerViewInterface;
import com.example.annaapp.ShowModel;
import com.example.annaapp.Show_RecyclerViewAdapter;
import com.example.annaapp.SwipeToDeleteCallbackManager;
import com.example.annaapp.databinding.FragmentManagerBinding;
import com.example.annaapp.databinding.FragmentMoverBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

public class ManagerFragment extends Fragment implements RecyclerViewInterface, DataLoadCallback, DisplayMethods {

    //TODO: Check if this works:
    //TODO: Move EVERYTHING to firebase, no sharedPreferences
    //TODO: EDIT: SharedPreferences MIGHT be a better course of action for local storage during
    //app usage

    ArrayList<ShowModel> showModels = new ArrayList<>();
    private RecyclerView recyclerView;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    Show_RecyclerViewAdapter adapter;

    private SharedPreferences sharedPreferences;

    private @NonNull FragmentManagerBinding binding;

    private ShowModel topShow;

    View root;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ManagerViewModel homeViewModel = new ViewModelProvider(this).get(ManagerViewModel.class);

        binding = FragmentManagerBinding.inflate(inflater, container, false);
        root = binding.getRoot();



        binding.layoutContentMain.addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), JoinCreate.class);
                startActivity(intent);
            }
        });
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        //loadData();
        loadFireBaseData(this);

        return root;

    }

    private void loadData() {
        sharedPreferences = requireContext().
                getSharedPreferences("DATA", MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sharedPreferences.getString("show_data", null);

        Type type = new TypeToken<ArrayList<ShowModel>>() {

        }.getType();
        showModels = gson.fromJson(json, type);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(int position) {
        loadData();
        //Toast.makeText(requireContext(), showModels.get(position).getUid() + ", " + currUid, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(requireContext(), AddorEditShow.class);
        intent.putExtra("pos", position);

        startActivity(intent);
    }

    @Override
    public void onLongClick(int position) {
        ShowModel deletedShow = showModels.get(position);
        //if (deletedShow.getUid().equals(currUid)) {
        Snackbar.make(recyclerView, deletedShow.getShowName(), Snackbar.LENGTH_LONG)
                .setAction("Are you sure you want to remove item?", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), showModels.get(position).getAccess_code(), Toast.LENGTH_SHORT).show();
                        deleteShowFromDatabase(showModels.get(position).getAccess_code());
                        showModels.remove(position);
                        // Update SharedPreferences with the new data
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        Gson gson = new Gson();
//                        String json = gson.toJson(showModels);
//                        editor.putString("show_data", json);
//                        editor.apply();


                        // Notify the adapter about the removal
                        adapter.notifyItemRemoved(position);


                    }
                }).show();
    }

    @Override
    public ShowModel getTopShow() {
        if (showModels == null) {
            return null;
        }
        if (showModels.isEmpty()) {
            return null;
        }
        ArrayList<DateModel> startDates = new ArrayList<>();
        for (ShowModel show : showModels) {
            if (!show.getDate1().equals("TBA")) {
                String date = show.getDate1();


                int indOfSlash = date.indexOf("/");
                int day = Integer.parseInt(date.substring(indOfSlash + 1));
                int month = Integer.parseInt(date.substring(0, indOfSlash));
                DateModel dateModel = new DateModel(month, day);

                startDates.add(dateModel);
            }
        }

        if (startDates.isEmpty()) {
            return null; // Handle the case when all dates are "TBA"
        }

        Collections.sort(startDates);

        //Now we find the most recent date by taking the first element:
        String topDate = startDates.get(0).getMonth() + "/" + startDates.get(0).getDay();

        ShowModel topShow = null;
        for (ShowModel show : showModels) {
            if (show.getDate1().equals(topDate)) {
                topShow = show;
                break;
            }
        }

        return topShow;
    }

    @Override
    public void fillTopShow(TextView showName, TextView crewName, TextView date1, TextView date2, ImageView showImage) {
        showName.setText(topShow.getShowName());
        crewName.setText(topShow.getCrewName());
        date1.setText(topShow.getDate1());
        date2.setText(topShow.getDate2());

        Glide.with(this)
                .load(topShow.getImageUri())
                .override(100)
                .fitCenter()
                .into(showImage);

    }

    public void deleteShowFromDatabase(String access_code) {
        String uid = user.getUid();
        Toast.makeText(getContext(), "DELETED_SHOW CODE: " + access_code, Toast.LENGTH_SHORT).show();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(uid);
        databaseRef.child("owned_shows").child(access_code).removeValue();
//        userRef.removeValue()
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(getContext(), "Didn't work", Toast.LENGTH_SHORT).show();
//                    }
//                });
    }

    @Override
    public void loadFireBaseData(DataLoadCallback callback) {
        ArrayList<ShowModel> shows = new ArrayList<>();
        assert user != null;
        String uid = user.getUid();
        Toast.makeText(getContext(), uid, Toast.LENGTH_SHORT).show();
        DatabaseReference nodeRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        nodeRef.child("owned_shows").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot showNode : dataSnapshot.getChildren()) {
                    String showAccessCode = showNode.getKey();
                    if (showAccessCode == null) {
                        Toast.makeText(getContext(), "Nothing to see here", Toast.LENGTH_SHORT).show();
                        return;
                    }

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
    private void saveData() {
        SharedPreferences sharedPreferences = getContext().
                getSharedPreferences("DATA", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();

        String json = gson.toJson(showModels);
        editor.putString("show_data", json);
        editor.apply();
        loadData();
    }

    @Override
    public void onDataLoaded(ArrayList<ShowModel> data) {
        assert root != null;
        recyclerView = root.findViewById(R.id.rvShows);

        showModels = data;
        recyclerView.setHasFixedSize(true);
        adapter = new Show_RecyclerViewAdapter(getActivity(), showModels, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        CardView cardTopShow = root.findViewById(R.id.cardtopShow);
        TextView var_showName = root.findViewById(R.id.var_showName);
        TextView var_crewName = root.findViewById(R.id.var_crewName);
        TextView var_date1 = root.findViewById(R.id.var_date1);
        TextView var_date2 = root.findViewById(R.id.var_date2);
        ImageView showImage = root.findViewById(R.id.var_imageView2);
        ConstraintLayout layoutContentMain = root.findViewById(R.id.layoutContentMain);
        TextView txtEmpty = root.findViewById(R.id.txtEmpty);

        recyclerView.setHasFixedSize(true);
        adapter = new Show_RecyclerViewAdapter(getActivity(), showModels, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallbackManager(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        if(showModels == null || showModels.isEmpty()){
            txtEmpty.setVisibility(View.VISIBLE);
        }
        else{
            txtEmpty.setVisibility(View.GONE);
        }

        topShow = getTopShow();
        if(topShow == null){
            cardTopShow.setVisibility(View.GONE);
            layoutContentMain.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.purple_600));
        }
        else {
            fillTopShow(var_showName, var_crewName, var_date1, var_date2, showImage);
            layoutContentMain.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.purple_400));
            cardTopShow.setVisibility(View.VISIBLE);
            txtEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDataLoadError(String errorMessage) {

    }

}