package com.dewarder.materialpin.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.dewarder.materialpin.enums.KeyboardButton;
import com.dewarder.materialpin.ui.PinLockActivity;
import com.github.lollipin.lib.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stoyan and olivier on 1/13/15.
 */
public class KeyboardView extends LinearLayout implements View.OnClickListener {

    /**
     * Created by stoyan and oliviergoutay on 1/13/15.
     * The {@link PinLockActivity} will implement
     * this in order to receive events from {@link com.dewarder.materialpin.views.KeyboardButtonView}
     * and {@link com.dewarder.materialpin.views.KeyboardView}
     */
    public interface OnButtonClickListener {

        /**
         * Receive the click of a button, just after a {@link android.view.View.OnClickListener} has fired.
         * Called before {@link #onRippleAnimationEnd()}.
         *
         * @param keyboardButtonEnum The organized enum of the clicked button
         */
        void onKeyboardButtonClicked(@NonNull KeyboardButton keyboardButtonEnum);

        /**
         * Receive the end of a {@link com.andexert.library.RippleView} animation using a
         * {@link com.andexert.library.RippleAnimationListener} to determine the end.
         * Called after {@link #onKeyboardButtonClicked(KeyboardButton)}.
         */
        void onRippleAnimationEnd();

    }

    private OnButtonClickListener mOnButtonClickListener;

    private final List<KeyboardButtonView> mButtons = new ArrayList<>();

    public KeyboardView(Context context) {
        this(context, null);
    }

    public KeyboardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            inflate(getContext(), R.layout.view_keyboard, this);
            initKeyboardButtons();
        }
    }

    /**
     * Init the keyboard buttons (onClickListener)
     */
    private void initKeyboardButtons() {
        for (KeyboardButton button : KeyboardButton.values()) {
            KeyboardButtonView buttonView = findViewById(button.getId());
            buttonView.setOnClickListener(this);
            mButtons.add(buttonView);
        }
    }

    @Override
    public void onClick(View v) {
        if (mOnButtonClickListener != null) {
            KeyboardButton button = KeyboardButton.fromId(v.getId());
            mOnButtonClickListener.onKeyboardButtonClicked(button);
        }
    }

    /**
     * Set the {@link com.andexert.library.RippleAnimationListener} to the
     * {@link com.dewarder.materialpin.views.KeyboardButtonView}
     */
    public void setKeyboardButtonClickedListener(OnButtonClickListener onButtonClickListener) {
        mOnButtonClickListener = onButtonClickListener;
        for (KeyboardButtonView button : mButtons) {
            button.setOnRippleAnimationEndListener(mOnButtonClickListener);
        }
    }
}
