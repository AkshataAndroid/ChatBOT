package com.unfyd.unfydChatBot.preferences;

import android.content.Context;
import android.content.SharedPreferences;

public class UserPreferences {

    public static final String PREF_NAME_MAIN = "chat";

    public static void setPreferences(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME_MAIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putString(key, value);
        prefsEditor.apply();
    }

    public static String getPreferences(Context context, String key) {
        String LP = null;
        SharedPreferences appSharedPrefs = context.getSharedPreferences(PREF_NAME_MAIN, Context.MODE_PRIVATE);
        if (appSharedPrefs != null) {
            return appSharedPrefs.getString(key, LP);
        }
        return null;
    }
}
