package com.dewarder.materialpin;

public final class DefaultConstants {

    public static final int LOGO_ID_NONE = -1;

    public static final int PIN_LENGTH = 4;
    public static final long TIMEOUT = 1000 * 10; // 10sec
    public static final boolean SHOW_FORGOT = true;
    public static final boolean PIN_CHALLENGE_CANCELED = false;
    public static final boolean ONLY_BACKGROUND_TIMEOUT = false;
    public static final boolean FINGERPRINT_AUTH_ENABLED = true;

    private DefaultConstants() {
        throw new UnsupportedOperationException();
    }
}
