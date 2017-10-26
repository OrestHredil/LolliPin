package com.dewarder.materialpin.interfaces;

import com.dewarder.materialpin.enums.KeyboardButtonEnum;

/**
 * Created by stoyan and oliviergoutay on 1/13/15.
 * The {@link com.dewarder.materialpin.managers.AppLockActivity} will implement
 * this in order to receive events from {@link com.dewarder.materialpin.views.KeyboardButtonView}
 * and {@link com.dewarder.materialpin.views.KeyboardView}
 */
public interface KeyboardButtonClickedListener {

    /**
     * Receive the click of a button, just after a {@link android.view.View.OnClickListener} has fired.
     * Called before {@link #onRippleAnimationEnd()}.
     * @param keyboardButtonEnum The organized enum of the clicked button
     */
    void onKeyboardClick(KeyboardButtonEnum keyboardButtonEnum);

    /**
     * Receive the end of a {@link com.andexert.library.RippleView} animation using a
     * {@link com.andexert.library.RippleAnimationListener} to determine the end.
     * Called after {@link #onKeyboardClick(com.dewarder.materialpin.enums.KeyboardButtonEnum)}.
     */
    void onRippleAnimationEnd();

}
