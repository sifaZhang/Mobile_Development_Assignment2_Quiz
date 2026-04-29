package com.group1.quiz.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TournamentModel {
    public String tournamentId;

    public String name;
    public String category;
    public String difficulty;
    public String startDate;
    public String endDate;
    public double rating;
    public int ratingCount = 0;
    public List<QuestionModel> questions;
    public Map<String, Boolean> participants = new HashMap<>();

    public TournamentModel() {}
}
