// UserManager.java
package com.example.travelplanner;

import android.content.Context;
import android.content.SharedPreferences;

public class UserManager {
    private static final String PREFS_NAME = "users";
    private SharedPreferences prefs;

    public UserManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveUser(String email, String username, String password) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(username + "_email", email);
        editor.putString(username + "_password", password);
        editor.apply();
    }

    public boolean userExists(String username) {
        return prefs.contains(username + "_password");
    }

    public boolean isPasswordCorrect(String username, String password) {
        return password.equals(prefs.getString(username + "_password", ""));
    }

    public boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}

