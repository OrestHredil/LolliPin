package com.dewarder.materialpin.interfaces;

import android.app.Activity;

import com.dewarder.materialpin.PinActivity;
import com.dewarder.materialpin.managers.DefaultAppLock;

/**
 * Created by stoyan on 1/12/15.
 * Allows to follow the LifeCycle of the {@link PinActivity}
 * Implemented by {@link DefaultAppLock} in order to
 * determine when the app was launched for the last time and when to launch the
 * {@link com.dewarder.materialpin.managers.AppLockActivity}
 */
public interface LifeCycleInterface {

    /**
     * Called in {@link android.app.Activity#onResume()}
     */
    void onActivityResumed(Activity activity);

    /**
     * Called in {@link android.app.Activity#onPause()}
     */
    void onActivityPaused(Activity activity);
}
