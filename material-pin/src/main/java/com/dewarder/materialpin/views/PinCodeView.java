package com.dewarder.materialpin.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.github.lollipin.lib.R;

/**
 * Created by stoyan and olivier on 1/12/15.
 */
public class PinCodeView extends RelativeLayout {

    public PinCodeView(Context context) {
        this(context, null);
    }

    public PinCodeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PinCodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.activity_pin_code, this);
    }
}
