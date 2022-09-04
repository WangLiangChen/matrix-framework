package wang.liangchen.matrix.framework.commons.encryption;

import wang.liangchen.matrix.framework.commons.collection.CollectionUtil;
import wang.liangchen.matrix.framework.commons.encryption.enums.DigestAlgorithm;
import wang.liangchen.matrix.framework.commons.encryption.enums.HmacAligorithm;
import wang.liangchen.matrix.framework.commons.encryption.enums.SignatureAlgorithm;
import wang.liangchen.matrix.framework.commons.exception.Assert;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.string.StringUtil;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Signature;
import java.security.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Liangchen.Wang 2022-04-11 17:07
 */
public enum DigestSignUtil {
    /**
     * instance
     */
    INSTANCE;

    public String hmac(HmacAligorithm aligorithm, String secretKeyString, String data) {
        Assert.INSTANCE.notBlank(secretKeyString, "secretKeyString can not be blank");
        Assert.INSTANCE.notBlank(data, "data can not be blank");
        byte[] keyBytes = secretKeyString.getBytes(StandardCharsets.UTF_8);
        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
        try {
            Mac mac = Mac.getInstance(aligorithm.getAlgorithm());
            SecretKey secretKey = new SecretKeySpec(keyBytes, aligorithm.getAlgorithm());
            mac.init(secretKey);
            byte[] bytes = mac.doFinal(dataBytes);
            return Base64Util.INSTANCE.encode(bytes);
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
        byte[] bytes = messageDigest.digest();
        return Base64Util.INSTANCE.encode(bytes);
    }

    public String sign(SignatureAlgorithm algorithm, String privateKeyString, String data) {
        Assert.INSTANCE.notBlank(privateKeyString, "privateKey can not be blank");
        Assert.INSTANCE.notBlank(data, "data can not be blank");
        try {
            PrivateKey priKey = SecretKeyUtil.INSTANCE.generatePrivateKeyPKCS8(algorithm.getKeyPairAlgorithm(), privateKeyString);
            Signature signature = Signature.getInstance(algorithm.getAlgorithm());
            signature.initSign(priKey);
            signature.update(data.getBytes(StandardCharsets.UTF_8));
            byte[] signed = signature.sign();
            return Base64Util.INSTANCE.encode(signed);
        } catch (Exception e) {
            throw new MatrixErrorException("数据签名失败:{}", e.getLocalizedMessage());
        }
    }

    public boolean verify(SignatureAlgorithm algorithm, String publicKeyString, String signatureString, String data) {
        Assert.INSTANCE.notBlank(publicKeyString, "publicKey can not be blank");
        Assert.INSTANCE.notBlank(data, "data can not be blank");
        Assert.INSTANCE.notBlank(signatureString, "signatureString can not be blank");

        try {
            PublicKey pubKey = SecretKeyUtil.INSTANCE.generatePublicKeyX509(algorithm.getKeyPairAlgorithm(), publicKeyString);
            Signature signature = Signature.getInstance(algorithm.getAlgorithm());
            signature.initVerify(pubKey);
            signature.update(data.getBytes(StandardCharsets.UTF_8));
            return signature.verify(Base64Util.INSTANCE.decode(signatureString));
        } catch (Exception e) {
            throw new MatrixErrorException(e, "RSA data = {},sign={}", data, signatureString);
        }
    }

    public int hashIndex(Object object, int indexScope) {
        Assert.INSTANCE.notNull(object, "object can not be null");
        int number = indexScope & (indexScope - 1);
        Assert.INSTANCE.isTrue(number == 0, "indexScope must be a power of 2");
        return Objects.hashCode(object) & (indexScope - 1);
    }

    public String dictionarySort(Map<String, String> source, String... excludeKeys) {
        List<String> excludeList = CollectionUtil.INSTANCE.array2List(excludeKeys);
        return source.entrySet().stream()
                .filter(e -> !excludeList.contains(e.getKey()) || StringUtil.INSTANCE.isNotBlank(e.getValue()))
                .sorted(Map.Entry.comparingByKey())
                .map(e -> String.format("%s=%s", e.getKey(), e.getValue())).collect(Collectors.joining("&"));
    }
}
