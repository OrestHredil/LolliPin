package com.dewarder.materialpin.managers;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dewarder.materialpin.storage.ConfigurationStorage;
import com.dewarder.materialpin.storage.PasscodeDataStorage;

public interface AppLock {

    /**
     * ENABLE_PINLOCK type, uses at firt to define the password
     */
    int ENABLE_PINLOCK = 0;
    /**
     * DISABLE_PINLOCK type, uses to disable the system by asking the current password
     */
    int DISABLE_PINLOCK = 1;
    /**
     * CHANGE_PIN type, uses to change the current password
     */
    int CHANGE_PIN = 2;
    /**
     * CONFIRM_PIN type, used to confirm the new password
     */
    int CONFIRM_PIN = 3;
    /**
     * UNLOCK_PIN type, uses to ask the password to the user, in order to unlock the app
     */
    int UNLOCK_PIN = 4;

    /**
     * LOGO_ID_NONE used to denote when a user has not set a logoId using {@link #setLogoId(int)}
     */
    int LOGO_ID_NONE = -1;

    /**
     * EXTRA_TYPE, uses to pass to the {@link com.dewarder.materialpin.managers.AppLockActivity}
     * to determine in which type it musts be started.
     */
    String EXTRA_TYPE = "type";

    /**
     * DEFAULT_TIMEOUT, define the default timeout returned by {@link #getTimeout()}.
     * If you want to modify it, you can call {@link #setTimeout(long)}. Will be stored using
     * {@link android.content.SharedPreferences}
     */
    long DEFAULT_TIMEOUT = 1000 * 10; // 10sec

    boolean DEFAULT_SHOW_FORGOT = true;

    boolean DEFAULT_PIN_CHALLENGE_CANCELED = false;

    boolean DEFAULT_ONLY_BACKGROUND_TIMEOUT = false;

    boolean DEFAULT_FINGERPRING_AUTH_ENABLED = true;

    static AppLock forActivity(@NonNull Context context,
                               @NonNull Class<? extends AppLockActivity> activityClass) {
        return new DefaultAppLock(context, activityClass);
    }

    /**
     * Add an ignored activity to the {@link java.util.HashSet}
     */
    void addIgnoredActivity(Class<? extends Activity> clazz);

    /**
     * Remove an ignored activity to the {@link java.util.HashSet}
     */
    void removeIgnoredActivity(Class<? extends Activity> clazz);

    /**
     * Get the timeout used in {@link #shouldLockSceen(android.app.Activity)}
     */
    long getTimeout();

    /**
     * Set the timeout used in {@link #shouldLockSceen(android.app.Activity)}
     */
    void setTimeout(long timeout);

    /**
     * Get logo resource id used by {@link com.dewarder.materialpin.managers.AppLockActivity}
     */
    int getLogoId();

    /**
     * Set logo resource id used by {@link com.dewarder.materialpin.managers.AppLockActivity}
     */
    void setLogoId(int logoId);

    /**
     * Get the forgot option used by {@link com.dewarder.materialpin.managers.AppLockActivity}
     */
    boolean shouldShowForgot(int appLockType);

    /**
     * Set the forgot option used by {@link com.dewarder.materialpin.managers.AppLockActivity}
     */
    void setShouldShowForgot(boolean showForgot);

    /**
     * Get whether the user backed out of the {@link AppLockActivity} previously
     */
    boolean pinChallengeCancelled();

    /**
     * Set whether the user backed out of the {@link AppLockActivity}
     */
    void setPinChallengeCancelled(boolean cancelled);


    /**
     * Get the only background timeout option used to determine if the time
     * spent in the activity must NOT be taken into account while calculating the timeout.
     */
    boolean onlyBackgroundTimeout();

    /**
     * Set whether the time spent on the activity must NOT be taken into account when calculating timeout.
     */
    void setOnlyBackgroundTimeout(boolean onlyBackgroundTimeout);

    /**
     * Enable the {@link com.dewarder.materialpin.managers.AppLock} by setting
     * {@link DefaultAppLock} as the
     * {@link com.dewarder.materialpin.interfaces.LifeCycleInterface}
     */
    void enable();

    /**
     * Disable the {@link com.dewarder.materialpin.managers.AppLock} by removing any
     * {@link com.dewarder.materialpin.interfaces.LifeCycleInterface}
     */
    void disable();

    /**
     * Disable the {@link com.dewarder.materialpin.managers.AppLock} by removing any
     * {@link com.dewarder.materialpin.interfaces.LifeCycleInterface} and also delete
     * all the previous saved configurations into {@link android.content.SharedPreferences}
     */
    void disableAndRemoveConfiguration();

    /**
     * Get the last active time of the app used by {@link #shouldLockSceen(android.app.Activity)}
     */
    long getLastActiveMillis();

    /**
     * Set the last active time of the app used by {@link #shouldLockSceen(android.app.Activity)}.
     * Set in {@link com.dewarder.materialpin.interfaces.LifeCycleInterface#onActivityPaused(android.app.Activity)}
     * and {@link com.dewarder.materialpin.interfaces.LifeCycleInterface#onActivityResumed(android.app.Activity)}
     */
    void setLastActiveMillis();

    int getAttemptsCount();

    int incrementAttemptsCountAndGet();

    void resetAttemptsCount();

    /**
     * Set the passcode (store his SHA1 into {@link android.content.SharedPreferences}) using the
     * {@link com.dewarder.materialpin.encryption.Encryptor} class.
     */
    boolean setPasscode(String passcode);

    /**
     * Check the {@link android.content.SharedPreferences} to see if fingerprint authentication is
     * enabled.
     */
    boolean isFingerprintAuthEnabled();

    /**
     * Enable or disable fingerprint authentication on the PIN screen.
     *
     * @param enabled If true, enables the fingerprint reader if it is supported.  If false, will
     *                hide the fingerprint reader icon on the PIN screen.
     */
    void setFingerprintAuthEnabled(boolean enabled);

    /**
     * Check the passcode by comparing his SHA1 into {@link android.content.SharedPreferences} using the
     * {@link com.dewarder.materialpin.encryption.Encryptor} class.
     */
    boolean checkPasscode(String passcode);

    /**
     * Check the {@link android.content.SharedPreferences} to see if a password already exists
     */
    boolean isPasscodeSet();

    /**
     * Check if an activity must be ignored and then don't call the
     * {@link com.dewarder.materialpin.interfaces.LifeCycleInterface}
     */
    boolean isIgnoredActivity(Activity activity);

    /**
     * Evaluates if:
     * - we are already into the {@link com.dewarder.materialpin.managers.AppLockActivity}
     * - the passcode is not set
     * - the timeout didn't reached
     * If any of this is true, then we don't need to start the
     * {@link com.dewarder.materialpin.managers.AppLockActivity} (it returns false)
     * Otherwise returns true
     */
    boolean shouldLockSceen(Activity activity);

    class Builder {

        private final Context mContext;
        private final Class<? extends AppLockActivity> mActivityClass;

        private ConfigurationStorage mConfigurationStorage;
        private PasscodeDataStorage mPasscodeDataStorage;

        public Builder(Context context, Class<? extends AppLockActivity> activityClass) {
            mContext = context;
            mActivityClass = activityClass;
        }

        @NonNull
        Context getContext() {
            return mContext;
        }

        @NonNull
        Class<? extends AppLockActivity> getActivityClass() {
            return mActivityClass;
        }

        @Nullable
        ConfigurationStorage getConfigurationStorage() {
            return mConfigurationStorage;
        }

        public Builder setConfigurationStorage(@Nullable ConfigurationStorage configurationStorage) {
            mConfigurationStorage = configurationStorage;
            return this;
        }

        @Nullable
        PasscodeDataStorage getPasscodeDataStorage() {
            return mPasscodeDataStorage;
        }

        public Builder setPasscodeDataStorage(@Nullable PasscodeDataStorage passcodeDataStorage) {
            mPasscodeDataStorage = passcodeDataStorage;
            return this;
        }

        public AppLock build() {
            return new DefaultAppLock(this);
        }
    }
}
