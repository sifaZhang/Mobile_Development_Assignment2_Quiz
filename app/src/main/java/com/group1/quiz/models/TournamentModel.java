package com.group1.quiz.models;

import java.util.List;

public class TournamentModel {
    public String name;
    public int category;
    public String difficulty;
    public String startDate;
    public String endDate;
    public double rating;
    public List<QuestionModel> questions;

    public TournamentModel() {}
}
