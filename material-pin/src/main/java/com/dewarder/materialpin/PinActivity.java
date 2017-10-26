package com.dewarder.materialpin;


import android.arch.lifecycle.GenericLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import com.dewarder.materialpin.managers.AppLockActivity;

/**
 * Created by stoyan and olivier on 1/12/15.
 * You must extend this Activity in order to support this library.
 * Then to enable PinCode blocking, you must call
 * {@link com.dewarder.materialpin.managers.LockManager#enableAppLock(android.content.Context, Class)}
 */
public class PinActivity extends AppCompatActivity implements LifecycleOwner {

    private final LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);
    private static GenericLifecycleObserver mLifeCycleListener;
    private final BroadcastReceiver mPinCancelledReceiver;

    public PinActivity() {
        mPinCancelledReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter(AppLockActivity.ACTION_CANCEL);
        LocalBroadcastManager.getInstance(this).registerReceiver(mPinCancelledReceiver, filter);
    }

    @Override
    protected void onResume() {
        if (mLifeCycleListener != null) {
            mLifeCycleListener.onStateChanged(this, Lifecycle.Event.ON_RESUME);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (mLifeCycleListener != null) {
            mLifeCycleListener.onActivityPaused(PinActivity.this);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mPinCancelledReceiver);
    }

    public static void setListener(GenericLifecycleObserver listener) {
        if (mLifeCycleListener != null) {
            mLifeCycleListener = null;
        }
        mLifeCycleListener = listener;
    }

    public static void clearListeners() {
        mLifeCycleListener = null;
    }

    public static boolean hasListeners() {
        return (mLifeCycleListener != null);
    }

    @Override
    public Lifecycle getLifecycle() {
        return mLifecycleRegistry;
    }
}
