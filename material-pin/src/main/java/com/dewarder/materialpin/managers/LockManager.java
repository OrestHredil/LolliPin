package com.dewarder.materialpin.managers;

import android.content.Context;
import android.support.annotation.Nullable;

import com.dewarder.materialpin.PinActivity;
import com.dewarder.materialpin.PinCompatActivity;
import com.dewarder.materialpin.PinFragmentActivity;

/**
 * Allows to handle the {@link com.dewarder.materialpin.managers.AppLock} from within
 * the actual app calling the library.
 * You must get this static instance by calling {@link #getInstance()}
 */
public class LockManager {

    /**
     * The static singleton instance
     */
    private static final LockManager INSTANCE = new LockManager();
    /**
     * The static singleton instance of {@link com.dewarder.materialpin.managers.AppLock}
     */
    private AppLock mAppLocker;

    /**
     * Used to retrieve the static instance
     */
    public static LockManager getInstance() {
        return INSTANCE;
    }

    /**
     * You must call that into your custom {@link android.app.Application} to enable the
     * {@link PinActivity}
     */
    public void enableAppLock(Context context, Class<? extends AppLockActivity> activityClass) {
        if (mAppLocker != null) {
            mAppLocker.disable();
        }
        mAppLocker = AppLock.forActivity(context, activityClass);
        mAppLocker.enable();
    }

    /**
     * Tells the app if the {@link com.dewarder.materialpin.managers.AppLock} is enabled or not
     */
    public boolean isAppLockEnabled() {
        return mAppLocker != null && (PinActivity.hasListeners() ||
                PinFragmentActivity.hasListeners() || PinCompatActivity.hasListeners());
    }

    /**
     * Disables the app lock by calling {@link AppLock#disable()}
     */
    public void disableAppLock() {
        if (mAppLocker != null) {
            mAppLocker.disable();
        }
        mAppLocker = null;
    }

    /**
     * Disables the previous app lock and set a new one
     */
    public void setAppLock(@Nullable AppLock appLocker) {
        if (mAppLocker != null) {
            mAppLocker.disable();
        }
        mAppLocker = appLocker;
    }

    /**
     * Get the {@link AppLock}. Used for defining custom timeouts etc...
     */
    @Nullable
    public AppLock getAppLock() {
        return mAppLocker;
    }
}
