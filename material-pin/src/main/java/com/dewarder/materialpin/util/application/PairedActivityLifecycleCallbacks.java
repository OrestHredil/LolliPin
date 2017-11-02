package com.dewarder.materialpin.util.application;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.Nullable;

public final class PairedActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    private final Application.ActivityLifecycleCallbacks first;
    private final Application.ActivityLifecycleCallbacks second;

    private PairedActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks first,
                                             Application.ActivityLifecycleCallbacks second) {
        this.first = first;
        this.second = second;
    }

    @Nullable
    public static Application.ActivityLifecycleCallbacks create(
            Application.ActivityLifecycleCallbacks first,
            Application.ActivityLifecycleCallbacks second) {

        if (first == null && second == null) {
            return null;
        }
        if (first != null && second != null) {
            return PairedActivityLifecycleCallbacks.create(first, second);
        }
        if (first != null) {
            return first;
        }
        return second;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        first.onActivityCreated(activity, savedInstanceState);
        second.onActivityCreated(activity, savedInstanceState);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        first.onActivityStarted(activity);
        second.onActivityStarted(activity);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        first.onActivityResumed(activity);
        second.onActivityResumed(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        first.onActivityPaused(activity);
        second.onActivityPaused(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        first.onActivityStopped(activity);
        second.onActivityStopped(activity);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        first.onActivitySaveInstanceState(activity, outState);
        second.onActivitySaveInstanceState(activity, outState);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        first.onActivityDestroyed(activity);
        second.onActivityDestroyed(activity);
    }
}
