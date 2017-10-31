package com.dewarder.materialpin.managers;

import android.support.annotation.NonNull;

import com.dewarder.materialpin.util.Objects;
import com.dewarder.materialpin.PinManager;
import com.dewarder.materialpin.encryption.Encryptor;
import com.dewarder.materialpin.encryption.SaltGenerator;
import com.dewarder.materialpin.storage.PinDataStorage;

public class DefaultPinManager implements PinManager {

    private PinDataStorage mStorage;

    public DefaultPinManager(@NonNull PinDataStorage storage) {
        mStorage = Objects.requireNonNull(storage);
    }

    @Override
    public void clearPin() {
        mStorage.clearPin();
    }

    @Override
    public void setPin(@NonNull String pin) {
        String salt = getSalt();
        String pinSHA = Encryptor.getSHA(salt + pin + salt);
        mStorage.writePin(pinSHA);
    }

    @Override
    public boolean checkPin(@NonNull String pin) {
        String salt = getSalt();
        String pinSHA = Encryptor.getSHA(salt + pin + salt);

        return mStorage.hasPin() &&
                mStorage.readPasscode().equalsIgnoreCase(pinSHA);
    }

    @Override
    public int incrementAttemptsCountAndGet() {
        return 0;
    }

    @Override
    public void resetAttemptsCount() {

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
