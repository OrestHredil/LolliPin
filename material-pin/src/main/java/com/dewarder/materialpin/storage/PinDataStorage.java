package com.dewarder.materialpin.storage;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface PinDataStorage {

    int readAttemptsCount();

    void writeAttemptsCount(int attempts);

    @Nullable
    String readSalt();

    void writeSalt(@Nullable String salt);

    @NonNull
    String readPasscode();

    void writePin(@NonNull String pin);

    boolean hasPin();

    void clearPin();

    void clear();
}
