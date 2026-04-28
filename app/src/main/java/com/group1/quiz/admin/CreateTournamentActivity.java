package com.group1.quiz.admin;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.group1.quiz.R;
import com.group1.quiz.Retrofit.CategoryResponse;
import com.group1.quiz.Retrofit.OpenTdbApi;
import com.group1.quiz.Retrofit.OpenTdbResponse;
import com.group1.quiz.Retrofit.RetrofitClient;
import com.group1.quiz.models.CategoryModel;
import com.group1.quiz.models.QuestionModel;
import com.group1.quiz.models.TournamentModel;
import com.group1.quiz.utils.BaseActivity;
import com.group1.quiz.utils.FirebaseHelper_Tournaments;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateTournamentActivity extends BaseActivity {

    EditText etName;
    Spinner spCategory, spDifficulty;
    TextView tvStartDate, tvEndDate;
    RatingBar ratingBar;
    Button btnCreate;
    ProgressBar progressBar;

    List<String> categoryNames = new ArrayList<>();
    List<Integer> categoryIds = new ArrayList<>();

    int selectedCategoryId = -1;
    String selectedDifficulty = "easy";
    String selectedCategory = "General Knowledge";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_tournament);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etName = findViewById(R.id.etName);
        spCategory = findViewById(R.id.spCategory);
        spDifficulty = findViewById(R.id.spDifficulty);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        ratingBar = findViewById(R.id.ratingBar);
        btnCreate = findViewById(R.id.btnCreate);
        progressBar = findViewById(R.id.progressBar);

        loadCategories();
        setupDifficultySpinner();
        setupDatePickers();

        btnCreate.setOnClickListener(v -> {
            hideKeyboard();
            createTournament();
        });
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void loadCategories() {
        OpenTdbApi api = RetrofitClient.getApi();

        api.getCategories().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(CreateTournamentActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<CategoryModel> list = response.body().trivia_categories;

                for (CategoryModel c : list) {
                    categoryNames.add(c.name);
                    categoryIds.add(c.id);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        CreateTournamentActivity.this,
                        android.R.layout.simple_spinner_item,
                        categoryNames
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spCategory.setAdapter(adapter);

                spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedCategoryId = categoryIds.get(position);
                        selectedCategory = spCategory.getSelectedItem().toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                Toast.makeText(CreateTournamentActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDifficultySpinner() {
        ArrayAdapter<CharSequence> diffAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.quiz_difficulties,
                android.R.layout.simple_spinner_item
        );
        diffAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDifficulty.setAdapter(diffAdapter);

        spDifficulty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDifficulty = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupDatePickers() {
        tvStartDate.setOnClickListener(v -> showDatePicker(tvStartDate));
        tvEndDate.setOnClickListener(v -> showDatePicker(tvEndDate));
    }

    private void showDatePicker(TextView target) {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            String date = year + "-" + (month + 1) + "-" + day;
            target.setText(date);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void createTournament() {
        String name = etName.getText().toString().trim();
        String startDate = tvStartDate.getText().toString();
        String endDate = tvEndDate.getText().toString();
        double rating = ratingBar.getRating();

        if (name.isEmpty()) {
            etName.setError("Required");
            return;
        }
        if (startDate.equals("Select date")) {
            Toast.makeText(this, "Please select start date", Toast.LENGTH_SHORT).show();
            return;
        }
        if (endDate.equals("Select date")) {
            Toast.makeText(this, "Please select end date", Toast.LENGTH_SHORT).show();
            return;
        }
        if (rating == 0) {
            Toast.makeText(this, "Please select rating", Toast.LENGTH_SHORT).show();
            return;
        }
        if (startDate.compareTo(endDate) >= 0) {
            Toast.makeText(this, "End date must be after start date", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        OpenTdbApi api = RetrofitClient.getApi();
        Call<OpenTdbResponse> call = api.getQuestions(
                10,
                selectedCategoryId,
                selectedDifficulty,
                null
        );

        call.enqueue(new Callback<OpenTdbResponse>() {
            @Override
            public void onResponse(Call<OpenTdbResponse> call, Response<OpenTdbResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(CreateTournamentActivity.this, "API Error", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<QuestionModel> questions = response.body().results;

                TournamentModel model = new TournamentModel();
                model.name = name;
                model.category = selectedCategory;
                model.difficulty = selectedDifficulty;
                model.startDate = startDate;
                model.endDate = endDate;
                model.rating = rating;
                model.questions = questions;

                FirebaseHelper_Tournaments dbHelper = new FirebaseHelper_Tournaments();

                dbHelper.createTournament(model, new FirebaseHelper_Tournaments.OnTournamentCreatedListener() {
                    @Override
                    public void onSuccess(String id) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(CreateTournamentActivity.this, "Tournament Created!", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(String error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(CreateTournamentActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<OpenTdbResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CreateTournamentActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}