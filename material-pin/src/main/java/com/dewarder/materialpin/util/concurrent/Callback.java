package com.dewarder.materialpin.util.concurrent;

public interface Callback<T> {

    void onSuccess(T result);

    void onError(Throwable throwable);
}
