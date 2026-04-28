package com.group1.quiz.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.group1.quiz.R;
import com.group1.quiz.utils.BaseActivity;

public class AdminDashboardActivity extends BaseActivity {

    Button btnCreate, btnView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvHeaderTitle.setText("Admin Dashboard");

        btnCreate = findViewById(R.id.btnCreate);
        btnView = findViewById(R.id.btnView);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click
                Intent intent = new Intent(AdminDashboardActivity.this, CreateTournamentActivity.class);
                startActivity(intent);
            }
        });

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click
                Intent intent = new Intent(AdminDashboardActivity.this, ViewAllTournamentActivity.class);
                startActivity(intent);
            }
        });
    }
}