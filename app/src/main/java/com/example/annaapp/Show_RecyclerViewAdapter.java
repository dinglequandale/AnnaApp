package com.example.annaapp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Show_RecyclerViewAdapter extends RecyclerView.Adapter<Show_RecyclerViewAdapter.MyViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;
    Context context;
    ArrayList<ShowModel> showModels;


    public Show_RecyclerViewAdapter(Context context, ArrayList<ShowModel> showModels, RecyclerViewInterface  recyclerViewInterface) {
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
        if(TextUtils.isEmpty(showModels.get(position).getImageURL())){
            (holder.image).setImageDrawable(ContextCompat.getDrawable(this.context, R.drawable.ic_square));
        }
        else{
            try{
                Picasso.get().load(showModels.get(position).getImageURL()).into(holder.image);
            }catch(Exception e){
                (holder.image).setImageDrawable(ContextCompat.getDrawable(this.context, R.drawable.ic_square));;
            }

        }

    }

    @Override
    public int getItemCount() {
        try{
            return showModels.size();
        }catch (NullPointerException e){
            return 0;
        }

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
                    if(recyclerViewInterface != null){
                        int position = getAdapterPosition();

                        if(position != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(position);
                        }
                    }
                }
            });

        }
    }
}
