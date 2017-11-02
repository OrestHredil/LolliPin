package com.dewarder.materialpin;

import android.app.Application;

import com.dewarder.materialpin.lock.LockCondition;
import com.dewarder.materialpin.manager.LockManager;

import java.util.concurrent.TimeUnit;

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
                LockCondition.only(LockedCompatActivity.class)
                        .or(LockCondition.timeout(10, TimeUnit.SECONDS)));
        lockManager.lock();
        lockManager.setPinLockActivity(CustomPinActivity.class);
    }
}
