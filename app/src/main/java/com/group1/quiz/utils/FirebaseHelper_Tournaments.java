package com.group1.quiz.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.group1.quiz.models.TournamentModel;

public class FirebaseHelper_Tournaments {
    private final DatabaseReference tournamentsRef;

    public FirebaseHelper_Tournaments() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        tournamentsRef = db.getReference("tournaments");
    }

    // 创建 Tournament
    public void createTournament(TournamentModel model, OnTournamentCreatedListener listener) {
        String id = tournamentsRef.push().getKey(); // 自动生成 ID

        if (id == null) {
            listener.onFailure("Failed to generate ID");
            return;
        }

        tournamentsRef.child(id).setValue(model)
                .addOnSuccessListener(unused -> listener.onSuccess(id))
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    // 回调接口
    public interface OnTournamentCreatedListener {
        void onSuccess(String id);
        void onFailure(String error);
    }
}
