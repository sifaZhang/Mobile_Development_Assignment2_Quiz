package com.group1.quiz.utils;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.group1.quiz.R;
import com.group1.quiz.admin.AdminDashboardActivity;
import com.group1.quiz.auth.LoginActivity;
import com.group1.quiz.auth.ProfileActivity;
import com.group1.quiz.common.AppConstants;
import com.group1.quiz.models.Users;
import com.group1.quiz.player.PlayerDashboardActivity;

public abstract class BaseActivity extends AppCompatActivity {

    protected ImageView imgHeaderLogo;
    protected TextView tvHeaderTitle, tvHeaderUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        setupHeader();

        View root = findViewById(android.R.id.content);
        root.setBackgroundColor(Color.parseColor(AppConstants.BK_COLOR));
    }

    protected void setupHeader() {
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        imgHeaderLogo = findViewById(R.id.imgHeaderLogo);
        tvHeaderUsername = findViewById(R.id.tvHeaderUsername);

        if (tvHeaderTitle == null || imgHeaderLogo == null || tvHeaderUsername == null) {
            return;
        }

        setupLogoClicke();
        setupUsername();
        setupUsernameClick();
    }

    private void setupUsernameClick() {
        tvHeaderUsername.setOnClickListener(v -> {
            UserPrefs prefs = new UserPrefs(this);
            if (prefs.isLoggedIn()) {
                showAvatarMenu(v);
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        });
    }

    private void showAvatarMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenuInflater().inflate(R.menu.user_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            } else if (id == R.id.menu_logout) {
                UserPrefs prefs = new UserPrefs(this);
                prefs.clear();
                UserManager.getInstance().clear();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

        popup.show();
    }

    protected void setupUsername() {
        Users user = UserManager.getInstance().getUser();
        if (user != null) {
            tvHeaderUsername.setText("Hi," + user.getName());
        }
    }

    private void setupLogoClicke() {
        imgHeaderLogo.setOnClickListener(v -> {
            Users user = UserManager.getInstance().getUser();
            if (user != null && user.getRole() != null) {
                goToDashboard(user.getRole());
            }
        });
    }

    protected void goToDashboard(String role) {
        switch (role) {
            case AppConstants.Role.PLAYER:
                startActivity(new Intent(this, PlayerDashboardActivity.class));
                finish();
                break;
            case AppConstants.Role.ADMIN:
                startActivity(new Intent(this, AdminDashboardActivity.class));
                finish();
                break;
            default:
                Toast.makeText(this, "Unknown role: " + role, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    protected void setHeaderTitle(String title) {
        if (tvHeaderTitle != null) {
            tvHeaderTitle.setText(title);
        }
    }
}
