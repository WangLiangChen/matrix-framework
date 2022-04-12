package wang.liangchen.matrix.framework.commons.encryption;


import org.apache.commons.lang3.builder.HashCodeBuilder;
import wang.liangchen.matrix.framework.commons.bytes.BytesUtil;
import wang.liangchen.matrix.framework.commons.encryption.enums.DigestAlgorithm;
import wang.liangchen.matrix.framework.commons.encryption.enums.MacAligorithm;
import wang.liangchen.matrix.framework.commons.exception.Assert;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author LiangChen.Wang
 */
public enum DigestUtil {
    /**
     * INSTANCE
     */
    INSTANCE;

    public int hashCode(Object... objects) {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(objects);
        return hashCodeBuilder.hashCode();
    }

    public int hashIndex(final Object object, int indexScope) {
        Assert.INSTANCE.notNull(object, "object can not be null");
        int number = indexScope & (indexScope - 1);
        Assert.INSTANCE.isTrue(number == 0, "indexScope must be a power of 2");
        return hashCode(object) & (indexScope - 1);
    }

    public String digest(String data, DigestAlgorithm algorithm) {
        Assert.INSTANCE.notBlank(data, "data can not be blank");
        Assert.INSTANCE.notNull(algorithm, "algorithm can not be null");
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(algorithm.getAlgorithm());
        } catch (NoSuchAlgorithmException e) {
            throw new MatrixErrorException(e);
        }
        messageDigest.update(data.getBytes(StandardCharsets.UTF_8));
        return BytesUtil.INSTANCE.toHexString(messageDigest.digest());
    }


    public String mac(String key, String data, MacAligorithm aligorithm) {
        Assert.INSTANCE.notBlank(key, "key can not be blank");
        Assert.INSTANCE.notBlank(data, "data can not be blank");
        Assert.INSTANCE.notNull(aligorithm, "aligorithm can not be null");
        try {
            Mac mac = Mac.getInstance(aligorithm.getAlgorithm());
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);

            SecretKey secretKey = new SecretKeySpec(keyBytes, aligorithm.getAlgorithm());
            mac.init(secretKey);

            byte[] doFinal = mac.doFinal(dataBytes);
            return BytesUtil.INSTANCE.toHexString(doFinal);
        } catch (Exception e) {
            throw new MatrixErrorException(e);
        }
    }
}
