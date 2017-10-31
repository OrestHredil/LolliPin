package com.dewarder.materialpin;

public final class DefaultConstants {

    public static final int LOGO_ID_NONE = -1;
    public static final long DEFAULT_TIMEOUT = 1000 * 10; // 10sec
    public static final boolean DEFAULT_SHOW_FORGOT = true;
    public static final boolean DEFAULT_PIN_CHALLENGE_CANCELED = false;
    public static final boolean DEFAULT_ONLY_BACKGROUND_TIMEOUT = false;
    public static final boolean DEFAULT_FINGERPRINT_AUTH_ENABLED = true;

    private DefaultConstants() {
        throw new UnsupportedOperationException();
    }
}
