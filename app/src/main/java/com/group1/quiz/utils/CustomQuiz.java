package com.group1.quiz.utils;

import com.group1.quiz.models.QuestionModel;

import java.util.List;

public class CustomQuiz {
    private static CustomQuiz instance;

    public List<QuestionModel> questions;
    public String difficulty;
    public String category;

    private CustomQuiz() {}

    public static CustomQuiz getInstance() {
        if (instance == null) {
            instance = new CustomQuiz();
        }
        return instance;
    }

    public void clear() {
        questions = null;
        difficulty = null;
        category = null;
    }
}
