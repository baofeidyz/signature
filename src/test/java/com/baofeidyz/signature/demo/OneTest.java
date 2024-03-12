package com.baofeidyz.signature.demo;

import com.baofeidyz.signature.pojo.dto.SignatureDTO;
import com.baofeidyz.signature.util.ConfigUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * {@link DemoOne}的单元测试.
 *
 * @author baofeidyz
 * @since v1.0.0
 */
class OneTest {

    /**
     * 测试文件夹，位于代码的target目录下.
     */
    private static final File folderFile = new File(ConfigUtil.getUserDir() + "/target/test-result/one");

    /**
     * 清空测试文件夹.
     */
    @BeforeAll
    static void before() throws IOException {
        // 清空临时目录再跑
        if (folderFile.exists()) {
            FileUtils.deleteDirectory(folderFile);
        }
        FileUtils.forceMkdir(folderFile);
    }

    /**
     * 最简单的用例.
     */
    @Test
    void createImage() throws IOException {
        SignatureDTO signatureDTO = SignatureDTO.builder()
                .titleText("这是标题").contentText("        这是内容，在实际业务场景中，内容往往是一个段落，并且可能会有一些数字。"
                        + "段落则表示可能会出现换行的问题，数字则表示不允许跨行显示，所以在实际计算的时候，需要考虑到。")
                .build();
        this._createImage("OneTest-createImage-", signatureDTO);
    }

    /**
     * 包含了一个数字不换行显示的用例.
     */
    @Test
    void createImage2() throws IOException {
        SignatureDTO signatureDTO = SignatureDTO.builder()
                .titleText("这是标题").contentText("        这是内容，在实际业务场景中，内容往往是一个段落，并且可能会有一些数字。"
                        + "比如30000这个数字就会连在一起，而不是拆开的。段落则表示可能会出现换行的问题，数字则表示不允许跨行显示，所以在实际计算的时候，需要考虑到。")
                .build();
        this._createImage("OneTest-createImage2-", signatureDTO);
    }

    private void _createImage(String filePrefix, SignatureDTO signatureDTO) throws IOException {
        try (InputStream inputStream = new DemoOne().createImage(signatureDTO)) {
            File tempFile = File.createTempFile(filePrefix, ".png", folderFile);
            FileUtils.copyToFile(inputStream, tempFile);
            Assertions.assertTrue(tempFile.canRead() && tempFile.length() > 0L);
            System.out.println(tempFile.getAbsoluteFile());
        }
    }

}