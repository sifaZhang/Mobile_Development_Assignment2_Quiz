package com.group1.quiz.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group1.quiz.R;
import com.group1.quiz.models.TournamentModel;

import java.util.ArrayList;

public class TournamentPlayerAdapter extends RecyclerView.Adapter<TournamentPlayerAdapter.ViewHolder> {

    private ArrayList<TournamentModel> list;
    private OnTournamentClickListener listener;

    public TournamentPlayerAdapter(ArrayList<TournamentModel> list, OnTournamentClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tournament_player, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TournamentModel t = list.get(position);

        holder.tvName.setText(t.name);
        holder.tvCategory.setText("Category: " + t.category);
        holder.tvDifficulty.setText("Difficulty: " + t.difficulty);
        holder.tvDate.setText("Date: " + t.startDate + " - " + t.endDate);

        // 显示平均评分
        holder.ratingBar.setRating((float) t.rating);

        // 显示评分人数
        if (t.ratingCount == 0) {
            holder.tvRatingCount.setText("Rating: (No ratings yet)");
        } else {
            holder.tvRatingCount.setText("Rating: (" + t.ratingCount + " ratings)");
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(t);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvCategory, tvDifficulty, tvDate, tvRatingCount;
        RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDifficulty = itemView.findViewById(R.id.tvDifficulty);
            tvDate = itemView.findViewById(R.id.tvDate);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            tvRatingCount = itemView.findViewById(R.id.tvRatingCount);
        }
    }

    public interface OnTournamentClickListener {
        void onClick(TournamentModel model);
    }
}

