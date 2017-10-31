package com.dewarder.materialpin.enums;

import android.support.annotation.IdRes;
import android.support.annotation.IntRange;

import com.github.lollipin.lib.R;

/**
 * Created by stoyan and oliviergoutay on 1/13/15.
 */
public enum KeyboardButton {

    BUTTON_0(0, R.id.pin_code_button_0),
    BUTTON_1(1, R.id.pin_code_button_1),
    BUTTON_2(2, R.id.pin_code_button_2),
    BUTTON_3(3, R.id.pin_code_button_3),
    BUTTON_4(4, R.id.pin_code_button_4),
    BUTTON_5(5, R.id.pin_code_button_5),
    BUTTON_6(6, R.id.pin_code_button_6),
    BUTTON_7(7, R.id.pin_code_button_7),
    BUTTON_8(8, R.id.pin_code_button_8),
    BUTTON_9(9, R.id.pin_code_button_9),
    BUTTON_CLEAR(-1, R.id.pin_code_button_clear);

    private final int mId;
    private final int mValue;

    public static KeyboardButton fromId(@IdRes int id) {
        for (KeyboardButton button : values()) {
            if (button.mId == id) {
                return button;
            }
        }
        throw new IllegalStateException("No keyboard button with such id " + id);
    }

    KeyboardButton(int value, int id) {
        mValue = value;
        mId = id;
    }

    @IdRes
    public int getId() {
        return mId;
    }

    @IntRange(from = -1, to = 9)
    public int getValue() {
        return mValue;
    }
}
