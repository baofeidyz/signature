package com.baofeidyz.signature.exception;

public class FontNotExistException extends SignatureException {

    public FontNotExistException(String messagePattern, Object... argArray) {
        super(messagePattern, argArray);
    }

}
