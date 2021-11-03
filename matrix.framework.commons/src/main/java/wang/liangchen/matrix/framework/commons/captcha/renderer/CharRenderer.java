package wang.liangchen.matrix.framework.commons.captcha.renderer;

import java.awt.image.BufferedImage;

/**
 * @author LiangChen.Wang 2019/7/3 18:53
 * 字符渲染器
 */
public interface CharRenderer {
    void render(BufferedImage image, String text);
}
