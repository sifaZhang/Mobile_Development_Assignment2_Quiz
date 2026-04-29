package com.group1.quiz.models;

import java.io.Serializable;
import java.util.List;

public class QuestionModel implements Serializable {
    public String type;
    public String question;
    public String correct_answer;
    public List<String> incorrect_answers;

    public QuestionModel() {}
}
