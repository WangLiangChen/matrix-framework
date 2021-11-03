package wang.liangchen.matrix.framework.commons.captcha.renderer.impl;


import liangchen.wang.gradf.framework.commons.captcha.Configurable;
import liangchen.wang.gradf.framework.commons.captcha.renderer.NoiseRenderer;

import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * @author LiangChen.Wang 2019/7/4 16:37
 * 噪点渲染器
 */
public class DefaultNoiseRenderer extends Configurable implements NoiseRenderer {

    @Override
    public void render(BufferedImage image) {
        Color color = this.getProperties().getNoiseColor();
        Random random = new Random();
        float factorOne = random.nextFloat();
        float factorTwo = random.nextFloat();
        float factorThree = random.nextFloat();
        float factorFour = random.nextFloat();


        int width = image.getWidth();
        int height = image.getHeight();
        Point2D[] pts;

        CubicCurve2D cc = new CubicCurve2D.Float((float) width * factorOne, (float) height * random.nextFloat(), (float) width * factorTwo, (float) height * random.nextFloat(), (float) width * factorThree, (float) height * random.nextFloat(), (float) width * factorFour, (float) height * random.nextFloat());
        PathIterator pi = cc.getPathIterator(null, 2.0D);
        Point2D[] tmp = new Point2D[200];
        int i = 0;

        while (!pi.isDone()) {
            float[] coords = new float[6];
            switch (pi.currentSegment(coords)) {
                case 0:
                case 1:
                    tmp[i] = new Point2D.Float(coords[0], coords[1]);
                default:
                    ++i;
                    pi.next();
                    break;
            }
        }

        pts = new Point2D[i];
        System.arraycopy(tmp, 0, pts, 0, i);
        Graphics2D graph = (Graphics2D) image.getGraphics();
        graph.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
        graph.setColor(color);

        for (i = 0; i < pts.length - 1; ++i) {
            if (i < 3) {
                graph.setStroke(new BasicStroke(0.9F * (float) (4 - i)));
            }
            graph.drawLine((int) pts[i].getX(), (int) pts[i].getY(), (int) pts[i + 1].getX(), (int) pts[i + 1].getY());
        }
        graph.dispose();
    }
}
