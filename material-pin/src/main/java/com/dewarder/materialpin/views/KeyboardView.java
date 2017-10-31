package com.dewarder.materialpin.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.dewarder.materialpin.enums.KeyboardButton;
import com.dewarder.materialpin.interfaces.OnKeyboardButtonClickListener;
import com.github.lollipin.lib.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stoyan and olivier on 1/13/15.
 */
public class KeyboardView extends LinearLayout implements View.OnClickListener {

    private OnKeyboardButtonClickListener mOnKeyboardButtonClickListener;

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
        if (mOnKeyboardButtonClickListener != null) {
            KeyboardButton button = KeyboardButton.fromId(v.getId());
            mOnKeyboardButtonClickListener.onKeyboardButtonClicked(button);
        }
    }

    /**
     * Set the {@link com.andexert.library.RippleAnimationListener} to the
     * {@link com.dewarder.materialpin.views.KeyboardButtonView}
     */
    public void setKeyboardButtonClickedListener(OnKeyboardButtonClickListener onKeyboardButtonClickListener) {
        mOnKeyboardButtonClickListener = onKeyboardButtonClickListener;
        for (KeyboardButtonView button : mButtons) {
            button.setOnRippleAnimationEndListener(mOnKeyboardButtonClickListener);
        }
    }
}
