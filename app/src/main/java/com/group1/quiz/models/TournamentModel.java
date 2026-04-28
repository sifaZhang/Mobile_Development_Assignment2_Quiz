package com.group1.quiz.models;

import java.util.List;

public class TournamentModel {
    public String tournamentId;

    public String name;
    public String category;
    public String difficulty;
    public String startDate;
    public String endDate;
    public double rating;
    public List<QuestionModel> questions;

    public TournamentModel() {}
}
