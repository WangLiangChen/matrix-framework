package wang.liangchen.matrix.framework.commons.encryption;


import wang.liangchen.matrix.framework.commons.exception.Assert;

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
    private final Base64.Encoder encoder = Base64.getEncoder();
    private final Base64.Decoder decoder = Base64.getDecoder();

    private final Base64.Encoder urlEncoder = Base64.getUrlEncoder().withoutPadding();
    private final Base64.Decoder urlDecoder = Base64.getUrlDecoder();

    public String encode(String string) {
        return encode(string, true);
    }

    public String encode(String string, boolean urlSafe) {
        Assert.INSTANCE.notBlank(string, "string can not be blank");
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        if (urlSafe) {
            urlEncoder.encodeToString(bytes);
        }
        return encoder.encodeToString(bytes);
    }

    public String encode(byte[] bytes) {
        return encode(bytes, true);
    }

    public String encode(byte[] bytes, boolean urlSafe) {
        Assert.INSTANCE.notEmpty(bytes, "bytes can not be empty");
        if (urlSafe) {
            return urlEncoder.encodeToString(bytes);
        }
        return encoder.encodeToString(bytes);
    }

    public byte[] decode(String string) {
        return decode(string, true);
    }

    public byte[] decode(String string, boolean urlSafe) {
        Assert.INSTANCE.notBlank(string, "string can not be blank");
        if (urlSafe) {
            return urlDecoder.decode(string);
        }
        return decoder.decode(string);
    }

    public byte[] decode(byte[] bytes) {
        return decode(bytes, true);
    }

    public byte[] decode(byte[] bytes, boolean urlSafe) {
        Assert.INSTANCE.notEmpty(bytes, "bytes can not be empty");
        if (urlSafe) {
            return urlDecoder.decode(bytes);
        }
        return decoder.decode(bytes);
    }
}
