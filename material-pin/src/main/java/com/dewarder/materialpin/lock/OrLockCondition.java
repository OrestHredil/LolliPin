package com.dewarder.materialpin.lock;

import android.app.Activity;
import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dewarder.materialpin.util.Objects;
import com.dewarder.materialpin.util.application.PairedActivityLifecycleCallbacks;

final class OrLockCondition implements LockCondition {

    private final LockCondition left;
    private final LockCondition right;

    private OrLockCondition(LockCondition left, LockCondition right) {
        this.left = left;
        this.right = right;
    }

    static LockCondition create(@NonNull LockCondition left, @NonNull LockCondition right) {
        Objects.requireNonNulls(left, right);
        return new OrLockCondition(left, right);
    }

    @Override
    public boolean shouldLock(@NonNull Activity activity) {
        Objects.requireNonNull(activity);
        return left.shouldLock(activity) || right.shouldLock(activity);
    }

    @Nullable
    @Override
    public Application.ActivityLifecycleCallbacks onAttachToApplication() {
        return PairedActivityLifecycleCallbacks.create(
                left.onAttachToApplication(),
                right.onAttachToApplication());
    }
}
