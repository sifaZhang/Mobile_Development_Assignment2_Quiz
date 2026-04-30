package com.group1.quiz.utils;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group1.quiz.common.AppConstants;
import com.group1.quiz.common.FirebaseNodes;
import com.group1.quiz.models.TournamentModel;

import java.util.ArrayList;
import java.util.HashMap;

public class FirebaseHelper_Tournaments {

    private final DatabaseReference tournamentsRef;

    public FirebaseHelper_Tournaments() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        tournamentsRef = db.getReference(FirebaseNodes.TOURNAMENTS);
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
                        model.tournamentId = ds.getKey();

                        // 确保 participants 不为 null
                        if (model.participants == null) {
                            model.participants = new HashMap<>();
                        }

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
    // READ ONE
    // ---------------------------------------------------------
    public void getTournamentById(String id, OnTournamentLoadedListener listener) {
        tournamentsRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                TournamentModel model = snapshot.getValue(TournamentModel.class);
                if (model != null) {
                    model.tournamentId = snapshot.getKey();

                    // 确保 participants 不为 null
                    if (model.participants == null) {
                        model.participants = new HashMap<>();
                    }

                    listener.onLoaded(model);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onError(error.getMessage());
            }
        });
    }

    public interface OnTournamentLoadedListener {
        void onLoaded(TournamentModel model);
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

    // ---------------------------------------------------------
    // UPDATE RATING
    // ---------------------------------------------------------
    public void updateRating(String tournamentId, String userId, double newRating, OnRatingUpdatedListener listener) {
        tournamentsRef.child(tournamentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                TournamentModel model = snapshot.getValue(TournamentModel.class);

                if (model == null) {
                    listener.onFailure("Tournament not found");
                    return;
                }

                // 计算新的平均分
                double oldRating = model.rating;
                int oldCount = model.ratingCount;

                double updatedRating = (oldRating * oldCount + newRating) / (oldCount + 1);

                model.rating = updatedRating;
                model.ratingCount = oldCount + 1;

                // 写回数据库
                tournamentsRef.child(tournamentId).setValue(model)
                        .addOnSuccessListener(unused -> {
                            // 同步记录参与者
                            addParticipant(tournamentId, userId);

                            listener.onSuccess(updatedRating, model.ratingCount);
                        })
                        .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onFailure(error.getMessage());
            }
        });
    }

    public interface OnRatingUpdatedListener {
        void onSuccess(double newAvg, int newCount);
        void onFailure(String error);
    }

    public void addParticipant(String tournamentId, String userId) {
        tournamentsRef.child(tournamentId)
                .child("participants")
                .child(userId)
                .setValue(true);
    }
}
