package wang.liangchen.matrix.framework.commons.image.captcha.producer.impl;


import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.image.captcha.Configurable;
import wang.liangchen.matrix.framework.commons.image.captcha.producer.IProducer;
import wang.liangchen.matrix.framework.commons.image.captcha.renderer.CharRenderer;
import wang.liangchen.matrix.framework.commons.image.captcha.renderer.EffectorRenderer;
import wang.liangchen.matrix.framework.commons.image.captcha.renderer.NoiseRenderer;
import wang.liangchen.matrix.framework.commons.io.ByteOutputStream;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Base64;

/**
 * @author LiangChen.Wang 2019/7/3 16:10
 */
public class DefaultProducer extends Configurable implements IProducer {
    @Override
    public String createText(int length) {
        return this.getProperties().getCharProducer().getText(length);
    }

    @Override
    public BufferedImage createImage(int width, int height, String text) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        CharRenderer charRenderer = this.getProperties().getCharRenderer();
        charRenderer.render(image, text);
        EffectorRenderer effectorRenderer = this.getProperties().getEffectorRenderer();
        effectorRenderer.render(image);
        NoiseRenderer noiseRenderer = this.getProperties().getNoiseRenderer();
        noiseRenderer.render(image);
        noiseRenderer.render(image);
        return image;
    }

    @Override
    public ByteOutputStream createOutStream(int width, int height, String formatName, String text) {
        ByteOutputStream outputStream = new ByteOutputStream();
        BufferedImage bufferedImage = createImage(width, height, text);
        try {
            ImageIO.write(bufferedImage, formatName, outputStream);
        } catch (IOException e) {
            throw new MatrixErrorException(e);
        }
        return outputStream;
    }

    @Override
    public String createBase64(int width, int height, String formatName, String text) {
        ByteOutputStream byteOutputStream = createOutStream(width, height, formatName, text);
        String base64 = Base64.getEncoder().encodeToString(byteOutputStream.getBytes());
        base64 = String.format("data:image/%s;base64,%s", formatName, base64);
        return base64;
    }
}
