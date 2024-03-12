package com.baofeidyz.signature.util;

import com.baofeidyz.signature.exception.FontNotExistException;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
public class AwtFontUtil {

    private final Map<String, Object> lockMap = new ConcurrentHashMap<>();

    public String registerFont(String fontFileName) {
        String fontName = FilenameUtils.getBaseName(fontFileName);
        boolean alreadyRegister =
                Arrays.stream(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames())
                        .anyMatch(fontFamilyName -> StringUtils.equals(fontFamilyName, fontName));
        if (alreadyRegister) {
            return fontName;
        }
        try {
            Object object = lockMap.computeIfAbsent(fontFileName, key -> new Object());
            synchronized (object) {
                boolean secondCheckRegisterStatus =
                        Arrays.stream(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames())
                                .anyMatch(fontFamilyName -> StringUtils.equals(fontFamilyName, fontName));
                if (!secondCheckRegisterStatus) {
                    Font font = Font.createFont(Font.TRUETYPE_FONT,
                            new File(ConfigUtil.getFontFolderPath() + "/" + fontFileName));
                    GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
                }
            }
            return fontName;
        } catch (IOException | FontFormatException e) {
            throw new FontNotExistException(
                    "【{}】字体文件不存在！请检查对应字体文件夹(【{}】)下是否存在，字体文件命名需要严格修改为【{}】且需要重启服务",
                    fontFileName, ConfigUtil.getFontFolderPath(), fontFileName);
        } finally {
            lockMap.remove(fontFileName);
        }
    }

}
