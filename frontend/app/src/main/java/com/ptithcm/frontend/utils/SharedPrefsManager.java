package com.ptithcm.frontend.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.ptithcm.frontend.network.dto.UserProfileDto;

import java.util.HashSet;
import java.util.Set;

public class SharedPrefsManager {

    private static final String PREF_NAME = "ECommercePrefs";
    private static final String KEY_TOKEN = "access_token";
    private static final String KEY_USER = "current_user";
    private static final String KEY_EMAIL_HISTORY = "email_history";

    private final SharedPreferences prefs;
    private final Gson gson;

    public SharedPrefsManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    // --- JWT Token ---
    public void saveToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public void clearToken() {
        prefs.edit().remove(KEY_TOKEN).apply();
    }

    public boolean hasToken() {
        return getToken() != null && !getToken().isEmpty();
    }

    // --- User Info ---
    public void saveUser(UserProfileDto user) {
        if (user != null) {
            String json = gson.toJson(user);
            prefs.edit().putString(KEY_USER, json).apply();
        }
    }

    public UserProfileDto getUser() {
        String json = prefs.getString(KEY_USER, null);
        if (json != null) {
            return gson.fromJson(json, UserProfileDto.class);
        }
        return null;
    }

    public void clearUser() {
        prefs.edit().remove(KEY_USER).apply();
    }

    // --- Email History ---
    public void saveEmailToHistory(String email) {
        Set<String> history = getEmailHistory();
        history.add(email);
        prefs.edit().putStringSet(KEY_EMAIL_HISTORY, history).apply();
    }

    public Set<String> getEmailHistory() {
        return new HashSet<>(prefs.getStringSet(KEY_EMAIL_HISTORY, new HashSet<>()));
    }
    
    // --- Clear All ---
    public void clearSession() {
        clearToken();
        clearUser();
    }
}
