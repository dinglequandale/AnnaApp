package com.example.annaapp.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.annaapp.AddorEditShow;

import com.example.annaapp.DateModel;
import com.example.annaapp.JoinCreate;
import com.example.annaapp.R;
import com.example.annaapp.RecyclerViewInterface;
import com.example.annaapp.ShowModel;
import com.example.annaapp.Show_RecyclerViewAdapter;


import com.example.annaapp.databinding.FragmentHomeBinding;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

public class HomeFragment extends Fragment implements RecyclerViewInterface {

    ArrayList<ShowModel> showModels;
    private RecyclerView recyclerView;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user;

    TextView var_date2;

    Show_RecyclerViewAdapter adapter;

    private String currUid;

    private SharedPreferences sharedPreferences;

    private FragmentHomeBinding binding;

    private ShowModel topShow;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.appBarMain.addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), JoinCreate.class);
                startActivity(intent);
            }
        });



        user = mAuth.getCurrentUser();
        assert user != null;
        currUid = user.getUid();


        loadData();

        recyclerView = root.findViewById(R.id.rvShows);

        recyclerView.setHasFixedSize(true);
        adapter = new Show_RecyclerViewAdapter(getActivity(), showModels, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        CardView cardTopShow = root.findViewById(R.id.cardtopShow);
        TextView var_showName = root.findViewById(R.id.var_showName);
        TextView var_crewName = root.findViewById(R.id.var_crewName);
        TextView var_date1 = root.findViewById(R.id.var_date1);
        TextView var_date2 = root.findViewById(R.id.var_date2);
        //ImageView showImage = root.findViewById(R.id.showName);


        topShow = getTopShow();
        if(topShow == null){
            cardTopShow.setVisibility(View.GONE);
        }
        else{
            fillTopShow(var_showName, var_crewName, var_date1, var_date2 /*, showImage*/);
            cardTopShow.setVisibility(View.VISIBLE);
        }

        return root;

    }

    private void loadData() {
        sharedPreferences = requireContext().
                getSharedPreferences("DATA", Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sharedPreferences.getString("show_data", null);

        Type type = new TypeToken<ArrayList<ShowModel>>(){

        }.getType();
        showModels = gson.fromJson(json,type);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(int position) {
        loadData();
        Toast.makeText(requireContext(), showModels.get(position).getUid() + ", " + currUid, Toast.LENGTH_SHORT).show();
        if(showModels.get(position).getUid().equals(currUid)) {

            Intent intent = new Intent(requireContext(), AddorEditShow.class);
            intent.putExtra("pos", position);

            startActivity(intent);
        }
    }

    @Override
    public void onLongClick(int position) {
        ShowModel deletedShow = showModels.get(position);
        if (deletedShow.getUid().equals(currUid)) {
            Snackbar.make(recyclerView, deletedShow.getShowName(), Snackbar.LENGTH_LONG)
                    .setAction("Are you sure you want to remove item?", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showModels.remove(position);

                            // Update SharedPreferences with the new data
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            Gson gson = new Gson();
                            String json = gson.toJson(showModels);
                            editor.putString("show_data", json);
                            editor.apply();

                            // Notify the adapter about the removal
                            adapter.notifyItemRemoved(position);
                        }
                    }).show();
        }
    }
    public ShowModel getTopShow() {
        if(showModels == null){
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
        if(show.getDate1().equals(topDate)){
            topShow = show;
            break;
        }
    }

    return topShow;
    }
    public void fillTopShow(TextView showName, TextView crewName, TextView date1, TextView date2){
        showName.setText(topShow.getShowName());
        crewName.setText(topShow.getCrewName());
        date1.setText(topShow.getDate1());
        date2.setText(topShow.getDate2());

        //TODO: Add image insertions
//        if (!TextUtils.isEmpty(topShow.getImageURL())) {
//            try {
//                Picasso.get().load(topShow.getImageURL()).into(showImage);
//            } catch (Exception e) {
//            }
//
//        }

    }
}