package com.group1.quiz.player;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.group1.quiz.R;
import com.group1.quiz.common.AppConstants;
import com.group1.quiz.common.FirebaseNodes;
import com.group1.quiz.models.QuestionModel;
import com.group1.quiz.utils.BaseActivity;
import com.group1.quiz.utils.CustomQuiz;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizQuestionActivity extends BaseActivity {
    private TextView tvQuestionNumber, tvQuestion;
    private LinearLayout layoutMultiple, layoutBoolean;
    private RadioGroup rgMultiple, rgBoolean;
    private RadioButton rb1, rb2, rb3, rb4, rbTrue, rbFalse;
    private Button btnNext;

    private List<QuestionModel> questions;
    private int currentIndex = 0;
    private int score = 0;

    private String tournamentId;
    private boolean isCustom = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz_question);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setHeaderTitle("Questions");

        isCustom = getIntent().getBooleanExtra(AppConstants.FilterType.IS_CUSTOM, false);
        if (isCustom){
            tournamentId = null;
        } else {
            tournamentId = getIntent().getStringExtra(AppConstants.FilterType.TOURNAMENT_ID);
        }

        questions = CustomQuiz.getInstance().questions;
        if (questions == null || questions.isEmpty()) {
            Toast.makeText(this, "No questions found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        showQuestion();

        btnNext.setOnClickListener(v -> handleNext());
    }

    private void initViews() {
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);
        tvQuestion = findViewById(R.id.tvQuestion);

        layoutMultiple = findViewById(R.id.layoutMultiple);
        layoutBoolean = findViewById(R.id.layoutBoolean);

        rgMultiple = findViewById(R.id.rgMultiple);
        rgBoolean = findViewById(R.id.rgBoolean);

        rb1 = findViewById(R.id.rb1);
        rb2 = findViewById(R.id.rb2);
        rb3 = findViewById(R.id.rb3);
        rb4 = findViewById(R.id.rb4);

        rbTrue = findViewById(R.id.rbTrue);
        rbFalse = findViewById(R.id.rbFalse);

        btnNext = findViewById(R.id.btnNext);
    }

    private void showQuestion() {
        QuestionModel q = questions.get(currentIndex);

        tvQuestionNumber.setText("Question " + (currentIndex + 1) + "/" + questions.size());

        String questionText = Html.fromHtml(q.question, Html.FROM_HTML_MODE_LEGACY).toString();
        tvQuestion.setText(questionText);

        rgMultiple.clearCheck();
        rgBoolean.clearCheck();

        if (q.type.equals(FirebaseNodes.QuestionType.MULTIPLE)) {
            layoutMultiple.setVisibility(View.VISIBLE);
            layoutBoolean.setVisibility(View.GONE);

            List<String> options = new ArrayList<>();
            options.add(q.correct_answer);
            options.addAll(q.incorrect_answers);
            Collections.shuffle(options);

            rb1.setText(Html.fromHtml(options.get(0), Html.FROM_HTML_MODE_LEGACY).toString());
            rb2.setText(Html.fromHtml(options.get(1), Html.FROM_HTML_MODE_LEGACY).toString());
            rb3.setText(Html.fromHtml(options.get(2), Html.FROM_HTML_MODE_LEGACY).toString());
            rb4.setText(Html.fromHtml(options.get(3), Html.FROM_HTML_MODE_LEGACY).toString());
        } else { // boolean
            layoutMultiple.setVisibility(View.GONE);
            layoutBoolean.setVisibility(View.VISIBLE);

            rbTrue.setText(Html.fromHtml("True", Html.FROM_HTML_MODE_LEGACY).toString());
            rbFalse.setText(Html.fromHtml("False", Html.FROM_HTML_MODE_LEGACY).toString());
        }

        if (currentIndex == questions.size() - 1) {
            btnNext.setText("Submit");
        }
    }

    private void handleNext() {
        QuestionModel q = questions.get(currentIndex);
        String selectedAnswer = null;

        if (q.type.equals(FirebaseNodes.QuestionType.MULTIPLE)) {
            int id = rgMultiple.getCheckedRadioButtonId();
            if (id == -1) {
                Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
                return;
            }
            selectedAnswer = ((RadioButton) findViewById(id)).getText().toString();

        } else { // boolean
            int id = rgBoolean.getCheckedRadioButtonId();
            if (id == -1) {
                Toast.makeText(this, "Please select True or False", Toast.LENGTH_SHORT).show();
                return;
            }
            selectedAnswer = ((RadioButton) findViewById(id)).getText().toString();
        }

        String selected = Html.fromHtml(selectedAnswer, Html.FROM_HTML_MODE_LEGACY)
                .toString().trim().toLowerCase();
        String correct = Html.fromHtml(q.correct_answer, Html.FROM_HTML_MODE_LEGACY)
                .toString().trim().toLowerCase();
        if (selected.equals(correct)) {
            score++;
        }

        if (currentIndex == questions.size() - 1) {
            finishQuiz();
        } else {
            currentIndex++;
            showQuestion();
        }
    }

    private void finishQuiz() {
        Intent intent = new Intent(this, QuizResultActivity.class);
        intent.putExtra(AppConstants.FilterType.SCORE, score);
        intent.putExtra(AppConstants.FilterType.TOTAL_QUESTION, questions.size());
        intent.putExtra(AppConstants.FilterType.TOURNAMENT_ID, tournamentId);
        intent.putExtra(AppConstants.FilterType.IS_CUSTOM, isCustom);
        startActivity(intent);
        finish();
    }
}