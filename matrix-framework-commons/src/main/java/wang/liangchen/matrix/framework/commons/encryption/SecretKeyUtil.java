package wang.liangchen.matrix.framework.commons.encryption;

import wang.liangchen.matrix.framework.commons.encryption.enums.KeyAlgorithm;
import wang.liangchen.matrix.framework.commons.exception.Assert;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author Liangchen.Wang 2022-04-12 10:14
 */
public enum SecretKeyUtil {
    /**
     * instance
     */
    INSTANCE;

    public PrivateKey generatePrivateKeyPKCS8(KeyAlgorithm algorithm, String privateKey) {
        Assert.INSTANCE.notNull(algorithm, "algorithm can't be null");
        Assert.INSTANCE.notBlank(privateKey, "privateKey can't be blank");
        byte[] privateKeyBytes = Base64Util.INSTANCE.decode(privateKey);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm.getAlgorithm());
            return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
        } catch (Exception e) {
            throw new MatrixErrorException(e);
        }
    }

    public PublicKey generatePublicKeyX509(KeyAlgorithm algorithm, String publicKey) {
        Assert.INSTANCE.notNull(algorithm, "algorithm can't be null");
        Assert.INSTANCE.notBlank(publicKey, "publicKey can't be blank");
        byte[] publicKeyBytes = Base64Util.INSTANCE.decode(publicKey);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm.getAlgorithm());
            return keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
        } catch (Exception e) {
            throw new MatrixErrorException(e);
        }
    }
}
