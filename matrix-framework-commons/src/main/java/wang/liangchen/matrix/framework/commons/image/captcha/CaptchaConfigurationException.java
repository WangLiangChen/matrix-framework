package wang.liangchen.matrix.framework.commons.image.captcha;


import wang.liangchen.matrix.framework.commons.exception.MatrixRuntimeException;

/**
 * @author LiangChen.Wang 2019/7/3 16:40
 */
public class CaptchaConfigurationException extends MatrixRuntimeException {
    public CaptchaConfigurationException() {
    }

    public CaptchaConfigurationException(String message, Object... args) {
        super(message, args);
    }


    public CaptchaConfigurationException(Throwable throwable, String message, Object... args) {
        super(throwable, message, args);
    }

    public CaptchaConfigurationException(Throwable throwable) {
        super(throwable);
    }
}
