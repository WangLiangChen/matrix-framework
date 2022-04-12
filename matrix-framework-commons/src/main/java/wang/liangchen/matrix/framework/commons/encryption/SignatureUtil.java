package wang.liangchen.matrix.framework.commons.encryption;

import wang.liangchen.matrix.framework.commons.collection.CollectionUtil;
import wang.liangchen.matrix.framework.commons.encryption.enums.SignatureAlgorithm;
import wang.liangchen.matrix.framework.commons.exception.Assert;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.string.StringUtil;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Liangchen.Wang 2022-04-11 17:07
 */
public enum SignatureUtil {
    /**
     * instance
     */
    INSTANCE;

    public String dictionarySort(Map<String, String> source, String... excludeKeys) {
        List<String> excludeList = CollectionUtil.INSTANCE.array2List(excludeKeys);
        return source.entrySet().stream()
                .filter(e -> !excludeList.contains(e.getKey()) || StringUtil.INSTANCE.isNotBlank(e.getValue()))
                .sorted(Map.Entry.comparingByKey())
                .map(e -> String.format("%s=%s", e.getKey(), e.getValue())).collect(Collectors.joining("&"));
    }

    public String sign(SignatureAlgorithm algorithm, String privateKey, String data) {
        Assert.INSTANCE.notBlank(data, "参数data不能为空");
        Assert.INSTANCE.notBlank(privateKey, "参数privateKey不能为空");
        try {
            PrivateKey priKey = SecretKeyUtil.INSTANCE.generatePrivateKeyPKCS8(algorithm.getKeyAlgorithm(), privateKey);
            Signature signature = Signature.getInstance(algorithm.getAlgorithm());
            signature.initSign(priKey);
            signature.update(data.getBytes(StandardCharsets.UTF_8));
            byte[] signed = signature.sign();
            return Base64Util.INSTANCE.encode(signed);
        } catch (Exception e) {
            throw new MatrixErrorException("数据签名失败:{}", e.getLocalizedMessage());
        }
    }

    public boolean verify(SignatureAlgorithm algorithm, String publicKey, String data, String sign) {
        Assert.INSTANCE.notBlank(data, "参数data不能为空");
        Assert.INSTANCE.notBlank(sign, "参数sign不能为空");
        Assert.INSTANCE.notBlank(publicKey, "参数publicKey不能为空");
        try {
            PublicKey pubKey = SecretKeyUtil.INSTANCE.generatePublicKeyX509(algorithm.getKeyAlgorithm(), publicKey);
            Signature signature = Signature.getInstance(algorithm.getAlgorithm());
            signature.initVerify(pubKey);

            signature.update(data.getBytes(StandardCharsets.UTF_8));

            return signature.verify(Base64Util.INSTANCE.decode(sign));
        } catch (Exception e) {
            throw new MatrixErrorException(e, "RSA data = {},sign={}", data, sign);
        }
    }


}
