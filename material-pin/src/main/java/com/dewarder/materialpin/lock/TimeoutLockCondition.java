package com.dewarder.materialpin.lock;

import android.app.Activity;
import android.app.Application;
import android.support.annotation.NonNull;

import com.dewarder.materialpin.util.Objects;
import com.dewarder.materialpin.util.application.SimpleActivityCallbacks;

import java.util.concurrent.TimeUnit;

final class TimeoutLockCondition implements LockCondition {

    private final long timeoutMillis;
    private long lastMillis = 0;

    private TimeoutLockCondition(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

    @Override
    public boolean shouldLock(@NonNull Activity activity) {
        return lastMillis != 0 && System.currentTimeMillis() - lastMillis > timeoutMillis;
    }

    @NonNull
    @Override
    public Application.ActivityLifecycleCallbacks onAttachToApplication() {
        return new SimpleActivityCallbacks() {
            @Override
            public void onActivityResumed(Activity activity) {
                lastMillis = System.currentTimeMillis();
            }
        };
    }

    static LockCondition create(long value, @NonNull TimeUnit unit) {
        Objects.requireNonNull(unit);
        return new TimeoutLockCondition(
                TimeUnit.MILLISECONDS.convert(value, unit));
    }
}
