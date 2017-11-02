package com.dewarder.materialpin;

import android.app.Activity;
import android.support.annotation.NonNull;

public interface LockCondition {

    boolean shouldLock(@NonNull Activity activity);

    @SafeVarargs
    static LockCondition only(Class<? extends Activity>... activities) {
        return ActivityLockCondition.only(activities);
    }

    @SafeVarargs
    static LockCondition allExcept(Class<? extends Activity>... activities) {
        return ActivityLockCondition.allExcept(activities);
    }
}
