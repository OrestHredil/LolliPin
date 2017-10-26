package com.dewarder.materialpin;

import android.app.Application;

import com.dewarder.materialpin.managers.LockManager;

import lollipin.orangegangsters.github.com.lollipin.R;

/**
 * Created by oliviergoutay on 1/14/15.
 */
public class CustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        LockManager lockManager = LockManager.getInstance();
        lockManager.enableAppLock(this, CustomPinActivity.class);
        lockManager.getAppLock().setLogoId(R.drawable.security_lock);
    }
}
