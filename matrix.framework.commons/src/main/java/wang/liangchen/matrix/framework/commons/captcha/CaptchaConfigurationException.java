package wang.liangchen.matrix.framework.commons.captcha;

import liangchen.wang.gradf.framework.commons.exception.GradfException;

/**
 * @author LiangChen.Wang 2019/7/3 16:40
 */
public class CaptchaConfigurationException extends GradfException {
    public CaptchaConfigurationException(String message, Object... args) {
        super(String.format(message.replaceAll("\\{}", "\\%s"), args));
    }

    public CaptchaConfigurationException(Throwable throwable) {
        super(throwable);
    }
}
