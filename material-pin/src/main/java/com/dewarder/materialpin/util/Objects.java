package com.dewarder.materialpin.util;

public final class Objects {

    private Objects() {
        throw new UnsupportedOperationException();
    }

    public static <T> T requireNonNull(T object) {
        return requireNonNull(object, "Non-null object is required");
    }

    public static <T> T requireNonNull(T object, String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
        return object;
    }
}
