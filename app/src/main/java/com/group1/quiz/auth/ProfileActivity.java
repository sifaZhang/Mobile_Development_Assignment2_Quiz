package com.group1.quiz.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.group1.quiz.R;
import com.group1.quiz.common.FirebaseNodes;
import com.group1.quiz.models.Users;
import com.group1.quiz.utils.BaseActivity;
import com.group1.quiz.utils.FirebaseHelper_Users;
import com.group1.quiz.utils.UserManager;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends BaseActivity {

    private EditText etName, etEmail, etOldPassword, etNewPassword, etReNewPassword, etRole;
    private Button btnSave;
    private String strUserUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());
            Insets navigationBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars());

            int bottomPadding = Math.max(imeInsets.bottom, navigationBars.bottom);
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomPadding);

            // 键盘弹出时（ime bottom > 0），自动滚动到焦点 View
            if (imeInsets.bottom > 0) {
                View focusedView = getCurrentFocus();
                if (focusedView != null) {
                    ScrollView scrollView = findViewById(R.id.main);
                    scrollView.post(() -> {
                        // 计算 focusedView 相对于 ScrollView 的位置
                        int[] location = new int[2];
                        focusedView.getLocationInWindow(location);
                        int[] scrollLocation = new int[2];
                        scrollView.getLocationInWindow(scrollLocation);

                        int scrollTo = location[1] - scrollLocation[1]
                                + scrollView.getScrollY()
                                - scrollView.getHeight() / 2; // 滚到屏幕中间位置

                        scrollView.smoothScrollTo(0, scrollTo);
                    });
                }
            }

            return insets;
        });

        setHeaderTitle("Profile");

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etReNewPassword = findViewById(R.id.etReNewPassword);
        etRole = findViewById(R.id.etRole);
        btnSave = findViewById(R.id.btnSave);

        loadUserProfile();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserProfile();
            }
        });
    }

    private void saveUserProfile() {
        String fullName = etName.getText().toString().trim();
        String oldPassword = etOldPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String newConfirmPassword = etReNewPassword.getText().toString().trim();

        // basic check
        if (fullName.isEmpty()) {
            Toast.makeText(ProfileActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // password
        if (!newPassword.equals(newConfirmPassword)) {
            Toast.makeText(ProfileActivity.this, "New Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.isEmpty() && newPassword.equals(oldPassword)) {
            Toast.makeText(ProfileActivity.this, "New Passwords equal old password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.isEmpty() && oldPassword.isEmpty()) {
            Toast.makeText(ProfileActivity.this, "Please input old password", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. update to database
        Map<String, Object> updates = new HashMap<>();
        updates.put(FirebaseNodes.UserFields.FULLNAME, fullName);

        FirebaseHelper_Users.updateUser(strUserUID, updates, oldPassword, newPassword, new FirebaseHelper_Users.UpdateCallback() {
            @Override
            public void onSuccess() {
                Users user = UserManager.getInstance().getUser();
                if (user != null) {
                    user.setName(fullName);
                    UserManager.getInstance().setUser(user);
                    etOldPassword.setText("");
                    etNewPassword.setText("");
                    etReNewPassword.setText("");
                    setupUsername();
                    Toast.makeText(ProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "User is null", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Exception e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException ||
                        e instanceof IllegalArgumentException) {
                    Toast.makeText(ProfileActivity.this, "Old password is incorrect", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadUserProfile() {
        Users user = UserManager.getInstance().getUser();
        if(user == null || user.getUid().isEmpty()) {
            Toast.makeText(this, "User is null", Toast.LENGTH_SHORT).show();
        } else {
            etName.setText(user.getName());
            etEmail.setText(user.getEmail());
            etRole.setText(user.getRole());
            strUserUID = user.getUid();
        }
    }
}