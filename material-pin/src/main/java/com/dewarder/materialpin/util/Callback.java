package com.dewarder.materialpin.util;

public interface Callback<T> {

    void onSuccess(T result);

    void onError(Throwable throwable);
}
