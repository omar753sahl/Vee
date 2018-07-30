package com.os.vee.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Patterns;

import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.os.vento.R;

import static com.os.vee.utils.Constants.MIN_PASSWORD_CHAR_COUNT;

/**
 * Created by Omar on 14-Jul-18 12:03 AM.
 */
public class Utils {

    public static boolean isValidName(CharSequence name) {
        return !TextUtils.isEmpty(name);
    }

    public static boolean isValidEmail(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(CharSequence password) {
        return password.length() >= MIN_PASSWORD_CHAR_COUNT;
    }

    public static String getMessageFromFirebaseException(com.google.firebase.auth.FirebaseAuthException exception, Context context) {
        if (exception instanceof FirebaseAuthWeakPasswordException) {
            String reason = ((FirebaseAuthWeakPasswordException) exception).getReason();
            return reason == null ? context.getString(R.string.error_login_unknown_error) : reason;
        } else {
            String errorCode = exception.getErrorCode();
            switch (errorCode) {
                case "ERROR_USER_DISABLED": {
                    return context.getString(R.string.error_user_disabled);
                }
                case "ERROR_USER_NOT_FOUND": {
                    return context.getString(R.string.error_user_not_found);
                }
                case "ERROR_USER_TOKEN_EXPIRED": {
                    return context.getString(R.string.error_user_token_expired);
                }
                case "ERROR_EMAIL_ALREADY_IN_USE": {
                    return context.getString(R.string.error_email_already_in_use);
                }
                case "ERROR_INVALID_EMAIL": {
                    return context.getString(R.string.error_invalid_email);
                }
                case "ERROR_WRONG_PASSWORD": {
                    return context.getString(R.string.error_wrong_password);
                }
                case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL": {
                    return context.getString(R.string.error_account_exists_with_different_credential);
                }
                default: {
                    return context.getString(R.string.error_generic);
                }
            }
        }
    }
}
