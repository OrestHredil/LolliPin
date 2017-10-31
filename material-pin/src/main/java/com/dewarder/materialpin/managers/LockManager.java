package com.dewarder.materialpin.managers;

import android.support.annotation.NonNull;

import com.dewarder.materialpin.FingerprintManager;
import com.dewarder.materialpin.LockCondition;
import com.dewarder.materialpin.PinManager;

public interface LockManager {

    void lock();

    void unlock();

    boolean isLocked();

    void addCondition(@NonNull LockCondition condition);

    void removeCondition(@NonNull LockCondition condition);

    @NonNull
    FingerprintManager getFingerprintManager();

    @NonNull
    PinManager getPinManager();
}
