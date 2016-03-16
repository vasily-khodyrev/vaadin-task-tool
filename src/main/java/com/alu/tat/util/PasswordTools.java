package com.alu.tat.util;

import com.vaadin.data.Validator;
import com.vaadin.data.validator.AbstractValidator;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by
 * User: vkhodyre
 * Date: 3/16/2016
 */
public class PasswordTools {

    private static MessageDigest md;

    static {
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            md = null;
        }
    }

    public static synchronized String getPwdHash(String password) {
        try {
            byte[] bytesOfMessage = password.getBytes("UTF-8");
            md.reset();
            byte[] thedigest = md.digest(bytesOfMessage);
            String result = Hex.encodeHexString(thedigest);
            return result;
        } catch (IOException e) {
            return null;
        }

    }

    // Validator for validating the passwords
    public static final class PasswordValidator extends
            AbstractValidator<String> implements Validator {
        private boolean isEmptyAllowed = false;

        public PasswordValidator(boolean isEmptyAllowed) {
            super("The password provided is not valid");
            this.isEmptyAllowed = isEmptyAllowed;
        }

        @Override
        protected boolean isValidValue(String value) {
            if (isEmptyAllowed && StringUtils.isBlank(value)){
                return true;
            }
            //
            // Password must be at least 6 characters long and contain at least
            // one number
            //
            if (value != null
                    && (value.length() < 6)) {
                return false;
            }
            return true;
        }

        @Override
        public Class<String> getType() {
            return String.class;
        }
    }
}
