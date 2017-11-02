package com.dewarder.materialpin;

import android.support.annotation.StringRes;

import com.github.lollipin.lib.R;

public enum PinState {
    ENABLE {
        @Override
        public int getStepDescription() {
            return R.string.pin_code_step_create;
        }

        @Override
        public boolean hasForgotPin() {
            return false;
        }
    },
    DISABLE {
        @Override
        public int getStepDescription() {
            return R.string.pin_code_step_disable;
        }

        @Override
        public boolean canBack() {
            return true;
        }
    },
    CHANGE {
        @Override
        public int getStepDescription() {
            return R.string.pin_code_step_change;
        }

        @Override
        public boolean canBack() {
            return true;
        }
    },
    CONFIRM {
        @Override
        public int getStepDescription() {
            return R.string.pin_code_step_enable_confirm;
        }

        @Override
        public boolean hasForgotPin() {
            return false;
        }
    },
    UNLOCK {
        @Override
        public int getStepDescription() {
            return R.string.pin_code_step_unlock;
        }
    };

    @StringRes
    public abstract int getStepDescription();

    public boolean canBack() {
        return false;
    }

    public boolean hasForgotPin() {
        return true;
    }
}
