package com.dewarder.materialpin.managers;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.dewarder.materialpin.DefaultConstants;
import com.dewarder.materialpin.FingerprintManager;
import com.dewarder.materialpin.PinManager;
import com.dewarder.materialpin.PinState;
import com.dewarder.materialpin.enums.KeyboardButton;
import com.dewarder.materialpin.interfaces.OnKeyboardButtonClickListener;
import com.dewarder.materialpin.views.KeyboardView;
import com.dewarder.materialpin.views.PinCodeRoundView;
import com.github.lollipin.lib.R;

import static com.dewarder.materialpin.PinState.CONFIRM;
import static com.dewarder.materialpin.PinState.ENABLE;
import static com.dewarder.materialpin.PinState.UNLOCK;

/**
 * Created by stoyan and olivier on 1/13/15.
 * The activity that appears when the password needs to be set or has to be asked.
 * Call this activity in normal or singleTop mode (not singleTask or singleInstance, it does not work
 * with {@link android.app.Activity#startActivityForResult(android.content.Intent, int)}).
 */
public class AppLockActivity extends Activity implements OnKeyboardButtonClickListener, FingerprintHelper.Callback {

    public static final String TAG = AppLockActivity.class.getSimpleName();
    public static final String ACTION_CANCEL = TAG + ".actionCancelled";

    private static final String EXTRA_PIN_STATE = "EXTRA_PIN_STATE";
    private static final String EMPTY_PIN_CODE = "";
    private static final int DEFAULT_PIN_LENGTH = 4;

    protected TextView mStepTextView;
    protected TextView mForgotTextView;
    protected PinCodeRoundView mPinCodeRoundView;
    protected KeyboardView mKeyboardView;
    protected ImageView mFingerprintImageView;
    protected TextView mFingerprintTextView;

    protected LockManager mLockManager;
    protected PinManager mPinManager;
    protected FingerprintManager mFingerprintManager;

    protected FingerprintHelper mFingerprintHelper;

    protected PinState mState = UNLOCK;
    protected int mAttempts = 0;
    protected String mPinCode = EMPTY_PIN_CODE;
    protected String mOldPinCode = EMPTY_PIN_CODE;
    private boolean isCodeSuccessful = false;


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

        Bundle extras = intent.getExtras();
        if (extras != null) {
            mState = (PinState) extras.getSerializable(EXTRA_PIN_STATE);
        }

        mLockManager = MaterialPin.getLockManager();
        mPinManager = mLockManager.getPinManager();
        mFingerprintManager = mLockManager.getFingerprintManager();

        //mLockManager.getAppLock().setPinChallengeCancelled(false);
        //mAttempts = mLockManager.getAppLock().getAttemptsCount();

        mStepTextView = findViewById(R.id.pin_code_step_textview);
        mPinCodeRoundView = findViewById(R.id.pin_code_round_view);
        mPinCodeRoundView.setPinLength(getPinLength());
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
        setForgotTextVisibility();

        setStepText();
    }

    private void initLayoutForFingerprint() {
        mFingerprintImageView = findViewById(R.id.pin_code_fingerprint_imageview);
        mFingerprintTextView = findViewById(R.id.pin_code_fingerprint_textview);

        if (mState == UNLOCK && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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

    private void setStepText() {
        mStepTextView.setText(
                getString(mState.getStepDescription(), getPinLength()));
    }

    public String getForgotText() {
        return getString(R.string.pin_code_forgot_text);
    }

    private void setForgotTextVisibility() {
        //TODO:   mForgotTextView.setVisibility(mLockManager.getAppLock().shouldShowForgot(mType) ? View.VISIBLE : View.GONE);
    }

    /**
     * Overrides to allow a slide_down animation when finishing
     */
    @Override
    public void finish() {
        super.finish();

        //If code successful, reset the timer
        if (isCodeSuccessful) {
            if (mLockManager != null) {
/*                AppLock appLock = null;//TODO: mLockManager.getAppLock();
                if (appLock != null) {
                    appLock.setLastActiveMillis();
                }*/
            }
        }

        overridePendingTransition(R.anim.nothing, R.anim.slide_down);
    }

    /**
     * Add the button clicked to {@link #mPinCode} each time.
     * Refreshes also the {@link com.dewarder.materialpin.views.PinCodeRoundView}
     */
    @Override
    public void onKeyboardButtonClicked(@NonNull KeyboardButton button) {
        if (mPinCode.length() < getPinLength()) {
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

    /**
     * Called at the end of the animation of the {@link com.andexert.library.RippleView}
     * Calls {@link #onPinCodeInputed} when {@link #mPinCode}
     */
    @Override
    public void onRippleAnimationEnd() {
        if (mPinCode.length() == getPinLength()) {
            onPinCodeInputed();
        }
    }

    protected void onPinCodeInputed() {
        switch (mState) {
            case DISABLE:
                handleDisableState();
                break;
            case ENABLE:
                handleEnableState();
                break;
            case CONFIRM:
                handleConfirmState();
                break;
            case CHANGE:
                handleChangeState();
                break;
            case UNLOCK:
                handleUnlockState();
                break;
        }
    }

    protected void handleDisableState() {
        if (!mPinManager.checkPin(mPinCode)) {
            onPinCodeError();
            return;
        }

        mPinManager.clearPin();
        onPinCodeSuccess();
        setResult(RESULT_OK);
        finish();
    }

    protected void handleEnableState() {
        mOldPinCode = mPinCode;
        setPinCode(EMPTY_PIN_CODE);
        mState = CONFIRM;
        setStepText();
        setForgotTextVisibility();
    }

    protected void handleConfirmState() {
        if (mPinCode.equals(mOldPinCode)) {
            mPinManager.setPin(mPinCode);
            onPinCodeSuccess();
            setResult(RESULT_OK);
            finish();
        } else {
            mOldPinCode = EMPTY_PIN_CODE;
            setPinCode(EMPTY_PIN_CODE);
            mState = ENABLE;
            setStepText();
            setForgotTextVisibility();
            onPinCodeError();
        }
    }

    protected void handleChangeState() {
        if (!mPinManager.checkPin(mPinCode)) {
            onPinCodeError();
            return;
        }

        mState = ENABLE;
        setStepText();
        setForgotTextVisibility();
        setPinCode(EMPTY_PIN_CODE);
        onPinCodeSuccess();
    }

    protected void handleUnlockState() {
        if (!mPinManager.checkPin(mPinCode)) {
            onPinCodeError();
            return;
        }

        onPinCodeSuccess();
        setResult(RESULT_OK);
        finish();
    }

    /**
     * Override {@link #onBackPressed()} to prevent user for finishing the activity
     */
    @Override
    public void onBackPressed() {
        if (mState.canBack()) {
            if (mState == UNLOCK) {
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
        onPinCodeSuccess();
        setResult(RESULT_OK);
        finish();
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

    /**
     * Run a shake animation when the password is not valid.
     */
    protected void onPinCodeError() {
        mAttempts = mLockManager.getPinManager().incrementAttemptsCountAndGet();
        onPinFailure(mAttempts);
        runOnUiThread(() -> {
            mPinCode = EMPTY_PIN_CODE;
            mPinCodeRoundView.refresh(mPinCode.length());
            Animation animation = AnimationUtils.loadAnimation(
                    AppLockActivity.this, R.anim.shake);
            mKeyboardView.startAnimation(animation);
        });
    }

    protected void onPinCodeSuccess() {
        isCodeSuccessful = true;
        onPinSuccess(mAttempts + 1);
        mAttempts = 0;
        mLockManager.getPinManager().resetAttemptsCount();
    }

    /**
     * Set the pincode and refreshes the {@link com.dewarder.materialpin.views.PinCodeRoundView}
     */
    public void setPinCode(String pinCode) {
        mPinCode = pinCode;
        mPinCodeRoundView.refresh(mPinCode.length());
    }

    /**
     * When the user has failed a pin challenge
     *
     * @param attempts the number of attempts the user has used
     */
    public void onPinFailure(int attempts) {

    }

    /**
     * When the user has succeeded at a pin challenge
     *
     * @param attempts the number of attempts the user had used
     */
    public void onPinSuccess(int attempts) {

    }

    /**
     * Gets the number of digits in the pin code.  Subclasses can override this to change the
     * length of the pin.
     *
     * @return the number of digits in the PIN
     */
    public int getPinLength() {
        return AppLockActivity.DEFAULT_PIN_LENGTH;
    }
}
