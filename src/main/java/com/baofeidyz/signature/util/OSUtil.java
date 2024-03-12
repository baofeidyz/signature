package com.baofeidyz.signature.util;

import com.baofeidyz.signature.pojo.constant.OSEnum;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
public class OSUtil {

    private final String LINUX = "linux";
    private final String MAC_OS = "mac os";
    private final String MAC_OS_X = "mac os x";
    private final String WINDOWS = "windows";

    private final String OS_DESC = System.getProperty("os.name").toLowerCase();

    public boolean isMacOS() {
        return StringUtils.equalsAnyIgnoreCase(OS_DESC, MAC_OS, MAC_OS_X);
    }

    public boolean isWindows() {
        return StringUtils.isNotEmpty(OS_DESC) && OS_DESC.contains(WINDOWS);
    }

    public boolean isLinux() {
        return StringUtils.equalsAnyIgnoreCase(OS_DESC, LINUX);
    }

    public OSEnum getOS() {
        if (isLinux()) {
            return OSEnum.LINUX;
        }
        if (isWindows()) {
            return OSEnum.WINDOWS;
        }
        if (isMacOS()) {
            return OSEnum.MACOS;
        }
        throw new IllegalArgumentException(
                MessageFormatterUtil.format("当前操作系统【{}】暂不支持", OS_DESC));
    }

}
