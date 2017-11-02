package com.dewarder.materialpin.managers;

import android.support.annotation.NonNull;

import com.dewarder.materialpin.PinType;

public interface PinLockView {

    PinLockPresenter getPinLockPresenter();

    void invalidateStep();

    void invalidateForgotPin();

    void setPinLength(int length);

    void onPinInvalid(@NonNull PinType type, int attempts);

    void onPinSuccess(@NonNull PinType type, int attempts);

    void onError(Throwable throwable);
}
