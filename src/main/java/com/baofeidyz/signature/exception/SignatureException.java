package com.baofeidyz.signature.exception;

import com.baofeidyz.signature.util.MessageFormatterUtil;

public class SignatureException extends RuntimeException {

    public SignatureException(final String messagePattern, final Object... argArray) {
        super(MessageFormatterUtil.format(messagePattern, argArray));
    }

}
