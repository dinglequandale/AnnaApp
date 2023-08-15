package com.example.annaapp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Show_RecyclerViewAdapter extends RecyclerView.Adapter<Show_RecyclerViewAdapter.MyViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;
    Context context;
    ArrayList<ShowModel> showModels;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();


    public Show_RecyclerViewAdapter(Context context, ArrayList<ShowModel> showModels, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.showModels = showModels;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public Show_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflator = LayoutInflater.from(context);
        View view = inflator.inflate(R.layout.one_show, parent, false);
        return new Show_RecyclerViewAdapter.MyViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.showName.setText(showModels.get(position).getShowName());
        holder.crewName.setText(showModels.get(position).getCrewName());
        holder.date1.setText(showModels.get(position).getDate1());
        holder.date2.setText(showModels.get(position).getDate2());
            if(showModels.get(position).getImageUri() != null) {
                Glide.with(context)
                        .load(showModels.get(position).getImageUri())
                        .error("Loading error")
                        .into(holder.image);
            }
    }

    @Override
    public int getItemCount() {
        try {
            return showModels.size();
        } catch (NullPointerException e) {
            return 0;
        }

    }

    public void deleteMoverItem(int position) {
        FirebaseUser user = mAuth.getCurrentUser();

        ShowModel deletedShow = showModels.get(position);
        showModels.remove(position);
        notifyItemRemoved(position);

        assert user != null;
        DatabaseReference showRef = FirebaseDatabase.getInstance().getReference().child("users")
                .child(user.getUid());

        showRef.child("joined_shows").child(deletedShow.getAccess_code()).removeValue();
    }

    public void deleteManagerItem(int position) {
        FirebaseUser user = mAuth.getCurrentUser();

        ShowModel deletedShow = showModels.get(position);
        showModels.remove(position);
        notifyItemRemoved(position);

        //TODO: Deal with the snackbar mess
//        Snackbar.make(, deletedShow.getShowName(), Snackbar.LENGTH_LONG)

        assert user != null;
        DatabaseReference showRef = FirebaseDatabase.getInstance().getReference().child("users")
                .child(user.getUid());

        showRef.child("owned_shows").child(deletedShow.getAccess_code()).removeValue();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView showName;
        TextView crewName;
        TextView date1;
        TextView date2;

        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            image = itemView.findViewById(R.id.imageView2);
            showName = itemView.findViewById(R.id.showName);
            crewName = itemView.findViewById(R.id.crewName);
            date1 = itemView.findViewById(R.id.date1);
            date2 = itemView.findViewById(R.id.date2);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewInterface != null) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(position);
                        }
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (recyclerViewInterface != null) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onLongClick(position);
                            return true;
                        }

                    }
                    return false;
                }
            });
        }
    }
}
