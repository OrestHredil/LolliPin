package com.dewarder.materialpin.managers;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.dewarder.materialpin.FingerprintManager;
import com.dewarder.materialpin.LockCondition;
import com.dewarder.materialpin.PinManager;
import com.dewarder.materialpin.PinState;
import com.dewarder.materialpin.storage.impl.DefaultPreferencesPinDataStorage;
import com.dewarder.materialpin.util.Objects;
import com.dewarder.materialpin.util.SimpleActivityCallbacks;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

public class DefaultLockManager implements LockManager {

    private Context mContext;
    private PinManager mPinManager;
    private FingerprintManager mFingerprintManager;

    private Class<? extends Activity> mPinActivityClass;
    private AtomicBoolean mIsLocked = new AtomicBoolean(false);

    private final Set<LockCondition> mConditions = new CopyOnWriteArraySet<>();

    protected DefaultLockManager() {
    }

    public DefaultLockManager(Application application) {
        mContext = application;
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
        Objects.requireNonNulls(conditions);
        mConditions.addAll(Arrays.asList(conditions));
    }

    @Override
    public void removeConditions(@NonNull LockCondition... conditions) {
        Objects.requireNonNulls(conditions);
        mConditions.removeAll(Arrays.asList(conditions));
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
        Intent intent = new Intent(mContext, mPinActivityClass);
        //TODO:
        intent.putExtra("EXTRA_PIN_STATE", PinState.UNLOCK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    protected class ActivityLockHandler extends SimpleActivityCallbacks {
        @Override
        public void onActivityResumed(Activity activity) {
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
