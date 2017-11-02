package com.dewarder.materialpin.manager.impl;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.dewarder.materialpin.lock.LockCondition;
import com.dewarder.materialpin.PinState;
import com.dewarder.materialpin.manager.FingerprintManager;
import com.dewarder.materialpin.manager.LockManager;
import com.dewarder.materialpin.manager.PinManager;
import com.dewarder.materialpin.storage.impl.DefaultPreferencesPinDataStorage;
import com.dewarder.materialpin.ui.PinLockActivity;
import com.dewarder.materialpin.util.Objects;
import com.dewarder.materialpin.util.application.SimpleActivityCallbacks;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

public class DefaultLockManager implements LockManager {

    private Application mApplication;
    private PinManager mPinManager;
    private FingerprintManager mFingerprintManager;

    private Class<? extends Activity> mLastActivityClass;
    private Class<? extends Activity> mPinActivityClass;
    private AtomicBoolean mIsLocked = new AtomicBoolean(false);

    private final Set<LockCondition> mConditions = new CopyOnWriteArraySet<>();
    private final Map<LockCondition, Application.ActivityLifecycleCallbacks> mConditionCallbackMap =
            new HashMap<>();

    protected DefaultLockManager() {
    }

    public DefaultLockManager(Application application) {
        mApplication = application;
        mPinActivityClass = PinLockActivity.class;
        mPinManager = new DefaultPinManager(new DefaultPreferencesPinDataStorage(application));
        mFingerprintManager = new DefaultFingerprintManager();
        application.registerActivityLifecycleCallbacks(new ActivityLockHandler());
    }

    @Override
    public void lock() {
        mIsLocked.set(true);
    }

    @Override
    public void unlock() {
        mIsLocked.set(false);
    }

    @Override
    public boolean isLocked() {
        return mIsLocked.get();
    }

    @Override
    public void addConditions(@NonNull LockCondition... conditions) {
        for (LockCondition condition : conditions) {
            Objects.requireNonNull(condition);
            mConditions.add(condition);

            Application.ActivityLifecycleCallbacks callbacks = condition.onAttachToApplication();
            if (callbacks != null) {
                mConditionCallbackMap.put(condition, callbacks);
                mApplication.registerActivityLifecycleCallbacks(callbacks);
            }
        }
    }

    @Override
    public void removeConditions(@NonNull LockCondition... conditions) {
        for (LockCondition condition : conditions) {
            Objects.requireNonNull(condition);
            mConditions.remove(condition);

            Application.ActivityLifecycleCallbacks callbacks = mConditionCallbackMap.remove(condition);
            if (callbacks != null) {
                mApplication.unregisterActivityLifecycleCallbacks(callbacks);
            }
        }

    }

    @Override
    public void setPinLockActivity(@NonNull Class<? extends Activity> lockActivityClass) {
        mPinActivityClass = Objects.requireNonNull(lockActivityClass);
    }

    @NonNull
    @Override
    public FingerprintManager getFingerprintManager() {
        return mFingerprintManager;
    }

    @NonNull
    @Override
    public PinManager getPinManager() {
        return mPinManager;
    }

    public void setPinActivity(@NonNull Class<? extends Activity> pinActivity) {
        Objects.requireNonNull(pinActivity);
        mPinActivityClass = pinActivity;
    }

    protected void openPinActivity() {
        Intent intent = new Intent(mApplication, mPinActivityClass);
        //TODO:
        intent.putExtra("EXTRA_PIN_STATE", PinState.UNLOCK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mApplication.startActivity(intent);
    }

    protected class ActivityLockHandler extends SimpleActivityCallbacks {
        @Override
        public void onActivityResumed(Activity activity) {
            Class<? extends Activity> activityClass = activity.getClass();
            if (activityClass.equals(mLastActivityClass)) {
                return;
            }
            if (activityClass.isAssignableFrom(mPinActivityClass)) {
                return;
            }

            mLastActivityClass = activityClass;

            if (!mIsLocked.get()) {
                return;
            }

            for (LockCondition condition : mConditions) {
                if (condition.shouldLock(activity)) {
                    openPinActivity();
                    break;
                }
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {
            if (!mIsLocked.get()) {
                return;
            }
        }
    }
}
