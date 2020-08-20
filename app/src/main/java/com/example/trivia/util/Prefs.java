package com.example.trivia.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
    private SharedPreferences preferences;

    public Prefs(Activity activity) {
        this.preferences = activity.getPreferences(Context.MODE_PRIVATE);
    }

    public void saveHighScore(int score) {
        // Provides the last saved high score
        int lastScore = preferences.getInt("high_score", 0);

        if (score > lastScore) {
            // We have a new high score and we save it
            preferences.edit().putInt("high_score", score).apply();
        }
    }

    public void resetHighScore(int score) {
        // Sets the high score equal to the current score, since the current
        // score is now the highest score.
        preferences.edit().putInt("high_score", score).apply();
    }

    public int getHighScore() {
        // retrieves the high score
        return preferences.getInt("high_score", 0);
    }

    public void setState(int index) {
        // saves the current state of the app (the current question)
        preferences.edit().putInt("index_state", index).apply();
    }

    public int getState() {
        // retrieves the saved state of the app
        return preferences.getInt("index_state", 0);
    }
}
