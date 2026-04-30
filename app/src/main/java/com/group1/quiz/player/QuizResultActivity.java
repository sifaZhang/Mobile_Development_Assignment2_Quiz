package com.group1.quiz.player;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.group1.quiz.R;
import com.group1.quiz.common.AppConstants;
import com.group1.quiz.utils.BaseActivity;
import com.group1.quiz.utils.FirebaseHelper_Tournaments;
import com.group1.quiz.utils.UserManager;

public class QuizResultActivity extends BaseActivity {
    private TextView tvScore, tvPercentage;
    private RatingBar ratingBar;
    private Button btnSubmitRating, btnBack;

    private int score, total;
    private String tournamentId;
    private String userId;
    private boolean isCustom = false;

    private FirebaseHelper_Tournaments dbHelper = new FirebaseHelper_Tournaments();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz_result);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setHeaderTitle("Result");

        score = getIntent().getIntExtra(AppConstants.FilterType.SCORE, 0);
        total = getIntent().getIntExtra(AppConstants.FilterType.TOTAL_QUESTION, 0);
        isCustom = getIntent().getBooleanExtra(AppConstants.FilterType.IS_CUSTOM, false);
        tournamentId = getIntent().getStringExtra(AppConstants.FilterType.TOURNAMENT_ID);
        userId = UserManager.getInstance().getUser().getUid();

        initViews();
        showResults();

        btnSubmitRating.setOnClickListener(v -> submitRating());
        btnBack.setOnClickListener(v -> goBack());
    }
    private void initViews() {
        tvScore = findViewById(R.id.tvScore);
        tvPercentage = findViewById(R.id.tvPercentage);
        ratingBar = findViewById(R.id.ratingBar);
        btnSubmitRating = findViewById(R.id.btnSubmitRating);
        btnBack = findViewById(R.id.btnBack);

        if (isCustom) {
            btnSubmitRating.setVisibility(View.GONE);
            ratingBar.setVisibility(View.GONE);

        } else {
            btnSubmitRating.setVisibility(View.VISIBLE);
        }
    }

    private void showResults() {
        tvScore.setText("Your Score: " + score + "/" + total);

        int percent = (int) (((double) score / total) * 100);
        tvPercentage.setText("Percentage: " + percent + "%");
    }

    private void submitRating() {
        float rating = ratingBar.getRating();
        if (rating == 0) {
            Toast.makeText(this, "Please select a rating", Toast.LENGTH_SHORT).show();
            return;
        }

        dbHelper.updateRating(tournamentId, userId, rating, new FirebaseHelper_Tournaments.OnRatingUpdatedListener() {
            @Override
            public void onSuccess(double newAvg, int newCount) {
                Toast.makeText(QuizResultActivity.this,
                        "Rating submitted! New Avg: " + newAvg,
                        Toast.LENGTH_SHORT).show();

                btnSubmitRating.setEnabled(false);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(QuizResultActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goBack() {
        Intent intent = new Intent(this, PlayerDashboardActivity.class);
        startActivity(intent);
        finish();
    }
}