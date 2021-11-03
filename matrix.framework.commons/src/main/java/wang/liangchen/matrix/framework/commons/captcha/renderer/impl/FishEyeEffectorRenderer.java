package wang.liangchen.matrix.framework.commons.captcha.renderer.impl;


import liangchen.wang.gradf.framework.commons.captcha.renderer.EffectorRenderer;

import java.awt.image.BufferedImage;

/**
 * @author LiangChen.Wang 2019/7/4 16:19
 */
public class FishEyeEffectorRenderer implements EffectorRenderer {
    @Override
    public void render(BufferedImage image) {
        int imageHeight = image.getHeight();
        int imageWidth = image.getWidth();

        int[] pix = new int[imageHeight * imageWidth];
        int j = 0;

        for (int j1 = 0; j1 < imageWidth; ++j1) {
            for (int k1 = 0; k1 < imageHeight; ++k1) {
                pix[j] = image.getRGB(j1, k1);
                ++j;
            }
        }

        double distance = (double) this.ranInt(imageWidth / 4, imageWidth / 3);
        int widthMiddle = image.getWidth() / 2;
        int heightMiddle = image.getHeight() / 2;

        for (int x = 0; x < image.getWidth(); ++x) {
            for (int y = 0; y < image.getHeight(); ++y) {
                int relX = x - widthMiddle;
                int relY = y - heightMiddle;
                double d1 = Math.sqrt((double) (relX * relX + relY * relY));
                if (d1 < distance) {
                    int j2 = widthMiddle + (int) (this.fishEyeFormula(d1 / distance) * distance / d1 * (double) (x - widthMiddle));
                    int k2 = heightMiddle + (int) (this.fishEyeFormula(d1 / distance) * distance / d1 * (double) (y - heightMiddle));
                    image.setRGB(x, y, pix[j2 * imageHeight + k2]);
                }
            }
        }
    }

    private int ranInt(int i, int j) {
        double d = Math.random();
        return (int) ((double) i + (double) (j - i + 1) * d);
    }

    private double fishEyeFormula(double s) {
        if (s < 0.0D) {
            return 0.0D;
        } else {
            return s > 1.0D ? s : -0.75D * s * s * s + 1.5D * s * s + 0.25D * s;
        }
    }
}
