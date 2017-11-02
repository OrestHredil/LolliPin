package com.dewarder.materialpin.lock;

import android.app.Activity;
import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.concurrent.TimeUnit;

public interface LockCondition {

    boolean shouldLock(@NonNull Activity activity);

    @Nullable
    default Application.ActivityLifecycleCallbacks onAttachToApplication() {
        return null;
    }

    @NonNull
    default LockCondition and(LockCondition another) {
        return AndLockCondition.create(this, another);
    }

    @NonNull
    default LockCondition or(LockCondition another) {
        return OrLockCondition.create(this, another);
    }

    @SafeVarargs
    static LockCondition only(Class<? extends Activity>... activities) {
        return ActivityLockCondition.only(activities);
    }

    @SafeVarargs
    static LockCondition allExcept(Class<? extends Activity>... activities) {
        return ActivityLockCondition.allExcept(activities);
    }

    static LockCondition timeout(long value, TimeUnit unit) {
        return TimeoutLockCondition.create(value, unit);
    }
}
