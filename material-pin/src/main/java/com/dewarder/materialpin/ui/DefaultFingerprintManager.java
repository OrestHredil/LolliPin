package com.dewarder.materialpin.ui;

import com.dewarder.materialpin.FingerprintManager;

public class DefaultFingerprintManager implements FingerprintManager {

    @Override
    public boolean isEnabled() {
        return true;
    }
}
