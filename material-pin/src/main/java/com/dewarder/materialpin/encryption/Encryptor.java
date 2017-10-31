package com.dewarder.materialpin.encryption;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.dewarder.materialpin.managers.DefaultAppLock;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/**
 * Used by {@link DefaultAppLock} to get the SHA1
 * of the 4-digit password.
 */
public final class Encryptor {

    private Encryptor() {
        throw new UnsupportedOperationException();
    }

    /**
     * Allows to get the SHA of a {@link java.lang.String} using {@link java.security.MessageDigest}
     * if device does not support sha-256, fall back to sha-1 instead
     */
    @NonNull
    public static String getSHA(String text) {
        String sha = "";
        if (TextUtils.isEmpty(text)) {
            return sha;
        }

        MessageDigest shaDigest = getShaDigest();
        byte[] textBytes = text.getBytes();
        shaDigest.update(textBytes, 0, text.length());
        byte[] shahash = shaDigest.digest();
        return bytes2Hex(shahash);
    }

    /**
     * Convert a chain of bytes into a {@link java.lang.String}
     *
     * @param bytes The chain of bytes
     * @return The converted String
     */
    private static String bytes2Hex(byte[] bytes) {
        StringBuilder hs = new StringBuilder();
        String stmp = "";
        for (byte aByte : bytes) {
            stmp = (Integer.toHexString(aByte & 0XFF));
            if (stmp.length() == 1) {
                hs.append("0").append(stmp);
            } else {
                hs.append(stmp);
            }
        }
        return hs.toString().toLowerCase(Locale.ENGLISH);
    }

    /**
     * Gets the default {@link MessageDigest} to use.
     * Select {@link Algorithm#SHA256} in {@link DefaultAppLock#setPasscode(String)}
     * but can be {@link Algorithm#SHA1} for older versions.
     *
     * @param algorithm The {@link Algorithm} to use
     */
    private static MessageDigest getShaDigest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            try {
                return MessageDigest.getInstance("SHA-1");
            } catch (NoSuchAlgorithmException e1) {
                throw new RuntimeException(e1);
            }
        }
    }
}
