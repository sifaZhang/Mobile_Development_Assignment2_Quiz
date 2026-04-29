package com.group1.quiz.common;

public class AppConstants {
    public static String BK_COLOR = "#E7EFE6";
    public static String NO_RATINGS = "(No ratings yet)";

    public static class PrefUser {
        public static final String PREF_NAME = "QuizPrefs";
        public static final String KEY_UID = "uid";
        public static final String KEY_LOGGED_IN = "isLoggedIn";
    }

    public static class Role {
        public static final String PLAYER = "player";
        public static final String ADMIN = "admin";

    }

    public static class FilterType {
        public static final String ONGOING = "ongoing";
        public static final String UPCOMING = "upcoming";
        public static final String PAST = "past";
        public static final String PARTICIPATED = "participated";

        public static final String FILTER_TYPE = "filterType"; // Intent key
        public static final String TOURNAMENT_ID = "tournamentId"; // Intent key
        public static final String QUESTIONS = "questions"; // Intent key
        public static final String SCORE = "score"; // Intent key
        public static final String TOTAL_QUESTION = "total"; // Intent key
    }
}
