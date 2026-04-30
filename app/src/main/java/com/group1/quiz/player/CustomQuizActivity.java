package com.group1.quiz.player;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.group1.quiz.admin.CreateTournamentActivity;
import com.group1.quiz.common.AppConstants;
import com.group1.quiz.models.CategoryModel;
import com.group1.quiz.models.QuestionModel;
import com.group1.quiz.models.TournamentModel;
import com.group1.quiz.utils.BaseActivity;
import com.group1.quiz.utils.CustomQuiz;
import com.group1.quiz.utils.FirebaseHelper_Tournaments;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomQuizActivity extends BaseActivity {
    private static CustomQuiz instance;
    private Spinner spCategory, spDifficulty;
    private EditText edtCount;
    private Button btnStart;
    private int selectedCategoryId = -1;
    private String selectedDifficulty = "easy";
    private String selectedCategory = "General Knowledge";
    private List<String> categoryNames = new ArrayList<>();
    private List<Integer> categoryIds = new ArrayList<>();
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_custom_quiz);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setHeaderTitle("Customize Quiz");

        spCategory = findViewById(R.id.spCategory);
        spDifficulty = findViewById(R.id.spDifficulty);
        btnStart = findViewById(R.id.btnStart);
        progressBar = findViewById(R.id.progressBar);
        edtCount = findViewById(R.id.edtCount);

        loadCategories();
        setupDifficultySpinner();
        btnStart.setOnClickListener(v -> startCustomQuiz());
    }

    private void loadCategories() {
        OpenTdbApi api = RetrofitClient.getApi();

        api.getCategories().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(CustomQuizActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<CategoryModel> list = response.body().trivia_categories;

                for (CategoryModel c : list) {
                    categoryNames.add(c.name);
                    categoryIds.add(c.id);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        CustomQuizActivity.this,
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
                Toast.makeText(CustomQuizActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void startCustomQuiz() {
        if (selectedCategoryId == -1) {
            Toast.makeText(this, "Please wait for categories to load", Toast.LENGTH_SHORT).show();
            return;
        }

        int count = Integer.parseInt(edtCount.getText().toString());
        if (count < 1 || count > 20) {
            Toast.makeText(this, "Count must be between 1 and 20", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        OpenTdbApi api = RetrofitClient.getApi();
        Call<OpenTdbResponse> call = api.getQuestions(
                count,
                selectedCategoryId,
                selectedDifficulty.toLowerCase(),
                null
        );

        call.enqueue(new Callback<OpenTdbResponse>() {
            @Override
            public void onResponse(Call<OpenTdbResponse> call, Response<OpenTdbResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(CustomQuizActivity.this, "API Error", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<QuestionModel> questions = response.body().results;
                int code = response.body().response_code;
                android.util.Log.d("API", "CategoryId=" + selectedCategoryId + ", Difficulty=" + selectedDifficulty + ", Category=" + selectedCategory + ", " + "response_code=" + code + ", questions=" + questions.size());

                if (questions.size() == 0){
                    Toast.makeText(CustomQuizActivity.this, "No questions found", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                CustomQuiz model = CustomQuiz.getInstance();
                model.clear();
                model.category = selectedCategory;
                model.difficulty = selectedDifficulty;
                model.questions = questions;

                progressBar.setVisibility(View.GONE);
                Toast.makeText(CustomQuizActivity.this, "Quiz Created!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(CustomQuizActivity.this, QuizQuestionActivity.class);
                intent.putExtra(AppConstants.FilterType.IS_CUSTOM, true);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<OpenTdbResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CustomQuizActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}