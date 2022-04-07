package wang.liangchen.matrix.framework.commons.image.captcha.renderer;

import java.awt.image.BufferedImage;

/**
 * @author LiangChen.Wang 2019/7/3 18:53
 * 背景渲染器
 */
public interface BackgroundRenderer {
    void render(BufferedImage image);
}
