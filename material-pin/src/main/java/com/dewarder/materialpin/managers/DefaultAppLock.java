package com.dewarder.materialpin.managers;

import android.app.Activity;
import android.util.Log;

import com.dewarder.materialpin.storage.ConfigurationStorage;
import com.dewarder.materialpin.storage.PinDataStorage;

import java.util.HashSet;

public class DefaultAppLock {

    public static final String TAG = DefaultAppLock.class.getSimpleName();

    private PinDataStorage mPinDataStorage;
    private ConfigurationStorage mConfigurationStorage;

    /**
     * The activity class that extends {@link com.dewarder.materialpin.managers.AppLockActivity}
     */
    private Class<? extends AppLockActivity> mActivityClass;

    private final HashSet<String> mIgnoredActivities = new HashSet<>();

    public void addIgnoredActivity(Class<? extends Activity> clazz) {
        mIgnoredActivities.add(clazz.getName());
    }

    public void removeIgnoredActivity(Class<? extends Activity> clazz) {
        mIgnoredActivities.remove(clazz.getName());
    }

    public long getTimeout() {
        return mConfigurationStorage.readTimeout();
    }

    public void setTimeout(long timeout) {
        mConfigurationStorage.writeTimeout(timeout);
    }

    public String getSalt() {
        return null;
    }

    public int getLogoId() {
        return mConfigurationStorage.readLogoId();
    }

    public void setLogoId(int logoId) {
        mConfigurationStorage.writeLogoId(logoId);
    }

/*    public boolean shouldShowForgot(int appLockType) {
        return mConfigurationStorage.readShouldShowForgot()
                && appLockType != AppLock.ENABLE_PINLOCK
                && appLockType != AppLock.CONFIRM_PIN;
    }*/

    public void setShouldShowForgot(boolean showForgot) {
        mConfigurationStorage.writeShouldShowForgot(showForgot);
    }

    public boolean pinChallengeCancelled() {
        return mConfigurationStorage.readPinChallengeCanceled();
    }

    public void setPinChallengeCancelled(boolean backedOut) {
        mConfigurationStorage.writePinChallengeCanceled(backedOut);
    }

    public boolean onlyBackgroundTimeout() {
        return mConfigurationStorage.readOnlyBackgroundTimeout();
    }

    public void setOnlyBackgroundTimeout(boolean onlyBackgroundTimeout) {
        mConfigurationStorage.writeOnlyBackgroundTimeout(onlyBackgroundTimeout);
    }

    public void enable() {
    }

    public void disable() {
    }

    public void disableAndRemoveConfiguration() {
        mConfigurationStorage.clear();
        mPinDataStorage.clear();
    }

    public boolean isFingerprintAuthEnabled() {
        return mConfigurationStorage.readFingerprintAuthEnabled();
    }

    public void setFingerprintAuthEnabled(boolean enabled) {
        mConfigurationStorage.writeFingerprintAuthEnabled(enabled);
    }

    public boolean checkPasscode(String passcode) {
        return false;
    }

    public long getLastActiveMillis() {
        return mConfigurationStorage.readLastActiveMillis();
    }

    public void setLastActiveMillis() {
        mConfigurationStorage.writeLastActiveMillis(System.currentTimeMillis());
    }

    public int getAttemptsCount() {
        return mPinDataStorage.readAttemptsCount();
    }

    public int incrementAttemptsCountAndGet() {
        int attempts = mPinDataStorage.readAttemptsCount() + 1;
        mPinDataStorage.writeAttemptsCount(attempts);
        return attempts;
    }

    public boolean isIgnoredActivity(Activity activity) {
        String clazzName = activity.getClass().getName();

        // ignored activities
        if (mIgnoredActivities.contains(clazzName)) {
            Log.d(TAG, "ignore activity " + clazzName);
            return true;
        }

        return false;
    }

/*
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
    }*/


/*    private void onActivityPaused(Activity activity) {
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
            intent.putExtra(AppLock.EXTRA_PIN_STATE, AppLock.UNLOCK_PIN);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.getApplication().startActivity(intent);
        }

        if (!shouldLockSceen(activity) && !(activity instanceof AppLockActivity)) {
            setLastActiveMillis();
        }
    }*/
}
