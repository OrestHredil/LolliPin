package com.dewarder.materialpin;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.dewarder.materialpin.FingerprintManager;
import com.dewarder.materialpin.lock.LockCondition;
import com.dewarder.materialpin.PinManager;

public interface LockManager {

    void lock();

    void unlock();

    boolean isLocked();

    void addConditions(@NonNull LockCondition... condition);

    void removeConditions(@NonNull LockCondition... condition);

    void setPinLockActivity(@NonNull Class<? extends Activity> lockActivityClass);

    @NonNull
    FingerprintManager getFingerprintManager();

    @NonNull
    PinManager getPinManager();
}
