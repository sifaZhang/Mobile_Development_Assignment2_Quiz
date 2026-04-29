package com.group1.quiz.player;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.group1.quiz.R;
import com.group1.quiz.common.AppConstants;
import com.group1.quiz.models.TournamentModel;
import com.group1.quiz.utils.BaseActivity;
import com.group1.quiz.utils.FirebaseHelper_Tournaments;
import com.group1.quiz.utils.UserManager;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TournamentDetailActivity extends BaseActivity {

    private TextView tvName, tvCategory, tvDifficulty, tvDates, tvRatingCount, tvCount;
    private RatingBar ratingBar;
    private Button btnAction;

    private String tournamentId;
    private String currentUserId;

    private FirebaseHelper_Tournaments dbHelper = new FirebaseHelper_Tournaments();
    private TournamentModel tournament;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tournament_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setHeaderTitle("Tournament Detail");

        tournamentId = getIntent().getStringExtra("tournamentId");
        currentUserId = UserManager.getInstance().getUser().getUid();

        initViews();
        loadTournament();
    }

    private void initViews() {
        tvName = findViewById(R.id.tvName);
        tvCategory = findViewById(R.id.tvCategory);
        tvDifficulty = findViewById(R.id.tvDifficulty);
        tvDates = findViewById(R.id.tvDates);
        tvRatingCount = findViewById(R.id.tvRatingCount);
        tvCount = findViewById(R.id.tvCount);

        ratingBar = findViewById(R.id.ratingBar);
        ratingBar.setIsIndicator(true);

        btnAction = findViewById(R.id.btnJoin);
    }

    private void loadTournament() {
        dbHelper.getTournamentById(tournamentId, new FirebaseHelper_Tournaments.OnTournamentLoadedListener() {
            @Override
            public void onLoaded(TournamentModel model) {
                tournament = model;
                updateUI();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(TournamentDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void updateUI() {
        tvName.setText(tournament.name);
        tvCategory.setText("Category: " + tournament.category);
        tvDifficulty.setText("Difficulty: " + tournament.difficulty);
        tvDates.setText("Date: " + tournament.startDate + " - " + tournament.endDate);

        // Show question count
        if (tournament.questions != null) {
            tvCount.setText("Question Count: " + tournament.questions.size());
        } else {
            tvCount.setText("Question Count: 0");
        }

        // Rating
        ratingBar.setRating((float) tournament.rating);
        tvRatingCount.setText("Rating Count: " + tournament.ratingCount);

        // Determine button state
        long now = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        long start = 0;
        long end = 0;
        try {
            start = sdf.parse(tournament.startDate).getTime();
            end = sdf.parse(tournament.endDate).getTime();
        } catch (Exception e) {
            btnAction.setText("Error");
            btnAction.setEnabled(false);
            return;
        }

        boolean ongoing = now >= start && now <= end;
        boolean ended = end < now;
        boolean upcoming = start > now;
        boolean joined = tournament.participants.containsKey(currentUserId);

        if (joined) {
            btnAction.setText("Joined Tournament");
            btnAction.setEnabled(false);
            return;
        }

        if (ended) {
            btnAction.setText("Tournament Ended");
            btnAction.setEnabled(false);
            return;
        }

        if (!ongoing) {
            btnAction.setText("Waiting for Start");
            btnAction.setEnabled(false);
            return;
        }

        // no Joined + ongoing → Start Quiz
        btnAction.setText("Start Quiz");
        btnAction.setEnabled(true);
        btnAction.setOnClickListener(v -> startQuiz());
    }

    private void startQuiz() {
         Intent intent = new Intent(this, QuizQuestionActivity.class);
         intent.putExtra(AppConstants.FilterType.TOURNAMENT_ID, tournamentId);
         startActivity(intent);
    }
}