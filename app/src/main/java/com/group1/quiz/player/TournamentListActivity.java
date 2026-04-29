package com.group1.quiz.player;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.group1.quiz.R;
import com.group1.quiz.common.AppConstants;
import com.group1.quiz.models.TournamentModel;
import com.group1.quiz.utils.BaseActivity;
import com.group1.quiz.utils.FirebaseHelper_Tournaments;
import com.group1.quiz.utils.TournamentPlayerAdapter;
import com.group1.quiz.utils.UserManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class TournamentListActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private TournamentPlayerAdapter adapter;
    private ArrayList<TournamentModel> tournamentList = new ArrayList<>();

    private String filterType;
    private String currentUserId;

    private FirebaseHelper_Tournaments firebaseHelper = new FirebaseHelper_Tournaments();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tournament_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setHeaderTitle("Tournament List");

        currentUserId = UserManager.getInstance().getUser().getUid();
        filterType = getIntent().getStringExtra(AppConstants.FilterType.FILTER_TYPE);

        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(filterType.toUpperCase() + " TOURNAMENTS");

        recyclerView = findViewById(R.id.recyclerViewTournaments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TournamentPlayerAdapter(tournamentList, this::openDetail);
        recyclerView.setAdapter(adapter);

        loadTournaments();

    }

    private void loadTournaments() {
        firebaseHelper.getAllTournaments(new FirebaseHelper_Tournaments.OnTournamentListListener() {
            @Override
            public void onReceived(ArrayList<TournamentModel> list) {
                tournamentList.clear();

                long now = System.currentTimeMillis();

                for (TournamentModel t : list) {
                    if (matchesFilter(t, now)) {
                        tournamentList.add(t);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    private boolean matchesFilter(TournamentModel t, long now) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        long start = 0;
        long end = 0;

        try {
            start = sdf.parse(t.startDate).getTime();
            end = sdf.parse(t.endDate).getTime();
        } catch (Exception e) {
            return false;
        }

        switch (filterType) {
            case AppConstants.FilterType.ONGOING:
                return now >= start && now <= end;
            case AppConstants.FilterType.UPCOMING:
                return start > now;
            case AppConstants.FilterType.PAST:
                return end < now;
            case AppConstants.FilterType.PARTICIPATED:
                return t.participants != null &&
                        t.participants.containsKey(currentUserId);
            default:
                return false;
        }
    }


    private void openDetail(TournamentModel t) {
        Intent intent = new Intent(this, TournamentDetailActivity.class);
        intent.putExtra(AppConstants.FilterType.TOURNAMENT_ID, t.tournamentId);
        startActivity(intent);
    }
}