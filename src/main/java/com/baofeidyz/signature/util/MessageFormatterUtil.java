package com.baofeidyz.signature.util;

import lombok.experimental.UtilityClass;
import org.slf4j.helpers.MessageFormatter;

@UtilityClass
public class MessageFormatterUtil {

    /**
     * 替换messagePattern中的{}.
     *
     * @param messagePattern 待替换的字符串
     * @param argArray 需要替换的可变参数
     * @return 替换完成后的字符串
     */
    public String format(final String messagePattern, final Object... argArray) {
        return MessageFormatter.arrayFormat(messagePattern, argArray).getMessage();
    }

}
