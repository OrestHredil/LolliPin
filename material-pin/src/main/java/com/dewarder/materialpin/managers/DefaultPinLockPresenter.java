package com.dewarder.materialpin.managers;

import android.support.annotation.NonNull;

import com.dewarder.materialpin.DefaultConstants;
import com.dewarder.materialpin.PinManager;
import com.dewarder.materialpin.PinState;
import com.dewarder.materialpin.PinType;
import com.dewarder.materialpin.enums.KeyboardButton;
import com.dewarder.materialpin.util.Callback;
import com.dewarder.materialpin.util.CompositeFuture;
import com.dewarder.materialpin.util.ExecutorHelper;
import com.dewarder.materialpin.util.Objects;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.dewarder.materialpin.PinState.CONFIRM;
import static com.dewarder.materialpin.PinState.ENABLE;
import static com.dewarder.materialpin.PinState.UNLOCK;

public class DefaultPinLockPresenter implements PinLockPresenter {

    private final CompositeFuture mFutures = new CompositeFuture();

    protected PinLockView mView;

    private ExecutorService mExecutor;
    private PinManager mPinManager;

    protected PinState mState = UNLOCK;
    protected int mAttempts = 0;
    protected String mPinCode = EMPTY_PIN_CODE;
    protected String mOldPinCode = EMPTY_PIN_CODE;
    private boolean isCodeSuccessful = false;

    public DefaultPinLockPresenter() {
        mExecutor = Executors.newSingleThreadExecutor();
        mPinManager = MaterialPin.getLockManager().getPinManager();
    }

    public DefaultPinLockPresenter(@NonNull ExecutorService executor,
                                   @NonNull PinManager pinManager) {

        Objects.requireNonNulls(executor, pinManager);
        mExecutor = executor;
        mPinManager = pinManager;
    }

    @Override
    public void setView(@NonNull PinLockView view) {
        mView = Objects.requireNonNull(view);
    }

    @Override
    public void initializeAttempts() {
        Callback<Integer> callback = new Callback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                mAttempts = result;
            }

            @Override
            public void onError(Throwable throwable) {
                mView.onError(throwable);
            }
        };

        mFutures.add(
                ExecutorHelper.submit(mExecutor, mPinManager.getAttemptsCount(), callback));
    }

    @Override
    public void setInitialState(@NonNull PinState state) {
        mState = state;
    }

    @Override
    public PinState getCurrentState() {
        return mState;
    }

    private void setPinCode(String pinCode) {
        mPinCode = pinCode;
        mView.setPinLength(mPinCode.length());
    }

    @Override
    public void pinEntered() {
        switch (mState) {
            case DISABLE:
                disablePinCode();
                break;
            case ENABLE:
                enablePinCode();
                break;
            case CONFIRM:
                confirmPinCode();
                break;
            case CHANGE:
                changePinCode();
                break;
            case UNLOCK:
                unlockPinCode();
                break;

            default:
                throw new IllegalStateException("Unsupported pin entry state " + mState);
        }
    }

    @Override
    public void buttonPressed(@NonNull KeyboardButton button) {
        if (mPinCode.length() < getMaximumPinLength()) {
            if (button == KeyboardButton.BUTTON_CLEAR) {
                if (!mPinCode.isEmpty()) {
                    setPinCode(mPinCode.substring(0, mPinCode.length() - 1));
                } else {
                    setPinCode(EMPTY_PIN_CODE);
                }
            } else {
                setPinCode(mPinCode + button.getValue());
            }
        }
    }

    protected void disablePinCode() {
        Callback<Boolean> callback = new Callback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    clearPinCodeAfterDisabling();
                } else {
                    onPinCodeInvalid();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                mView.onError(throwable);
            }
        };

        mFutures.add(
                ExecutorHelper.submit(mExecutor, mPinManager.checkPin(mPinCode), callback));
    }

    protected void unlockPinCode() {
        Callback<Boolean> callback = new Callback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    onPinCodeSuccess();
                } else {
                    onPinCodeInvalid();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                mView.onError(throwable);
            }
        };

        mFutures.add(
                ExecutorHelper.submit(mExecutor, mPinManager.checkPin(mPinCode), callback));

    }

    protected void changePinCode() {
        Callback<Boolean> callback = new Callback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    mState = ENABLE;
                    mView.invalidateStep();
                    mView.invalidateForgotPin();
                    setPinCode(EMPTY_PIN_CODE);
                    onPinCodeSuccess();
                } else {
                    onPinCodeInvalid();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                mView.onError(throwable);
            }
        };

        mFutures.add(
                ExecutorHelper.submit(mExecutor, mPinManager.checkPin(mPinCode), callback));
    }

    protected void enablePinCode() {
        mOldPinCode = mPinCode;
        setPinCode(EMPTY_PIN_CODE);
        mState = CONFIRM;
        mView.invalidateStep();
        mView.invalidateForgotPin();
    }

    protected void confirmPinCode() {
        Runnable task;
        Callback<?> callback;
        if (mPinCode.equals(mOldPinCode)) {
            task = mPinManager.setPin(mPinCode);
            callback = new Callback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    onPinCodeSuccess();
                }

                @Override
                public void onError(Throwable throwable) {
                    mView.onError(throwable);
                }
            };
        } else {
            task = mPinManager.setPin(EMPTY_PIN_CODE);
            callback = new Callback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    mOldPinCode = EMPTY_PIN_CODE;
                    mState = ENABLE;
                    mView.invalidateStep();
                    mView.invalidateForgotPin();
                    onPinCodeInvalid();
                }

                @Override
                public void onError(Throwable throwable) {
                    mView.onError(throwable);
                }
            };
        }

        mFutures.add(
                ExecutorHelper.submit(mExecutor, task, callback));
    }

    private void clearPinCodeAfterDisabling() {
        Callback<?> callback = new Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                onPinCodeSuccess();
            }

            @Override
            public void onError(Throwable throwable) {
                mView.onError(throwable);
            }
        };

        mFutures.add(
                ExecutorHelper.submit(mExecutor, mPinManager.clearPin(), callback));
    }

    protected void onPinCodeSuccess() {
        Callback<?> callback = new Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                isCodeSuccessful = true;
                mView.onPinSuccess(PinType.CODE, mAttempts + 1);
                mAttempts = 0;
            }

            @Override
            public void onError(Throwable throwable) {
                mView.onError(throwable);
            }
        };

        mFutures.add(
                ExecutorHelper.submit(mExecutor, mPinManager.resetAttemptsCount(), callback));
    }

    protected void onPinCodeInvalid() {
        Callback<Integer> callback = new Callback<Integer>() {
            @Override
            public void onSuccess(Integer attempts) {
                mAttempts = attempts;
                mPinCode = EMPTY_PIN_CODE;
                mView.onPinInvalid(PinType.CODE, attempts);
            }

            @Override
            public void onError(Throwable throwable) {
                mView.onError(throwable);
            }
        };

        mFutures.add(
                ExecutorHelper.submit(mExecutor, mPinManager.incrementAttemptsCountAndGet(), callback));
    }

    @Override
    public int getCurrentPinLength() {
        return mPinCode.length();
    }

    @Override
    public int getMaximumPinLength() {
        return DefaultConstants.PIN_LENGTH;
    }

    @Override
    public void dispose() {
        mFutures.clear();
    }
}
