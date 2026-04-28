package com.group1.quiz.utils;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group1.quiz.models.TournamentModel;

import java.util.ArrayList;

public class FirebaseHelper_Tournaments {

    private final DatabaseReference tournamentsRef;

    public FirebaseHelper_Tournaments() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        tournamentsRef = db.getReference("tournaments");
    }

    // ---------------------------------------------------------
    // CREATE
    // ---------------------------------------------------------
    public void createTournament(TournamentModel model, OnTournamentCreatedListener listener) {
        String id = tournamentsRef.push().getKey();

        if (id == null) {
            listener.onFailure("Failed to generate ID");
            return;
        }

        tournamentsRef.child(id).setValue(model)
                .addOnSuccessListener(unused -> listener.onSuccess(id))
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public interface OnTournamentCreatedListener {
        void onSuccess(String id);
        void onFailure(String error);
    }

    // ---------------------------------------------------------
    // READ ALL
    // ---------------------------------------------------------
    public void getAllTournaments(OnTournamentListListener listener) {
        tournamentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<TournamentModel> list = new ArrayList<>();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    TournamentModel model = ds.getValue(TournamentModel.class);
                    if (model != null) {
                        model.tournamentId = ds.getKey(); // 保存 Firebase ID
                        list.add(model);
                    }
                }

                listener.onReceived(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onError(error.getMessage());
            }
        });
    }

    public interface OnTournamentListListener {
        void onReceived(ArrayList<TournamentModel> list);
        void onError(String error);
    }

    // ---------------------------------------------------------
    // UPDATE
    // ---------------------------------------------------------
    public void updateTournament(String id, TournamentModel model, OnTournamentUpdatedListener listener) {
        tournamentsRef.child(id).setValue(model)
                .addOnSuccessListener(unused -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public interface OnTournamentUpdatedListener {
        void onSuccess();
        void onFailure(String error);
    }

    // ---------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------
    public void deleteTournament(String id, OnTournamentDeletedListener listener) {
        tournamentsRef.child(id).removeValue()
                .addOnSuccessListener(unused -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public interface OnTournamentDeletedListener {
        void onSuccess();
        void onFailure(String error);
    }
}
