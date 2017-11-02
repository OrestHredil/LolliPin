package com.dewarder.materialpin.util.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public final class CompositeFuture {

    private final List<Future<?>> mFutures = new ArrayList<>();

    public void add(Future<?> future) {
        mFutures.add(future);
    }

    public void clear() {
        for (Future<?> future : mFutures) {
            if (!future.isDone()) {
                future.cancel(true);
            }
        }
        mFutures.clear();
    }
}
