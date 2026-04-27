package com.group1.quiz.utils;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.group1.quiz.common.FirebaseNodes;
import com.group1.quiz.models.Users;

import java.util.Map;

public class FirebaseHelper_Users {

    private static final DatabaseReference USERS_REF =
            FirebaseDatabase.getInstance().getReference(FirebaseNodes.USERS);

    // load users
    public interface UserCallback {
        void onSuccess(Users user);
        void onFailure(Exception e);
    }

    public static void loadUserInfo(String uid, UserCallback callback) {
        USERS_REF.child(uid).get().addOnSuccessListener(snapshot -> {
            Users user = snapshot.getValue(Users.class);
            callback.onSuccess(user);
        }).addOnFailureListener(callback::onFailure);
    }





    // -----------------------------
    // Register user
    // -----------------------------
    // write users
    public interface RegisterCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public static void registerUser(Users user, RegisterCallback callback) {
        if (user == null || user.getUid() == null) {
            callback.onFailure(new Exception("User or UID is null"));
            return;
        }

        USERS_REF.child(user.getUid())
                .setValue(user)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    // -----------------------------
    // Register user (Auth + DB)
    // -----------------------------
    public static void registerUserWithAuth(
            String email,
            String password,
            Users user,
            RegisterCallback callback
    ) {
        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {

                    String uid = authResult.getUser().getUid();
                    user.setUid(uid); // 确保 UID 写入 Users 对象

                    USERS_REF.child(uid)
                            .setValue(user)
                            .addOnSuccessListener(aVoid -> callback.onSuccess())
                            .addOnFailureListener(callback::onFailure);

                })
                .addOnFailureListener(callback::onFailure);
    }






// -----------------------------
    // Update user info (Realtime DB)
    // -----------------------------
    public interface UpdateCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public static void updateUserProfile(String uid, Map<String, Object> updates, UpdateCallback callback) {
        USERS_REF.child(uid)
                .updateChildren(updates)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    // -----------------------------
    // Combined update (DB + password)
    // -----------------------------
    public static void updateUser(
            String uid,
            Map<String, Object> updates,
            String oldPassword,
            String newPassword,
            UpdateCallback callback
    ) {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return;
        }

        // Step 1: update database
        updateUserProfile(uid, updates, new UpdateCallback() {
            @Override
            public void onSuccess() {

                // Step 2: update password (if provided)
                if (newPassword != null && !newPassword.isEmpty()) {
                    AuthCredential credential = EmailAuthProvider.getCredential(
                            currentUser.getEmail(),
                            oldPassword
                    );

                    currentUser.reauthenticate(credential)
                            .addOnSuccessListener(aVoid -> {
                                currentUser.updatePassword(newPassword)
                                        .addOnSuccessListener(v -> {callback.onSuccess();})
                                        .addOnFailureListener(e -> {callback.onFailure(e);});
                            })
                            .addOnFailureListener(e -> {callback.onFailure(e);});
                } else {
                    callback.onSuccess();
                }
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }
}
