package wang.liangchen.matrix.framework.commons.secret;


import wang.liangchen.matrix.framework.commons.exception.AssertUtil;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author LiangChen.Wang 2020/9/11
 */
public enum Base64Util {
    /**
     *
     */
    INSTANCE;
    private final Base64.Decoder base64Decoder = Base64.getDecoder();
    private final Base64.Encoder base64Encoder = Base64.getEncoder();

    public byte[] decode(String string) {
        AssertUtil.INSTANCE.notBlank(string, "string can not be blank");
        return base64Decoder.decode(string);
    }

    public byte[] decode(byte[] bytes) {
        AssertUtil.INSTANCE.notEmpty(bytes, "bytes can not be empty");
        return base64Decoder.decode(bytes);
    }

    public String encode(byte[] bytes) {
        AssertUtil.INSTANCE.notEmpty(bytes, "bytes can not be empty");
        return base64Encoder.encodeToString(bytes);
    }

    public String encode(String string, Charset charset) {
        AssertUtil.INSTANCE.notBlank(string, "string can not be blank");
        if (null == charset) {
            charset = StandardCharsets.UTF_8;
        }
        byte[] bytes = string.getBytes(charset);
        return base64Encoder.encodeToString(bytes);
    }

    public String encode(String string) {
        AssertUtil.INSTANCE.notBlank(string, "string can not be blank");
        return encode(string, StandardCharsets.UTF_8);
    }
}
