package wang.liangchen.matrix.framework.commons.image.captcha;

import wang.liangchen.matrix.framework.commons.image.captcha.producer.CharProducer;
import wang.liangchen.matrix.framework.commons.image.captcha.producer.impl.DefaultCharProducer;
import wang.liangchen.matrix.framework.commons.image.captcha.renderer.CharRenderer;
import wang.liangchen.matrix.framework.commons.image.captcha.renderer.EffectorRenderer;
import wang.liangchen.matrix.framework.commons.image.captcha.renderer.NoiseRenderer;
import wang.liangchen.matrix.framework.commons.image.captcha.renderer.impl.DefaultCharRenderer;
import wang.liangchen.matrix.framework.commons.image.captcha.renderer.impl.DefaultNoiseRenderer;
import wang.liangchen.matrix.framework.commons.image.captcha.renderer.impl.FishEyeEffectorRenderer;
import wang.liangchen.matrix.framework.commons.type.ClassUtil;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.Properties;

/**
 * @author LiangChen.Wang 2019/7/3 14:25
 */
public class CaptchaProperties extends Properties {
    private static final String DEFAULTCHARS = "abcdefghjkmnpqrstuvwxyz23456789ABCDEFGHJKMNPQRSTUVWXYZ";
    //系统字体名称
    private static final GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
    private static final String[] systemFontNames = e.getAvailableFontFamilyNames();

    public static CaptchaProperties newInstance() {
        return new CaptchaProperties();
    }

    //--字符相关配置-开始
    public char[] getCharString() {
        String paramName = "char.string";
        String paramValue = this.getProperty(paramName);
        return getChars(paramName, paramValue, DEFAULTCHARS.toCharArray());
    }

    public int getCharSpace() {
        String paramName = "char.space";
        String paramValue = this.getProperty(paramName);
        return getPositiveInt(paramName, paramValue, 5);
    }

    public Color getCharColor() {
        String paramName = "char.color";
        String paramValue = this.getProperty(paramName);
        return getColor(paramName, paramValue, Color.BLACK);
    }

    public Font[] getCharFonts(int fontSize) {
        String paramName = "char.fonts";
        String paramValue = this.getProperty(paramName);
        return this.getFonts(paramName, paramValue, fontSize, new Font[]{new Font("Arial", 1, fontSize), new Font("Microsoft YaHei", 1, fontSize)});
    }

    public CharProducer getCharProducer() {
        String paramName = "char.producer";
        String paramValue = this.getProperty(paramName);
        return (CharProducer) getClassInstance(paramName, paramValue, new DefaultCharProducer());
    }

    public CharRenderer getCharRenderer() {
        String paramName = "char.renderer";
        String paramValue = this.getProperty(paramName);
        return (CharRenderer) getClassInstance(paramName, paramValue, new DefaultCharRenderer());
    }
    //--字符相关配置结束

    //--背景相关配置开始
    //--背景相关配置结束

    //--边框相关配置开始
    //--边框相关配置结束

    //--效果相关配置开始
    public EffectorRenderer getEffectorRenderer() {
        String paramName = "effector.renderer";
        String paramValue = this.getProperty(paramName);
        return (EffectorRenderer) getClassInstance(paramName, paramValue, new FishEyeEffectorRenderer());
    }
    //--效果相关配置结束

    //--噪点/干扰线相关配置开始
    public Color getNoiseColor() {
        String paramName = "noise.color";
        String paramValue = this.getProperty(paramName);
        return getColor(paramName, paramValue, Color.BLACK);
    }

    public NoiseRenderer getNoiseRenderer() {
        String paramName = "noise.renderer";
        String paramValue = this.getProperty(paramName);
        return (NoiseRenderer) getClassInstance(paramName, paramValue, new DefaultNoiseRenderer());
    }
    //--噪点/干扰线相关配置结束

    private int getPositiveInt(String paramName, String paramValue, int defaultInt) {
        if (paramValue == null || paramValue.length() == 0) {
            return defaultInt;
        }
        try {
            int intValue = Integer.parseInt(paramValue);
            if (intValue < 1) {
                throw new CaptchaConfigurationException("配置项{}取值必须大于等于1", paramName);
            }
            return intValue;
        } catch (NumberFormatException e) {
            throw new CaptchaConfigurationException(e);
        }
    }

    private char[] getChars(String paramName, String paramValue, char[] defaultChars) {
        if (paramValue == null || paramValue.length() == 0) {
            return defaultChars;
        }
        return paramValue.toCharArray();
    }

    public Object getClassInstance(String paramName, String paramValue, Object defaultInstance) {
        Object instance;
        if (null == paramValue || paramValue.length() == 0) {
            instance = defaultInstance;
        } else {
            try {
                instance = ClassUtil.INSTANCE.instantiate(paramValue);
            } catch (Exception e) {
                throw new CaptchaConfigurationException(e);
            }
        }
        if (instance instanceof Configurable) {
            ((Configurable) instance).setProperties(this);
        }
        return instance;
    }

    private Color getColor(String paramName, String paramValue, Color defaultColor) {
        if (paramValue == null || paramValue.length() == 0) {
            return defaultColor;
        }
        if (paramValue.indexOf(",") > 0) {
            return createColorFromCommaSeparatedValues(paramName, paramValue);
        } else {
            return this.createColorFromFieldValue(paramName, paramValue);
        }
    }

    private Color createColorFromCommaSeparatedValues(String paramName, String paramValue) {
        String[] colorValues = paramValue.split(",");
        try {
            int r = Integer.parseInt(colorValues[0]);
            int g = Integer.parseInt(colorValues[1]);
            int b = Integer.parseInt(colorValues[2]);
            if (colorValues.length == 3) {
                return new Color(r, g, b);
            } else if (colorValues.length == 4) {
                int a = Integer.parseInt(colorValues[3]);
                return new Color(r, g, b, a);
            } else {
                throw new CaptchaConfigurationException("配置项{}的取值错误，值:{}", paramName, paramValue);
            }
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            throw new CaptchaConfigurationException(e);
        }
    }

    private Color createColorFromFieldValue(String paramName, String paramValue) {
        try {
            Field field = Class.forName("java.awt.Color").getField(paramValue);
            return (Color) field.get(null);
        } catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException e) {
            throw new CaptchaConfigurationException(e);
        }
    }

    private Font[] getFonts(String paramName, String paramValue, int fontSize, Font[] defaultFonts) {
        if (paramValue == null || paramValue.length() == 0) {
            return defaultFonts;
        }
        String[] fontNames = paramValue.split(",");
        Font[] fonts = new Font[fontNames.length];
        for (int i = 0; i < fontNames.length; ++i) {
            fonts[i] = new Font(fontNames[i], 1, fontSize);
        }
        return fonts;
    }

}
