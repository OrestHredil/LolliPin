package com.dewarder.materialpin.manager.impl;

import android.support.annotation.NonNull;

import com.dewarder.materialpin.encryption.Encryptor;
import com.dewarder.materialpin.encryption.SaltGenerator;
import com.dewarder.materialpin.manager.PinManager;
import com.dewarder.materialpin.storage.PinDataStorage;
import com.dewarder.materialpin.util.Objects;

import java.util.concurrent.Callable;

public class DefaultPinManager implements PinManager {

    private PinDataStorage mStorage;

    public DefaultPinManager(@NonNull PinDataStorage storage) {
        mStorage = Objects.requireNonNull(storage);
    }

    @Override
    public Runnable clearPin() {
        return mStorage::clearPin;
    }

    @Override
    public Runnable setPin(@NonNull String pin) {
        return () -> {
            String salt = getSalt();
            String pinSHA = Encryptor.getSHA(salt + pin + salt);
            mStorage.writePin(pinSHA);
        };
    }

    @Override
    public Callable<Boolean> checkPin(@NonNull String pin) {
        return () -> {
            String salt = getSalt();
            String pinSHA = Encryptor.getSHA(salt + pin + salt);
            return mStorage.hasPin() &&
                    mStorage.readPasscode().equalsIgnoreCase(pinSHA);
        };
    }

    @Override
    public Callable<Integer> getAttemptsCount() {
        return mStorage::readAttemptsCount;
    }

    @Override
    public Callable<Integer> incrementAttemptsCountAndGet() {
        return () -> {
            int attempts = mStorage.readAttemptsCount() + 1;
            mStorage.writeAttemptsCount(attempts);
            return attempts;
        };
    }

    @Override
    public Runnable resetAttemptsCount() {
        return () -> mStorage.writeAttemptsCount(0);
    }

    @NonNull
    protected String getSalt() {
        String salt = mStorage.readSalt();
        if (salt == null) {
            salt = SaltGenerator.generate();
            setSalt(salt);
        }
        return salt;
    }

    protected void setSalt(@NonNull String salt) {
        mStorage.writeSalt(salt);
    }
}
