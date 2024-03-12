package com.baofeidyz.signature.demo;

import com.baofeidyz.signature.pojo.dto.SignatureDTO;
import com.baofeidyz.signature.util.AwtFontUtil;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import org.apache.commons.lang3.StringUtils;

/**
 * 样例一.
 * <p>样例一主要是针对固定长宽的矩形设计，适用于一些企事业单位需要在文件上方增加一个文字章效果。<br/>
 * 涉及三个部分，分别是标题、段落、签名和时间戳。其中标题设计为居中显示；段落设计为根据内容自动换行，且段落换行时有针对数字等做不换行处理；签名和时间错则位于文字章的右下角</p>
 *
 * @author baofeidyz
 * @since v1.0.0
 */
public class DemoOne implements Demo {

    private static final int imageWidth = 800;
    private static final int imageHeight = 400;
    private static final String signText = "右下角签名";
    private static final String timestampText = "2024年3月8日";
    private static final Color g2dColor = Color.RED;
    private static final double imageDPI = 300d;

    /**
     * 内边距.
     * <p>文字内容上下左右到边框的距离</p>
     * <p>0.028是 (int) Math.ceil(2/72) 计算得来，2磅/72就可以转成像素，再乘以DPI计算出图中像素大小以还原实际效果</p>
     */
    private static final float padding = Math.round(0.028d * imageDPI);
    /**
     * 需要在同一行的字符正则表达式.
     * <p>对于纯数字的情况，以及带有中英文括号的情况下，要保证这个字符不会单独换行</p>
     */
    private static final Pattern needOnSameLinePattern = Pattern.compile("\\d+|[（）()]");

    @Override
    public InputStream createImage(SignatureDTO signatureDTO) throws IOException {
        assert StringUtils.isNotEmpty(signatureDTO.getTitleText());
        assert StringUtils.isNotEmpty(signatureDTO.getContentText());

        // 这里一定要使用BufferedImage.TYPE_INT_ARGB，否则无法创建透明图片
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

        // 获取图形环境
        Graphics2D g2d = image.createGraphics();

        // 设置抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 设置透明背景
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, imageWidth, imageHeight);
        g2d.setComposite(AlphaComposite.SrcOver);

        // 设置画笔颜色
        g2d.setColor(g2dColor);

        // 设置边框
        g2d.drawRect(0, 0, imageWidth - 1, imageHeight - 1);

        // 标题
        Font titleFont = getTitleFont();
        g2d.setFont(titleFont);

        FontMetrics titleMetrics = g2d.getFontMetrics(titleFont);
        // 绘制文本且文本居中
        g2d.drawString(signatureDTO.getTitleText(),
                (imageWidth - titleMetrics.stringWidth(signatureDTO.getTitleText())) / 2, titleMetrics.getAscent());

        // 内容
        Font contentFont = getContentFont();
        g2d.setFont(contentFont);
        // 内容对应的字体属性信息
        FontMetrics contentMetrics = g2d.getFontMetrics(contentFont);
        FontRenderContext frc = g2d.getFontRenderContext();
        TextLayout layout;

        // 左右各有一些边距 更美观
        int maxContentWidth = (int) (imageWidth - padding * 2);
        // 获取当前内容区域每一行末尾的数组下标，以便于计算内容区域高度以及对字符串进行截取展示
        List<Integer> allContentLineEndIndexList =
                listContentLineEndIndex(signatureDTO.getContentText(), maxContentWidth, contentMetrics);
        // 内容区域高度
        int contentLineHeight = allContentLineEndIndexList.size() * contentMetrics.getHeight();
        // 内容区域的x坐标起点
        float contentX = padding;
        // 标题区域的高度+内容区域descent（descent是为了让标题区域和内容区域间隔开，更好看一些）
        int title2ContentLineHeight = titleMetrics.getHeight() + contentMetrics.getDescent();
        // 计算出可以容纳内容区域的高度：整体高度-2个间距-标题高度（含标题到内容的间距）-签名高度-时间戳高度-2个签名间距
        int canContainsContentHeight = imageHeight - (int) (2 * padding) - title2ContentLineHeight
                - contentMetrics.getHeight() * 2 - contentMetrics.getDescent() * 2;
        // 内容区域的y轴起点：(canContainsContentHeight - contentLineHeight) / 2f 让内容区域到标题和签名部分能Y轴居中
        float contentY = title2ContentLineHeight + (canContainsContentHeight - contentLineHeight) / 2f;
        // 开始绘制内容区域
        int lineStartIndex = 0;
        for (Integer end : allContentLineEndIndexList) {
            String line = signatureDTO.getContentText().substring(lineStartIndex, end);
            AttributedString contentAttributedString = new AttributedString(line);
            contentAttributedString.addAttribute(TextAttribute.FONT, g2d.getFont());
            layout = new TextLayout(contentAttributedString.getIterator(), frc);
            layout.draw(g2d, contentX, contentY += contentMetrics.getHeight());
            lineStartIndex = end;
        }

        // 签名固定到右下角：X轴靠右对齐
        g2d.drawString(signText, imageWidth - contentMetrics.stringWidth(signText) - padding,
                imageHeight - contentMetrics.getHeight() - contentMetrics.getDescent() - padding);
        // 时间错固定到右下角：X轴靠右对齐
        g2d.drawString(timestampText, imageWidth - contentMetrics.stringWidth(timestampText) - padding,
                imageHeight - contentMetrics.getDescent() - padding);
        g2d.dispose();
        // 因为我设定了DPI，长宽也是固定的，所以文件大小不会很大，对于一些并发不高的业务，就直接放内存了。
        // 如果你的业务并发很高，那么你应该使用IO存储，不要放内存中。其次，如果你的内容很可能重复，你也应该存起来做缓存。
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", byteArrayOutputStream);
            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        }
    }

    /**
     * 获取标题字体.
     */
    private Font getTitleFont() {
        // 设置字体和颜色，10.5磅是字体大小，这里除以72就算为了转成像素，再乘以DPI
        return new Font(AwtFontUtil.registerFont("STSong"), Font.BOLD,
                (int) Math.ceil(10.5d / 72d * imageDPI));
    }

    /**
     * 获取内容字体.
     */
    private Font getContentFont() {
        return new Font(AwtFontUtil.registerFont("STSong"), Font.PLAIN,
                (int) Math.ceil(9d / 72d * imageDPI));
    }

    /**
     * 获取当前内容区域每一行末尾的数组下标集合.
     *
     * @param contentText 内容区域需要填充的内容
     * @param contentMaxWidth 内容区域最大宽度
     * @param contentFontMetrics 内容区域字体大小
     * @return 当前内容区域每一行末尾的数组下标集合
     */
    private List<Integer> listContentLineEndIndex(String contentText, int contentMaxWidth,
            FontMetrics contentFontMetrics) {
        List<Integer> allEndIndexList = new ArrayList<>();
        int start = 0;
        String line;
        String contentTextRetains = contentText;
        int end = getContentLineEndIndex(contentText, contentMaxWidth, contentFontMetrics);
        do {
            allEndIndexList.add(end);
            line = StringUtils.substring(contentText, start, end);
            start = end;
            contentTextRetains = contentTextRetains.replace(line, StringUtils.EMPTY);
            end = start + getContentLineEndIndex(contentTextRetains, contentMaxWidth, contentFontMetrics);
        } while (StringUtils.isNotBlank(contentTextRetains));
        return allEndIndexList;
    }

    /**
     * 计算单行可以放下的文字部分的数组下标
     *
     * @param contentText 内容
     * @param maxWidth 最大宽度（图片宽度-{@link #padding} * 2）
     * @param fontMetrics 字体信息
     * @return 单行可以放下的文字部分的数组下标
     */
    private int getContentLineEndIndex(String contentText, int maxWidth, FontMetrics fontMetrics) {
        for (int i = 0; i < contentText.length(); i++) {
            if (fontMetrics.stringWidth(contentText.substring(0, i)) >= maxWidth) {
                i--;
                String substring;
                do {
                    substring = contentText.substring(i - 1, i);
                } while (needOnSameLine(substring) && i-- > 0);
                return i;
            }
        }
        return contentText.length();
    }

    private boolean needOnSameLine(String str) {
        return needOnSameLinePattern.matcher(str).find();
    }

}
