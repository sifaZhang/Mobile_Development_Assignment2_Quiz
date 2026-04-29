package com.group1.quiz.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group1.quiz.common.AppConstants;
import com.group1.quiz.models.TournamentModel;
import com.group1.quiz.R;

import java.util.ArrayList;

public class TournamentAdapter extends RecyclerView.Adapter<TournamentAdapter.ViewHolder> {

    private ArrayList<TournamentModel> list;
    private OnTournamentActionListener listener;

    public TournamentAdapter(ArrayList<TournamentModel> list, OnTournamentActionListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tournament, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TournamentModel t = list.get(position);

        holder.tvName.setText(t.name);
        holder.tvCategory.setText("Category: " + t.category);
        holder.tvDifficulty.setText("Difficulty: " + t.difficulty);
        holder.tvDate.setText("Date: " + t.startDate + " - " + t.endDate);
        holder.ratingBar.setRating((float) t.rating);
        if (t.ratingCount == 0){
            holder.tvRatingCount.setText("Rating: " + AppConstants.NO_RATINGS);
        } else {
            holder.tvRatingCount.setText("Rating: (" + t.ratingCount + " ratings)");
        }

        // Edit button
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(t);
        });

        // Delete button
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(t);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvCategory, tvDifficulty, tvDate, tvRatingCount;
        RatingBar ratingBar;
        Button btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDifficulty = itemView.findViewById(R.id.tvDifficulty);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvRatingCount = itemView.findViewById(R.id.tvRatingCount);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    // Callback interface for Edit/Delete
    public interface OnTournamentActionListener {
        void onEdit(TournamentModel model);
        void onDelete(TournamentModel model);
    }
}
