package com.dewarder.materialpin.util;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public final class ExecutorHelper {

    private static final Handler UI_HANDLER = new Handler(Looper.getMainLooper());

    public static <T> Future<T> submit(@NonNull ExecutorService service,
                                       @NonNull Callable<T> task,
                                       @NonNull Callback<T> callback) {
        Objects.requireNonNulls(service, task, callback);
        return service.submit(new CallableWrapper<>(task, callback));
    }

    public static Future<?> submit(ExecutorService service,
                                   Runnable task,
                                   Callback<?> callback) {
        Objects.requireNonNulls(service, task, callback);
        return service.submit(new RunnableWrapper(task, callback));
    }

    private static class CallableWrapper<T> implements Callable<T> {

        private final Callable<T> original;
        private final Callback<T> callback;

        CallableWrapper(Callable<T> original, Callback<T> callback) {
            this.original = original;
            this.callback = callback;
        }

        @Override
        public T call() throws Exception {
            try {
                T result = original.call();
                UI_HANDLER.post(() -> callback.onSuccess(result));
                return result;
            } catch (Throwable t) {
                UI_HANDLER.post(() -> callback.onError(t));
                throw new RuntimeException(t);
            }
        }
    }

    private static class RunnableWrapper implements Runnable {

        private final Runnable runnable;
        private final Callback<?> callback;

        RunnableWrapper(Runnable runnable, Callback<?> callback) {
            this.runnable = runnable;
            this.callback = callback;
        }

        @Override
        public void run() {
            try {
                runnable.run();
                callback.onSuccess(null);
            } catch (Throwable t) {
                UI_HANDLER.post(() -> callback.onError(t));
                throw new RuntimeException(t);
            }
        }
    }
}
