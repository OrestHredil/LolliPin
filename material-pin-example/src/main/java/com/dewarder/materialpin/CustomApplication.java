package com.dewarder.materialpin;

import android.app.Application;

import com.dewarder.materialpin.managers.LockManager;
import com.dewarder.materialpin.managers.MaterialPin;

/**
 * Created by oliviergoutay on 1/14/15.
 */
public class CustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MaterialPin.initDefault(this);

        LockManager lockManager = MaterialPin.getLockManager();
        lockManager.addConditions(
                ActivityLockCondition.onlyFor(MainActivity.class));
    }
}
