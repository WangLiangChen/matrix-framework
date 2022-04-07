package wang.liangchen.matrix.framework.commons.image.captcha.renderer.impl;



import wang.liangchen.matrix.framework.commons.image.captcha.Configurable;
import wang.liangchen.matrix.framework.commons.image.captcha.renderer.CharRenderer;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * @author LiangChen.Wang 2019/7/3 19:01
 */
public class DefaultCharRenderer extends Configurable implements CharRenderer {
    @Override
    public void render(BufferedImage image, String text) {
        int width = image.getWidth();
        int height = image.getHeight();
        int fontSize = height - 5;
        Color color = this.getProperties().getCharColor();
        Font[] fonts = this.getProperties().getCharFonts(fontSize);

        char[] chars = text.toCharArray();
        int length = chars.length;
        Font[] charFonts = new Font[length];
        int[] charWidths = new int[length];

        Graphics2D g2D = image.createGraphics();
        g2D.setColor(color);
        //微调参数 如抗锯齿
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        hints.add(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
        g2D.setRenderingHints(hints);
        g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        //计算startX
        int widthNeeded = 0;
        int startX;
        int charSpace = this.getProperties().getCharSpace();
        Random random = new Random();
        FontRenderContext frc = g2D.getFontRenderContext();
        GlyphVector gv;
        for (startX = 0; startX < length; ++startX) {
            charFonts[startX] = fonts[random.nextInt(fonts.length)];
            char[] charToDraw = new char[]{chars[startX]};
            gv = charFonts[startX].createGlyphVector(frc, charToDraw);
            charWidths[startX] = (int) gv.getVisualBounds().getWidth();
            if (startX < length - 1) {
                charWidths[startX] += charSpace;
            }
            widthNeeded += charWidths[startX];
        }
        startX = (width - widthNeeded) / 2;
        int startY = fontSize;
        //开始写字
        for (int i = 0; i < length; ++i) {
            g2D.setFont(charFonts[i]);
            char[] charToDraw = new char[]{chars[i]};
            //旋转的角度和弧度
            int angle = random.nextInt(60) - 30;
            double radian = angle * Math.PI / 180;
            g2D.rotate(radian, startX, startY);
            g2D.drawChars(charToDraw, 0, charToDraw.length, startX, startY);
            //要转回来，供下一次用
            g2D.rotate(-radian, startX, startY);
            startX = startX + charWidths[i];
        }
        g2D.dispose();
    }
}
