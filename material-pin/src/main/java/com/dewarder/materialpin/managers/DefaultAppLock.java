package com.dewarder.materialpin.managers;

import android.app.Activity;
import android.arch.lifecycle.GenericLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dewarder.materialpin.PinActivity;
import com.dewarder.materialpin.PinCompatActivity;
import com.dewarder.materialpin.PinFragmentActivity;
import com.dewarder.materialpin.encryption.Encryptor;
import com.dewarder.materialpin.encryption.SaltGenerator;
import com.dewarder.materialpin.enums.Algorithm;
import com.dewarder.materialpin.storage.ConfigurationStorage;
import com.dewarder.materialpin.storage.PasscodeDataStorage;
import com.dewarder.materialpin.storage.impl.DefaultPreferencesConfigurationStorage;
import com.dewarder.materialpin.storage.impl.DefaultPreferencesPasscodeDataStorage;

import java.util.HashSet;

public class DefaultAppLock implements AppLock, GenericLifecycleObserver {

    public static final String TAG = DefaultAppLock.class.getSimpleName();

    private final PasscodeDataStorage mPasscodeDataStorage;
    private final ConfigurationStorage mConfigurationStorage;

    /**
     * The activity class that extends {@link com.dewarder.materialpin.managers.AppLockActivity}
     */
    private final Class<? extends AppLockActivity> mActivityClass;

    /**
     * A {@link java.util.HashSet} of {@link java.lang.String} which are the classes we don't want to
     * take into account for the {@link PinActivity}. These activities
     * will not log the last opened time, will not launch the
     * {@link com.dewarder.materialpin.managers.AppLockActivity} etc...
     */
    private final HashSet<String> mIgnoredActivities = new HashSet<>();

    DefaultAppLock(Context context, Class<? extends AppLockActivity> activityClass) {
        mActivityClass = activityClass;
        mPasscodeDataStorage = new DefaultPreferencesPasscodeDataStorage(context);
        mConfigurationStorage = new DefaultPreferencesConfigurationStorage(context);
    }

    DefaultAppLock(Builder builder) {
        mActivityClass = builder.getActivityClass();
        mConfigurationStorage = getConfigurationStorageOrDefault(builder);
        mPasscodeDataStorage = getPasscodeDataStorageOrDefault(builder);
    }

    private static ConfigurationStorage getConfigurationStorageOrDefault(Builder builder) {
        ConfigurationStorage storage = builder.getConfigurationStorage();
        if (storage == null) {
            return new DefaultPreferencesConfigurationStorage(builder.getContext());
        } else {
            return storage;
        }
    }

    private static PasscodeDataStorage getPasscodeDataStorageOrDefault(Builder builder) {
        PasscodeDataStorage storage = builder.getPasscodeDataStorage();
        if (storage == null) {
            return new DefaultPreferencesPasscodeDataStorage(builder.getContext());
        } else {
            return storage;
        }
    }

    @Override
    public void addIgnoredActivity(Class<? extends Activity> clazz) {
        mIgnoredActivities.add(clazz.getName());
    }

    @Override
    public void removeIgnoredActivity(Class<? extends Activity> clazz) {
        mIgnoredActivities.remove(clazz.getName());
    }

    @Override
    public long getTimeout() {
        return mConfigurationStorage.readTimeout();
    }

    @Override
    public void setTimeout(long timeout) {
        mConfigurationStorage.writeTimeout(timeout);
    }

    public String getSalt() {
        String salt = mPasscodeDataStorage.readSalt();
        if (salt == null) {
            salt = SaltGenerator.generate();
            setSalt(salt);
        }
        return salt;
    }

    private void setSalt(String salt) {
        mPasscodeDataStorage.writeSalt(salt);
    }

    @Override
    public int getLogoId() {
        return mConfigurationStorage.readLogoId();
    }

    @Override
    public void setLogoId(int logoId) {
        mConfigurationStorage.writeLogoId(logoId);
    }

    @Override
    public boolean shouldShowForgot(int appLockType) {
        return mConfigurationStorage.readShouldShowForgot()
                && appLockType != AppLock.ENABLE_PINLOCK
                && appLockType != AppLock.CONFIRM_PIN;
    }

    @Override
    public void setShouldShowForgot(boolean showForgot) {
        mConfigurationStorage.writeShouldShowForgot(showForgot);
    }

    @Override
    public boolean pinChallengeCancelled() {
        return mConfigurationStorage.readPinChallengeCanceled();
    }

    @Override
    public void setPinChallengeCancelled(boolean backedOut) {
        mConfigurationStorage.writePinChallengeCanceled(backedOut);
    }

    @Override
    public boolean onlyBackgroundTimeout() {
        return mConfigurationStorage.readOnlyBackgroundTimeout();
    }

    @Override
    public void setOnlyBackgroundTimeout(boolean onlyBackgroundTimeout) {
        mConfigurationStorage.writeOnlyBackgroundTimeout(onlyBackgroundTimeout);
    }

    @Override
    public void enable() {
        PinActivity.setListener(this);
        PinCompatActivity.setListener(this);
        PinFragmentActivity.setListener(this);
    }

    @Override
    public void disable() {
        PinActivity.clearListeners();
        PinCompatActivity.clearListeners();
        PinFragmentActivity.clearListeners();
    }

    @Override
    public void disableAndRemoveConfiguration() {
        PinActivity.clearListeners();
        PinCompatActivity.clearListeners();
        PinFragmentActivity.clearListeners();
        mConfigurationStorage.clear();
        mPasscodeDataStorage.clear();
    }

    @Override
    public boolean isFingerprintAuthEnabled() {
        return mConfigurationStorage.readFingerprintAuthEnabled();
    }

    @Override
    public void setFingerprintAuthEnabled(boolean enabled) {
        mConfigurationStorage.writeFingerprintAuthEnabled(enabled);
    }

    @Override
    public long getLastActiveMillis() {
        return mConfigurationStorage.readLastActiveMillis();
    }

    @Override
    public void setLastActiveMillis() {
        mConfigurationStorage.writeLastActiveMillis(System.currentTimeMillis());
    }

    @Override
    public int getAttemptsCount() {
        return mPasscodeDataStorage.readAttemptsCount();
    }

    @Override
    public int incrementAttemptsCountAndGet() {
        int attempts = mPasscodeDataStorage.readAttemptsCount() + 1;
        mPasscodeDataStorage.writeAttemptsCount(attempts);
        return attempts;
    }

    @Override
    public void resetAttemptsCount() {
        mPasscodeDataStorage.writeAttemptsCount(0);
    }

    @Override
    public boolean checkPasscode(String passcode) {
        Algorithm algorithm = mPasscodeDataStorage.readCurrentAlgorithm();

        String salt = getSalt();
        passcode = salt + passcode + salt;
        passcode = Encryptor.getSHA(passcode, algorithm);
        String storedPasscode = "";

        if (isPasscodeSet()) {
            storedPasscode = mPasscodeDataStorage.readPasscode();
        }

        return storedPasscode.equalsIgnoreCase(passcode);
    }

    @Override
    public boolean setPasscode(String passcode) {
        String salt = getSalt();
        if (passcode == null) {
            mPasscodeDataStorage.clearPasscode();
            this.disable();
        } else {
            setAlgorithm(Algorithm.SHA256);
            passcode = Encryptor.getSHA(salt + passcode + salt, Algorithm.SHA256);
            mPasscodeDataStorage.writePasscode(passcode);
            this.enable();
        }
        return true;
    }

    /**
     * Set the algorithm used in {@link #setPasscode(String)}
     */
    private void setAlgorithm(Algorithm algorithm) {
        mPasscodeDataStorage.writeCurrentAlgorithm(algorithm);
    }

    @Override
    public boolean isPasscodeSet() {
        return mPasscodeDataStorage.hasPasscode();
    }

    @Override
    public boolean isIgnoredActivity(Activity activity) {
        String clazzName = activity.getClass().getName();

        // ignored activities
        if (mIgnoredActivities.contains(clazzName)) {
            Log.d(TAG, "ignore activity " + clazzName);
            return true;
        }

        return false;
    }

    @Override
    public boolean shouldLockSceen(Activity activity) {
        Log.d(TAG, "Lollipin shouldLockSceen() called");

        // previously backed out of pin screen
        if (pinChallengeCancelled()) {
            return true;
        }

        // already unlock
        if (activity instanceof AppLockActivity) {
            AppLockActivity ala = (AppLockActivity) activity;
            if (ala.getType() == AppLock.UNLOCK_PIN) {
                Log.d(TAG, "already unlock activity");
                return false;
            }
        }

        // no pass code set
        if (!isPasscodeSet()) {
            Log.d(TAG, "lock passcode not set.");
            return false;
        }

        // no enough timeout
        long lastActiveMillis = getLastActiveMillis();
        long passedTime = System.currentTimeMillis() - lastActiveMillis;
        long timeout = getTimeout();
        if (lastActiveMillis > 0 && passedTime <= timeout) {
            Log.d(TAG, "no enough timeout " + passedTime + " for "
                    + timeout);
            return false;
        }

        return true;
    }

    @Override
    public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
        if (source instanceof Activity) {
            switch (event) {
                case ON_PAUSE: {
                    onActivityPaused((Activity) source);
                    break;
                }
                case ON_RESUME: {
                    onActivityResumed((Activity) source);
                    break;
                }
            }
        }
    }

    private void onActivityPaused(Activity activity) {
        if (isIgnoredActivity(activity)) {
            return;
        }

        String clazzName = activity.getClass().getName();
        Log.d(TAG, "onActivityPaused " + clazzName);

        if ((onlyBackgroundTimeout() || !shouldLockSceen(activity)) && !(activity instanceof AppLockActivity)) {
            setLastActiveMillis();
        }
    }

    private void onActivityResumed(Activity activity) {
        if (isIgnoredActivity(activity)) {
            return;
        }

        String clazzName = activity.getClass().getName();
        Log.d(TAG, "onActivityResumed " + clazzName);

        if (shouldLockSceen(activity)) {
            Log.d(TAG, "mActivityClass.getClass() " + mActivityClass);
            Intent intent = new Intent(activity.getApplicationContext(),
                    mActivityClass);
            intent.putExtra(AppLock.EXTRA_TYPE, AppLock.UNLOCK_PIN);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.getApplication().startActivity(intent);
        }

        if (!shouldLockSceen(activity) && !(activity instanceof AppLockActivity)) {
            setLastActiveMillis();
        }
    }
}
