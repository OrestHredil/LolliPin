package com.dewarder.materialpin.interfaces;

import android.support.annotation.NonNull;

import com.dewarder.materialpin.enums.KeyboardButton;

/**
 * Created by stoyan and oliviergoutay on 1/13/15.
 * The {@link com.dewarder.materialpin.managers.AppLockActivity} will implement
 * this in order to receive events from {@link com.dewarder.materialpin.views.KeyboardButtonView}
 * and {@link com.dewarder.materialpin.views.KeyboardView}
 */
public interface OnKeyboardButtonClickListener {

    /**
     * Receive the click of a button, just after a {@link android.view.View.OnClickListener} has fired.
     * Called before {@link #onRippleAnimationEnd()}.
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
