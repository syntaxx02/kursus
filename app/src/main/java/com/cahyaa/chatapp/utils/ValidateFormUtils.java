package com.cahyaa.chatapp.utils;

import com.google.android.material.textfield.TextInputLayout;

public class ValidateFormUtils {

    public boolean validateField(String value, TextInputLayout textInputLayout, boolean isPatternMatch, String errorEmptyMessage, String errorWrongMessage) {
        if (value.isEmpty()) {
            textInputLayout.setError(errorEmptyMessage);
            return false;
        } else if (!isPatternMatch) {
            textInputLayout.setError(errorWrongMessage);
            return false;
        } else {
            textInputLayout.setError("");
            return true;
        }
    }

}