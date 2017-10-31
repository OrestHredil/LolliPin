package com.dewarder.materialpin.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class Sets {

    @SafeVarargs
    public static <T> Set<T> asSet(T... items) {
        return new HashSet<>(Arrays.asList(items));
    }
}
