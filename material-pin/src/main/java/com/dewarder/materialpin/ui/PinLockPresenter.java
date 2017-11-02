package com.dewarder.materialpin.ui;

import android.support.annotation.NonNull;

import com.dewarder.materialpin.PinState;
import com.dewarder.materialpin.enums.KeyboardButton;

public interface PinLockPresenter {

    String EMPTY_PIN_CODE = "";

    void setView(@NonNull PinLockView view);

    void initializeAttempts();

    void setInitialState(@NonNull PinState state);

    PinState getCurrentState();

    void buttonPressed(@NonNull KeyboardButton button);

    void pinEntered();

    int getCurrentPinLength();

    int getMaximumPinLength();

    void dispose();
}
