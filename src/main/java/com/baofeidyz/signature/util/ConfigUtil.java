package com.baofeidyz.signature.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ConfigUtil {

    public String getFontFolderPath() {
        return switch (OSUtil.getOS()) {
            case LINUX -> "/usr/share/fonts";
            case WINDOWS -> MessageFormatterUtil.format("{}\\AppData\\Local\\Microsoft\\Windows\\Fonts",
                    getUserHome());
            case MACOS -> MessageFormatterUtil.format("{}/Library/Fonts", getUserHome());
        };
    }

    public String getUserHome() {
        return System.getProperty("user.home");
    }

    public String getUserDir() {
        return System.getProperty("user.dir");
    }

}
