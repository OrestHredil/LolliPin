package com.dewarder.materialpin.managers;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.dewarder.materialpin.DefaultConstants;
import com.dewarder.materialpin.FingerprintManager;
import com.dewarder.materialpin.PinState;
import com.dewarder.materialpin.PinType;
import com.dewarder.materialpin.enums.KeyboardButton;
import com.dewarder.materialpin.interfaces.OnKeyboardButtonClickListener;
import com.dewarder.materialpin.views.KeyboardView;
import com.dewarder.materialpin.views.PinCodeRoundView;
import com.github.lollipin.lib.R;

import static com.dewarder.materialpin.PinState.UNLOCK;

/**
 * Created by stoyan and olivier on 1/13/15.
 * The activity that appears when the password needs to be set or has to be asked.
 * Call this activity in normal or singleTop mode (not singleTask or singleInstance, it does not work
 * with {@link android.app.Activity#startActivityForResult(android.content.Intent, int)}).
 */
public class PinLockActivity extends Activity implements
        OnKeyboardButtonClickListener,
        FingerprintHelper.Callback,
        PinLockView {

    public static final String TAG = PinLockActivity.class.getSimpleName();
    public static final String ACTION_CANCEL = TAG + ".actionCancelled";

    private static final String EXTRA_PIN_STATE = "EXTRA_PIN_STATE";

    protected TextView mStepTextView;
    protected TextView mForgotTextView;
    protected PinCodeRoundView mPinCodeRoundView;
    protected KeyboardView mKeyboardView;
    protected ImageView mFingerprintImageView;
    protected TextView mFingerprintTextView;

    protected PinLockPresenter mPresenter;
    protected LockManager mLockManager;
    protected FingerprintManager mFingerprintManager;

    protected FingerprintHelper mFingerprintHelper;

    /**
     * Gets the resource id to the {@link View} to be set with {@link #setContentView(int)}.
     * The custom layout must include the following:
     * - {@link TextView} with an id of pin_code_step_textview
     * - {@link TextView} with an id of pin_code_forgot_textview
     * - {@link PinCodeRoundView} with an id of pin_code_round_view
     * - {@link KeyboardView} with an id of pin_code_keyboard_view
     *
     * @return the resource id to the {@link View}
     */
    protected int getContentView() {
        return R.layout.activity_pin_code;
    }

    /**
     * First creation
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getContentView());
        initLayout(getIntent());
    }

    /**
     * If called in singleTop mode
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        initLayout(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Init layout for Fingerprint
        initLayoutForFingerprint();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mFingerprintHelper != null) {
            mFingerprintHelper.stopListening();
        }
    }

    private void initLayout(Intent intent) {
        overridePendingTransition(R.anim.nothing, R.anim.nothing);

        mPresenter = getPinLockPresenter();
        mLockManager = getLockManager();
        mFingerprintManager = getFingerprintManager();

        mPresenter.setView(this);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            PinState state = (PinState) extras.getSerializable(EXTRA_PIN_STATE);
            if (state != null) {
                mPresenter.setInitialState(state);
            }
        }
        mPresenter.initializeAttempts();

        mStepTextView = findViewById(R.id.pin_code_step_textview);
        mPinCodeRoundView = findViewById(R.id.pin_code_round_view);
        mPinCodeRoundView.setPinLength(mPresenter.getMaximumPinLength());
        mForgotTextView = findViewById(R.id.pin_code_forgot_textview);
        mForgotTextView.setOnClickListener(v -> showForgotDialog());
        mKeyboardView = findViewById(R.id.pin_code_keyboard_view);
        mKeyboardView.setKeyboardButtonClickedListener(this);

        int logoId = DefaultConstants.LOGO_ID_NONE;
        ImageView logoImage = findViewById(R.id.pin_code_logo_imageview);
        if (logoId != DefaultConstants.LOGO_ID_NONE) {
            logoImage.setVisibility(View.VISIBLE);
            logoImage.setImageResource(logoId);
        }

        mForgotTextView.setText(getForgotText());

        invalidateForgotPin();
        invalidateStep();
    }

    @Override
    public PinLockPresenter getPinLockPresenter() {
        return new DefaultPinLockPresenter();
    }

    public LockManager getLockManager() {
        return MaterialPin.getLockManager();
    }

    public FingerprintManager getFingerprintManager() {
        return mLockManager.getFingerprintManager();
    }

    private void initLayoutForFingerprint() {
        mFingerprintImageView = findViewById(R.id.pin_code_fingerprint_imageview);
        mFingerprintTextView = findViewById(R.id.pin_code_fingerprint_textview);

        if (mPresenter.getCurrentState() == UNLOCK && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mFingerprintHelper = new FingerprintHelper.Builder(this)
                    .setIconView(mFingerprintImageView)
                    .setErrorView(mFingerprintTextView)
                    .setCallback(this)
                    .build();

            if (mFingerprintManager.isEnabled() && mFingerprintHelper.isAvailable()) {
                mFingerprintImageView.setVisibility(View.VISIBLE);
                mFingerprintTextView.setVisibility(View.VISIBLE);
                mFingerprintHelper.startListening();
                return;
            }
        }

        mFingerprintImageView.setVisibility(View.GONE);
        mFingerprintTextView.setVisibility(View.GONE);
    }

    @Override
    public void setPinLength(int length) {
        mPinCodeRoundView.refresh(length);
    }

    @Override
    public void invalidateStep() {
        @StringRes int description = mPresenter.getCurrentState().getStepDescription();
        int pinLength = mPresenter.getMaximumPinLength();
        mStepTextView.setText(getString(description, pinLength));
    }

    @Override
    public void invalidateForgotPin() {
        PinState pinState = mPresenter.getCurrentState();
        mForgotTextView.setVisibility(pinState.hasForgotPin() ? View.VISIBLE : View.GONE);
    }

    public String getForgotText() {
        return getString(R.string.pin_code_forgot_text);
    }

    /**
     * Overrides to allow a slide_down animation when finishing
     */
    @Override
    public void finish() {
        super.finish();

        //If code successful, reset the timer
/*        if (isCodeSuccessful) {
            if (mLockManager != null) {
*//*                AppLock appLock = null;//TODO: mLockManager.getAppLock();
                if (appLock != null) {
                    appLock.setLastActiveMillis();
                }*//*
            }
        }*/

        overridePendingTransition(R.anim.nothing, R.anim.slide_down);
    }


    @Override
    public void onKeyboardButtonClicked(@NonNull KeyboardButton button) {
        mPresenter.buttonPressed(button);
    }

    @Override
    public void onRippleAnimationEnd() {
        if (mPresenter.getCurrentPinLength() == mPresenter.getMaximumPinLength()) {
            mPresenter.pinEntered();
        }
    }

    /**
     * Override {@link #onBackPressed()} to prevent user for finishing the activity
     */
    @Override
    public void onBackPressed() {
        PinState state = mPresenter.getCurrentState();
        if (state.canBack()) {
            if (state == UNLOCK) {
                //TODO: mLockManager.getAppLock().setPinChallengeCancelled(true);
                LocalBroadcastManager
                        .getInstance(this)
                        .sendBroadcast(new Intent().setAction(ACTION_CANCEL));
            }
            super.onBackPressed();
        }
    }

    @Override
    public void onAuthenticated() {
        Log.e(TAG, "onAuthenticated");
        onPinSuccess(PinType.FINGERPRINT, 0);
    }

    @Override
    public void onError() {
        Log.e(TAG, "onError");
    }

    /**
     * Displays the information dialog when the user clicks the
     * {@link #mForgotTextView}
     */
    public void showForgotDialog() {

    }


    @CallSuper
    @Override
    public void onPinInvalid(@NonNull PinType type, int attempts) {
        mPinCodeRoundView.clear();
        Animation animation = AnimationUtils.loadAnimation(
                PinLockActivity.this, R.anim.shake);
        mKeyboardView.startAnimation(animation);
    }

    @CallSuper
    @Override
    public void onPinSuccess(@NonNull PinType type, int attempts) {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onError(Throwable throwable) {

    }
}
