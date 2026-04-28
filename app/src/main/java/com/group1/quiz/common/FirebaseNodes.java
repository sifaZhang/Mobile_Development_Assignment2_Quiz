package com.group1.quiz.common;

public class FirebaseNodes {

    public static final double FEATURED_DISCOUNT = 20;
    public static final String USERS = "users";
    public static class UserFields {
        public static final String UID = "uid";
        public static final String FULLNAME = "name";
        public static final String EMAIL = "email";
        public static final String ROLE = "role";
    }

    public static final String TOURNAMENTS = "tournaments";

    public static class TournamentFields {
        public static final String TOURNAMENT_ID = "tournamentId";
        public static final String NAME = "name";
        public static final String CATEGORY = "category";
        public static final String DIFFICULTY = "difficulty";
        public static final String START_DATE = "startDate";
        public static final String END_DATE = "endDate";
        public static final String RATING = "rating";
        public static final String QUESTIONS = "questions";
    }

    public static class QuestionFields {
        public static final String TYPE = "type"; // multiple / boolean
        public static final String QUESTION = "question";
        public static final String CORRECT_ANSWER = "correct_answer";
        public static final String INCORRECT_ANSWERS = "incorrect_answers";
    }

    public static class QuestionType {
        public static final String SINGLE = "single";
        public static final String MULTIPLE = "multiple";
        public static final String BOOLEAN = "boolean";   // True/False
    }


}
