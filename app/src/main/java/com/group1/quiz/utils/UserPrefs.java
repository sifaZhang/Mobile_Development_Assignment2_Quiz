package com.group1.quiz.utils;


import android.content.Context;
import android.content.SharedPreferences;

import com.group1.quiz.common.AppConstants;

public class UserPrefs {
    private SharedPreferences prefs;

    public UserPrefs(Context context) {
        prefs = context.getSharedPreferences(AppConstants.PrefUser.PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveLogin(String uid) {
        prefs.edit()
                .putString(AppConstants.PrefUser.KEY_UID, uid)
                .putBoolean(AppConstants.PrefUser.KEY_LOGGED_IN, true)
                .apply();
    }

    public String getUid() {
        return prefs.getString(AppConstants.PrefUser.KEY_UID, null);
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(AppConstants.PrefUser.KEY_LOGGED_IN, false);
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}

