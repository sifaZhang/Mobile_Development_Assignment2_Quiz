package com.group1.quiz.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.group1.quiz.R;
import com.group1.quiz.models.Users;
import com.group1.quiz.utils.BaseActivity;
import com.group1.quiz.utils.FirebaseHelper_Users;
import com.group1.quiz.utils.UserManager;
import com.group1.quiz.utils.UserPrefs;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        UserPrefs prefs = new UserPrefs(this);
        String uid = prefs.getUid();

        if (!prefs.isLoggedIn() || uid == null) {
            goToLogin();
            return;
        }

        Users currentUser = UserManager.getInstance().getUser();

        if (currentUser == null) {
            FirebaseHelper_Users.loadUserInfo(uid, new FirebaseHelper_Users.UserCallback() {
                @Override
                public void onSuccess(Users user) {
                    UserManager.getInstance().setUser(user);
                    goToDashboard(user.getRole());
                }

                @Override
                public void onFailure(Exception e) {
                    goToLogin(); // 数据损坏或网络问题
                }
            });
        }
        else {
            goToDashboard(currentUser.getRole());
        }
    }

    private void goToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}