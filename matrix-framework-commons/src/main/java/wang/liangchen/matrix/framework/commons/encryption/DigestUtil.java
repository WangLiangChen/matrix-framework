package wang.liangchen.matrix.framework.commons.encryption;


import wang.liangchen.matrix.framework.commons.bytes.BytesUtil;
import wang.liangchen.matrix.framework.commons.encryption.enums.DigestAlgorithm;
import wang.liangchen.matrix.framework.commons.encryption.enums.HmacAligorithm;
import wang.liangchen.matrix.framework.commons.exception.Assert;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * @author LiangChen.Wang
 */
public enum DigestUtil {
    /**
     * INSTANCE
     */
    INSTANCE;

    public String hmac(HmacAligorithm aligorithm, String key, String data) {
        Assert.INSTANCE.notBlank(key, "key can not be blank");
        Assert.INSTANCE.notBlank(data, "data can not be blank");
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
        try {
            Mac mac = Mac.getInstance(aligorithm.getAlgorithm());
            SecretKey secretKey = new SecretKeySpec(keyBytes, aligorithm.getAlgorithm());
            mac.init(secretKey);
            byte[] bytes = mac.doFinal(dataBytes);
            return BytesUtil.INSTANCE.toHexString(bytes);
        } catch (Exception e) {
            throw new MatrixErrorException(e);
        }
    }

    public String digest(DigestAlgorithm algorithm, String data) {
        Assert.INSTANCE.notBlank(data, "data can not be blank");
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(algorithm.getAlgorithm());
        } catch (NoSuchAlgorithmException e) {
            throw new MatrixErrorException(e);
        }
        messageDigest.update(data.getBytes(StandardCharsets.UTF_8));
        return BytesUtil.INSTANCE.toHexString(messageDigest.digest());
    }


    public int hashIndex(Object object, int indexScope) {
        Assert.INSTANCE.notNull(object, "object can not be null");
        int number = indexScope & (indexScope - 1);
        Assert.INSTANCE.isTrue(number == 0, "indexScope must be a power of 2");
        return Objects.hashCode(object) & (indexScope - 1);
    }
}
