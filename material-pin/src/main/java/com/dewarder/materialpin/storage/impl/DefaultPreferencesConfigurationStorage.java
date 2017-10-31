package com.dewarder.materialpin.storage.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dewarder.materialpin.DefaultConstants;
import com.dewarder.materialpin.storage.ConfigurationStorage;

public class DefaultPreferencesConfigurationStorage implements ConfigurationStorage {

    private static final String TIMEOUT_MILLIS_PREFERENCE_KEY = "TIMEOUT_MILLIS_PREFERENCE_KEY";
    private static final String LOGO_ID_PREFERENCE_KEY = "LOGO_ID_PREFERENCE_KEY";
    private static final String SHOW_FORGOT_PREFERENCE_KEY = "SHOW_FORGOT_PREFERENCE_KEY";
    private static final String LAST_ACTIVE_MILLIS_PREFERENCE_KEY = "LAST_ACTIVE_MILLIS";
    private static final String ONLY_BACKGROUND_TIMEOUT_PREFERENCE_KEY = "ONLY_BACKGROUND_TIMEOUT_PREFERENCE_KEY";
    private static final String PIN_CHALLENGE_CANCELLED_PREFERENCE_KEY = "PIN_CHALLENGE_CANCELLED_PREFERENCE_KEY";
    private static final String FINGERPRINT_AUTH_ENABLED_PREFERENCE_KEY = "FINGERPRINT_AUTH_ENABLED_PREFERENCE_KEY";

    private final SharedPreferences mPreferences;

    public DefaultPreferencesConfigurationStorage(Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public long readTimeout() {
        return mPreferences.getLong(TIMEOUT_MILLIS_PREFERENCE_KEY, DefaultConstants.DEFAULT_TIMEOUT);
    }

    @Override
    public void writeTimeout(long timeout) {
        mPreferences.edit()
                .putLong(TIMEOUT_MILLIS_PREFERENCE_KEY, timeout)
                .apply();
    }

    @Override
    public int readLogoId() {
        return mPreferences.getInt(LOGO_ID_PREFERENCE_KEY, DefaultConstants.LOGO_ID_NONE);
    }

    @Override
    public void writeLogoId(int logoId) {
        mPreferences.edit()
                .putInt(LOGO_ID_PREFERENCE_KEY, logoId)
                .apply();
    }

    @Override
    public boolean readShouldShowForgot() {
        return mPreferences.getBoolean(SHOW_FORGOT_PREFERENCE_KEY, DefaultConstants.DEFAULT_SHOW_FORGOT);
    }

    @Override
    public void writeShouldShowForgot(boolean showForgot) {
        mPreferences.edit()
                .putBoolean(SHOW_FORGOT_PREFERENCE_KEY, showForgot)
                .apply();
    }

    @Override
    public boolean readPinChallengeCanceled() {
        return mPreferences.getBoolean(
                PIN_CHALLENGE_CANCELLED_PREFERENCE_KEY, DefaultConstants.DEFAULT_PIN_CHALLENGE_CANCELED);
    }

    @Override
    public void writePinChallengeCanceled(boolean pinChallengeCanceled) {
        mPreferences.edit()
                .putBoolean(PIN_CHALLENGE_CANCELLED_PREFERENCE_KEY, pinChallengeCanceled)
                .apply();
    }

    @Override
    public boolean readOnlyBackgroundTimeout() {
        return mPreferences.getBoolean(
                ONLY_BACKGROUND_TIMEOUT_PREFERENCE_KEY, DefaultConstants.DEFAULT_ONLY_BACKGROUND_TIMEOUT);
    }

    @Override
    public void writeOnlyBackgroundTimeout(boolean onlyBackgroundTimeout) {
        mPreferences.edit()
                .putBoolean(ONLY_BACKGROUND_TIMEOUT_PREFERENCE_KEY, onlyBackgroundTimeout)
                .apply();
    }

    @Override
    public boolean readFingerprintAuthEnabled() {
        return mPreferences.getBoolean(
                FINGERPRINT_AUTH_ENABLED_PREFERENCE_KEY, DefaultConstants.DEFAULT_FINGERPRINT_AUTH_ENABLED);
    }

    @Override
    public void writeFingerprintAuthEnabled(boolean enabled) {
        mPreferences.edit()
                .putBoolean(FINGERPRINT_AUTH_ENABLED_PREFERENCE_KEY, enabled)
                .apply();
    }

    @Override
    public long readLastActiveMillis() {
        return mPreferences.getLong(LAST_ACTIVE_MILLIS_PREFERENCE_KEY, 0);
    }

    @Override
    public void writeLastActiveMillis(long millis) {
        mPreferences.edit()
                .putLong(LAST_ACTIVE_MILLIS_PREFERENCE_KEY, millis)
                .apply();
    }

    @Override
    public void clear() {
        mPreferences.edit()
                .remove(TIMEOUT_MILLIS_PREFERENCE_KEY)
                .remove(LOGO_ID_PREFERENCE_KEY)
                .remove(SHOW_FORGOT_PREFERENCE_KEY)
                .remove(PIN_CHALLENGE_CANCELLED_PREFERENCE_KEY)
                .remove(ONLY_BACKGROUND_TIMEOUT_PREFERENCE_KEY)
                .remove(FINGERPRINT_AUTH_ENABLED_PREFERENCE_KEY)
                .remove(LAST_ACTIVE_MILLIS_PREFERENCE_KEY)
                .apply();
    }
}
