package com.group1.quiz.admin;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.group1.quiz.R;
import com.group1.quiz.models.TournamentModel;
import com.group1.quiz.utils.BaseActivity;
import com.group1.quiz.utils.FirebaseHelper_Tournaments;
import com.group1.quiz.utils.TournamentAdapter;

import java.util.ArrayList;
import java.util.Calendar;

public class ViewAllTournamentActivity extends BaseActivity {
    private RecyclerView recyclerTournaments;
    private TournamentAdapter adapter;
    private ArrayList<TournamentModel> list = new ArrayList<>();
    private FirebaseHelper_Tournaments firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_all_tournament);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerTournaments = findViewById(R.id.recyclerTournaments);
        recyclerTournaments.setLayoutManager(new LinearLayoutManager(this));

        firebaseHelper = new FirebaseHelper_Tournaments();

        adapter = new TournamentAdapter(list, new TournamentAdapter.OnTournamentActionListener() {
            @Override
            public void onEdit(TournamentModel model) {
                showEditDialog(model);
            }

            @Override
            public void onDelete(TournamentModel model) {
                confirmDelete(model);
            }
        });

        recyclerTournaments.setAdapter(adapter);

        loadTournaments();
    }

    // ---------------------------------------------------------
    // read all tournaments
    // ---------------------------------------------------------
    private void loadTournaments() {
        firebaseHelper.getAllTournaments(new FirebaseHelper_Tournaments.OnTournamentListListener() {
            @Override
            public void onReceived(ArrayList<TournamentModel> tournaments) {
                list.clear();
                list.addAll(tournaments);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ViewAllTournamentActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ---------------------------------------------------------
    // EDIT DIALOG
    // ---------------------------------------------------------
    private void showEditDialog(TournamentModel model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_tournament, null);
        builder.setView(view);

        EditText etName = view.findViewById(R.id.etName);
        EditText etStart = view.findViewById(R.id.etStartDate);
        EditText etEnd = view.findViewById(R.id.etEndDate);

        // 填入原数据
        etName.setText(model.name);
        etStart.setText(model.startDate);
        etEnd.setText(model.endDate);

        // 日期选择器
        etStart.setOnClickListener(v -> pickDate(etStart));
        etEnd.setOnClickListener(v -> pickDate(etEnd));

        builder.setPositiveButton("Save", (dialog, which) -> {
            model.name = etName.getText().toString().trim();
            model.startDate = etStart.getText().toString();
            model.endDate = etEnd.getText().toString();

            if (model.name.isEmpty()) {
                Toast.makeText(this, "Please enter tournament name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (model.startDate.equals("Select date")) {
                Toast.makeText(this, "Please select start date", Toast.LENGTH_SHORT).show();
                return;
            }
            if (model.endDate.equals("Select date")) {
                Toast.makeText(this, "Please select end date", Toast.LENGTH_SHORT).show();
                return;
            }
            if (model.startDate.compareTo(model.endDate) >= 0) {
                Toast.makeText(this, "End date must be after start date", Toast.LENGTH_SHORT).show();
                return;
            }

            firebaseHelper.updateTournament(model.tournamentId, model, new FirebaseHelper_Tournaments.OnTournamentUpdatedListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(ViewAllTournamentActivity.this, "Updated!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(ViewAllTournamentActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }


    private void pickDate(EditText target) {
        Calendar c = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (DatePicker view, int year, int month, int day) -> {
                    String date = year + "-" + (month + 1) + "-" + day;
                    target.setText(date);
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    // ---------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------
    private void confirmDelete(TournamentModel model) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Tournament")
                .setMessage("Are you sure you want to delete \"" + model.name + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> deleteTournament(model))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteTournament(TournamentModel model) {
        firebaseHelper.deleteTournament(model.tournamentId, new FirebaseHelper_Tournaments.OnTournamentDeletedListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(ViewAllTournamentActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(ViewAllTournamentActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}