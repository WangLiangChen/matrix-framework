package wang.liangchen.matrix.framework.commons.encryption;

import wang.liangchen.matrix.framework.commons.CollectionUtil;
import wang.liangchen.matrix.framework.commons.StringUtil;
import wang.liangchen.matrix.framework.commons.encryption.enums.DigestAlgorithm;
import wang.liangchen.matrix.framework.commons.encryption.enums.HmacAlgorithm;
import wang.liangchen.matrix.framework.commons.encryption.enums.SignatureAlgorithm;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.exception.MatrixExceptionLevel;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
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

    public String hmac(HmacAlgorithm aligorithm, String secretKeyString, String dataString) {
        ValidationUtil.INSTANCE.notBlank(MatrixExceptionLevel.WARN, secretKeyString, "secretKeyString must not be blank");
        ValidationUtil.INSTANCE.notBlank(MatrixExceptionLevel.WARN, dataString, "dataString must not be blank");
        byte[] secretKeyBytes = secretKeyString.getBytes(StandardCharsets.UTF_8);
        byte[] dataBytes = dataString.getBytes(StandardCharsets.UTF_8);
        return hmac(aligorithm, secretKeyBytes, dataBytes);
    }

    public String hmac(HmacAlgorithm aligorithm, byte[] secretKeyBytes, byte[] dataBytes) {
        ValidationUtil.INSTANCE.notEmpty(MatrixExceptionLevel.WARN, secretKeyBytes, "secretKeyBytes must not be empty");
        ValidationUtil.INSTANCE.notEmpty(MatrixExceptionLevel.WARN, dataBytes, "dataBytes must not be empty");
        try {
            Mac mac = Mac.getInstance(aligorithm.name());
            SecretKey secretKey = new SecretKeySpec(secretKeyBytes, aligorithm.name());
            mac.init(secretKey);
            byte[] bytes = mac.doFinal(dataBytes);
            return Base64Util.INSTANCE.encode(bytes);
        } catch (Exception e) {
            throw new MatrixErrorException(e);
        }
    }


    public String digest(DigestAlgorithm algorithm, String dataString) {
        ValidationUtil.INSTANCE.notBlank(MatrixExceptionLevel.WARN, dataString, "data must not be blank");
        byte[] dataBytes = dataString.getBytes(StandardCharsets.UTF_8);
        return digest(algorithm, dataBytes);
    }

    public String digest(DigestAlgorithm algorithm, byte[] dataBytes) {
        ValidationUtil.INSTANCE.notEmpty(MatrixExceptionLevel.WARN, dataBytes, "dataBytes must not be empty");
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(algorithm.getAlgorithm());
        } catch (NoSuchAlgorithmException e) {
            throw new MatrixErrorException(e);
        }
        messageDigest.update(dataBytes);
        byte[] bytes = messageDigest.digest();
        return Base64Util.INSTANCE.encode(bytes);
    }

    public String sign(SignatureAlgorithm algorithm, String privateKeyString, String data) {
        ValidationUtil.INSTANCE.notBlank(MatrixExceptionLevel.WARN, privateKeyString, "privateKey must not be blank");
        ValidationUtil.INSTANCE.notBlank(MatrixExceptionLevel.WARN, data, "data must not be blank");
        try {
            PrivateKey priKey = SecretKeyUtil.INSTANCE.generatePrivateKeyPKCS8(algorithm.getKeyPairAlgorithm(), privateKeyString);
            Signature signature = Signature.getInstance(algorithm.name());
            signature.initSign(priKey);
            signature.update(data.getBytes(StandardCharsets.UTF_8));
            byte[] signed = signature.sign();
            return Base64Util.INSTANCE.encode(signed);
        } catch (Exception e) {
            throw new MatrixErrorException("数据签名失败:{}", e.getLocalizedMessage());
        }
    }

    public boolean verify(SignatureAlgorithm algorithm, String publicKeyString, String signatureString, String data) {
        ValidationUtil.INSTANCE.notBlank(MatrixExceptionLevel.WARN, publicKeyString, "publicKey must not be blank");
        ValidationUtil.INSTANCE.notBlank(MatrixExceptionLevel.WARN, data, "data must not be blank");
        ValidationUtil.INSTANCE.notBlank(MatrixExceptionLevel.WARN, signatureString, "signatureString must not be blank");

        try {
            PublicKey pubKey = SecretKeyUtil.INSTANCE.generatePublicKeyX509(algorithm.getKeyPairAlgorithm(), publicKeyString);
            Signature signature = Signature.getInstance(algorithm.name());
            signature.initVerify(pubKey);
            signature.update(data.getBytes(StandardCharsets.UTF_8));
            return signature.verify(Base64Util.INSTANCE.decode(signatureString));
        } catch (Exception e) {
            throw new MatrixErrorException(e, "RSA data = {},sign={}", data, signatureString);
        }
    }

    public int hashIndex(Object object, int indexScope) {
        ValidationUtil.INSTANCE.notNull(MatrixExceptionLevel.WARN, object, "object must not be null");
        int number = indexScope & (indexScope - 1);
        ValidationUtil.INSTANCE.isTrue(MatrixExceptionLevel.WARN, number == 0, "indexScope must be a power of 2");
        return Objects.hashCode(object) & (indexScope - 1);
    }

    public String dictionarySort(Map<String, String> source, String... excludeKeys) {
        List<String> excludeList = CollectionUtil.INSTANCE.array2List(excludeKeys);
        return source.entrySet().stream()
                .filter(e -> !excludeList.contains(e.getKey()) || StringUtil.INSTANCE.isNotEmpty(e.getValue()))
                .sorted(Map.Entry.comparingByKey())
                .map(e -> String.format("%s=%s", e.getKey(), e.getValue())).collect(Collectors.joining("&"));
    }
}
