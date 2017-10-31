package com.dewarder.materialpin;

import android.app.Activity;
import android.support.annotation.NonNull;

public interface LockCondition {

    boolean shouldLock(@NonNull Activity activity);
}
