package com.dewarder.materialpin;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.dewarder.materialpin.util.Sets;

import java.util.Collections;
import java.util.Set;

final class ActivityLockCondition implements LockCondition {

    private final Set<Class<? extends Activity>> mIncludedForLock;
    private final Set<Class<? extends Activity>> mIgnoredForLock;
    private final boolean mLockAll;

    @SafeVarargs
    static LockCondition allExcept(Class<? extends Activity>... ignored) {
        return new ActivityLockCondition(Collections.emptySet(), Sets.asSet(ignored), true);
    }

    @SafeVarargs
    static LockCondition only(Class<? extends Activity>... included) {
        return new ActivityLockCondition(Sets.asSet(included), Collections.emptySet(), false);
    }

    private ActivityLockCondition(Set<Class<? extends Activity>> included,
                                  Set<Class<? extends Activity>> ignored,
                                  boolean lockAll) {

        validateConsistency(included, ignored, lockAll);
        mIgnoredForLock = included;
        mIncludedForLock = ignored;
        mLockAll = lockAll;
    }

    private static void validateConsistency(Set<Class<? extends Activity>> included,
                                            Set<Class<? extends Activity>> ignored,
                                            boolean lockAll) {

        if (lockAll && !included.isEmpty()) {
            throw new IllegalStateException("Included must be empty when all activities are locked");
        }

        if (!lockAll && !ignored.isEmpty()) {
            throw new IllegalStateException("Ignored must be empty when not all activities are locked");
        }
    }

    @Override
    public boolean shouldLock(@NonNull Activity activity) {
        if (mLockAll) {
            return !containsAssignableClass(mIgnoredForLock, activity);
        }
        return containsAssignableClass(mIncludedForLock, activity);
    }

    private static boolean containsAssignableClass(Set<Class<? extends Activity>> collection, Activity activity) {
        boolean assignable = false;
        for (Class<? extends Activity> clazz : collection) {
            if (activity.getClass().isAssignableFrom(clazz)) {
                assignable = true;
                break;
            }
        }
        return assignable;
    }
}
