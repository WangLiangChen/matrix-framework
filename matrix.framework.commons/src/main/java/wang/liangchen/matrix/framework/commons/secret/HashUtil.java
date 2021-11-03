package wang.liangchen.matrix.framework.commons.secret;


import org.apache.commons.lang3.builder.HashCodeBuilder;
import wang.liangchen.matrix.framework.commons.exception.AssertUtil;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.utils.ByteUtil;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author LiangChen.Wang
 */
public enum HashUtil {
    /**
     * INSTANCE
     */
    INSTANCE;

    public int hashCode(Object... objects) {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(objects);
        return hashCodeBuilder.hashCode();
    }

    public int hash(Object object) {
        AssertUtil.INSTANCE.notNull(object, "object can not be null");
        int h = object.hashCode();
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }

    public int hashIndex(final Object object, int indexScope) {
        AssertUtil.INSTANCE.notNull(object, "object can not be null");
        int number = indexScope & (indexScope - 1);
        AssertUtil.INSTANCE.isTrue(number == 0, "indexScope must be a power of 2");
        return hash(object) & (indexScope - 1);
    }

    public String digest(String content, String algorithm) {
        AssertUtil.INSTANCE.notBlank(algorithm, "content can not be blank");
        AssertUtil.INSTANCE.notBlank(algorithm, "algorithm can not be blank");
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new MatrixErrorException(e);
        }
        messageDigest.update(content.getBytes(StandardCharsets.UTF_8));
        return ByteUtil.INSTANCE.toHexString(messageDigest.digest());
    }


    public String md5Digest(String string) {
        AssertUtil.INSTANCE.notBlank(string, "string can not be blank");
        return digest(string, "MD5");
    }

    public String md5Digest16(String string) {
        return md5Digest(string).substring(8, 24);
    }

    public String sha256Digest(String key, String content) {
        AssertUtil.INSTANCE.notBlank(key, "key can not be blank");
        AssertUtil.INSTANCE.notBlank(content, "content can not by blank");
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            byte[] secretByte = key.getBytes("utf-8");
            byte[] dataBytes = content.getBytes("utf-8");

            SecretKey secret = new SecretKeySpec(secretByte, "HMACSHA256");
            mac.init(secret);

            byte[] doFinal = mac.doFinal(dataBytes);
            return ByteUtil.INSTANCE.toHexString(doFinal);
        } catch (Exception e) {
            throw new MatrixErrorException(e);
        }
    }
}
