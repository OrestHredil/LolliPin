package com.dewarder.materialpin.manager.impl;

import com.dewarder.materialpin.manager.FingerprintManager;

public class DefaultFingerprintManager implements FingerprintManager {

    @Override
    public boolean isEnabled() {
        return true;
    }
}
