package com.dewarder.materialpin;

import android.app.Application;
import android.support.annotation.NonNull;

import com.dewarder.materialpin.manager.impl.DefaultLockManager;
import com.dewarder.materialpin.manager.LockManager;
import com.dewarder.materialpin.util.Objects;

public final class MaterialPin {

    private static LockManager sLockManager;

    private MaterialPin() {
        throw new UnsupportedOperationException();
    }

    public static void initDefault(@NonNull Application application) {
        Objects.requireNonNull(application);
        sLockManager = new DefaultLockManager(application);
    }

    public static void setDefaultLockManager(@NonNull LockManager lockManager) {
        sLockManager = Objects.requireNonNull(lockManager);
    }

    @NonNull
    public static LockManager getLockManager() {
        if (sLockManager == null) {
            throw new IllegalStateException("LockManager must be initialized before call");
        }
        return sLockManager;
    }
}
