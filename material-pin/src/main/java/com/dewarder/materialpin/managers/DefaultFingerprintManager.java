package com.dewarder.materialpin.managers;

import com.dewarder.materialpin.FingerprintManager;

public class DefaultFingerprintManager implements FingerprintManager {

    @Override
    public boolean isEnabled() {
        return true;
    }
}
