package com.group1.quiz.player;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;
import com.group1.quiz.R;
import com.group1.quiz.common.AppConstants;
import com.group1.quiz.models.Users;
import com.group1.quiz.utils.BaseActivity;
import com.group1.quiz.utils.UserManager;

public class PlayerDashboardActivity extends BaseActivity {

    MaterialCardView cardOngoing, cardUpcoming, cardPast, cardParticipated, cardCustomQuiz;
    TextView tvWelcome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_player_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setHeaderTitle("Player Dashboard");

        cardOngoing = findViewById(R.id.cardOngoing);
        cardUpcoming = findViewById(R.id.cardUpcoming);
        cardPast = findViewById(R.id.cardPast);
        cardParticipated = findViewById(R.id.cardParticipated);
        cardCustomQuiz = findViewById(R.id.cardCustomQuiz);
        tvWelcome = findViewById(R.id.tvWelcome);

        Users user = UserManager.getInstance().getUser();
        tvWelcome.setText((user != null ? user.getName() : "") + ", Welcome Back!" );

        cardOngoing.setOnClickListener(v -> openList(AppConstants.FilterType.ONGOING));
        cardUpcoming.setOnClickListener(v -> openList(AppConstants.FilterType.UPCOMING));
        cardPast.setOnClickListener(v -> openList(AppConstants.FilterType.PAST));
        cardParticipated.setOnClickListener(v -> openList(AppConstants.FilterType.PARTICIPATED));

        cardCustomQuiz.setOnClickListener(v ->
                startActivity(new Intent(PlayerDashboardActivity.this, CustomQuizActivity.class))
        );
    }

    private void openList(String type) {
        Intent intent = new Intent(PlayerDashboardActivity.this, TournamentListActivity.class);
        intent.putExtra(AppConstants.FilterType.FILTER_TYPE, type);
        startActivity(intent);
    }
}