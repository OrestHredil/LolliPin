package com.dewarder.materialpin;

import android.support.annotation.NonNull;

public interface PinManager {

    void clearPin();

    void setPin(@NonNull String pin);

    boolean checkPin(@NonNull String pin);

    int incrementAttemptsCountAndGet();

    void resetAttemptsCount();
}
