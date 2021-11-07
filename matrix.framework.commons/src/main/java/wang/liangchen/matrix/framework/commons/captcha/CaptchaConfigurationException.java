package wang.liangchen.matrix.framework.commons.captcha;


import wang.liangchen.matrix.framework.commons.exception.MatrixRuntimeException;

/**
 * @author LiangChen.Wang 2019/7/3 16:40
 */
public class CaptchaConfigurationException extends MatrixRuntimeException {
    public CaptchaConfigurationException(String message, Object... args) {
        super(String.format(message.replaceAll("\\{}", "\\%s"), args));
    }

    public CaptchaConfigurationException(Throwable throwable) {
        super(throwable);
    }
}
