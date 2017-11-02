package com.dewarder.materialpin;

import android.support.annotation.NonNull;

import java.util.concurrent.Callable;

public interface PinManager {

    Runnable clearPin();

    Runnable setPin(@NonNull String pin);

    Callable<Boolean> checkPin(@NonNull String pin);

    Callable<Integer> getAttemptsCount();

    Callable<Integer> incrementAttemptsCountAndGet();

    Runnable resetAttemptsCount();
}
