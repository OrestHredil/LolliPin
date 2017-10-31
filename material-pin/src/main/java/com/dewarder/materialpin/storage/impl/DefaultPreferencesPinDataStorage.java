package com.dewarder.materialpin.storage.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dewarder.materialpin.storage.PinDataStorage;

public class DefaultPreferencesPinDataStorage implements PinDataStorage {

    private static final String ATTEMPTS_COUNT_PREFERENCE_KEY = "ATTEMPTS_COUNT_PREFERENCE_KEY";
    /**
     * The {@link android.content.SharedPreferences} key used to store the dynamically generated password salt
     */
    private static final String PASSWORD_SALT_PREFERENCE_KEY = "PASSWORD_SALT_PREFERENCE_KEY";
    /**
     * The {@link android.content.SharedPreferences} key used to store the password
     */
    private static final String PASSWORD_PREFERENCE_KEY = "PASSCODE";

    private final SharedPreferences mPreferences;

    public DefaultPreferencesPinDataStorage(Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public int readAttemptsCount() {
        return mPreferences.getInt(ATTEMPTS_COUNT_PREFERENCE_KEY, 0);
    }

    @Override
    public void writeAttemptsCount(int attempts) {
        mPreferences.edit()
                .putInt(ATTEMPTS_COUNT_PREFERENCE_KEY, attempts)
                .apply();
    }

    @Override
    @Nullable
    public String readSalt() {
        return mPreferences.getString(PASSWORD_SALT_PREFERENCE_KEY, null);
    }

    @Override
    public void writeSalt(@Nullable String salt) {
        mPreferences.edit()
                .putString(PASSWORD_SALT_PREFERENCE_KEY, salt)
                .apply();
    }

    @NonNull
    @Override
    public String readPasscode() {
        return mPreferences.getString(PASSWORD_PREFERENCE_KEY, "");
    }

    @Override
    public void writePin(@NonNull String passcode) {
        mPreferences.edit()
                .putString(PASSWORD_PREFERENCE_KEY, passcode)
                .apply();
    }

    @Override
    public boolean hasPin() {
        return mPreferences.contains(PASSWORD_PREFERENCE_KEY);
    }

    @Override
    public void clearPin() {
        mPreferences.edit()
                .remove(PASSWORD_PREFERENCE_KEY)
                .apply();
    }

    @Override
    public void clear() {
        mPreferences.edit()
                .remove(PASSWORD_SALT_PREFERENCE_KEY)
                .remove(PASSWORD_PREFERENCE_KEY)
                .apply();
    }
}
