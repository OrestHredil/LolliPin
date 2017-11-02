package com.dewarder.materialpin.util;

public final class Objects {

    private Objects() {
        throw new UnsupportedOperationException();
    }

    public static <T> T requireNonNull(T object) {
        return requireNonNull(object, "Non-null object is required");
    }

    @SafeVarargs
    public static <T> T[] requireNonNulls(T... objects) {
        for (Object o : objects) {
            if (o == null) {
                throw new NullPointerException("All objects should be non-null");
            }
        }
        return objects;
    }

    public static <T extends Iterable<?>> T requireNonNulls(T objects) {
        for (Object o : objects) {
            if (o == null) {
                throw new NullPointerException("All objects should be non-null");
            }
        }
        return objects;
    }

    public static <T> T requireNonNull(T object, String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
        return object;
    }
}
