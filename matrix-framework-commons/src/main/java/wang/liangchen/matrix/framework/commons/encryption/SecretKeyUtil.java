package wang.liangchen.matrix.framework.commons.encryption;

import wang.liangchen.matrix.framework.commons.bytes.BytesUtil;
import wang.liangchen.matrix.framework.commons.encryption.enums.KeyPairAlgorithm;
import wang.liangchen.matrix.framework.commons.encryption.enums.SecureRandomAlgorithm;
import wang.liangchen.matrix.framework.commons.exception.Assert;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.*;
import java.security.spec.KeySpec;
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
    public final static String PRIVATE_KEY = "PRIVATE_KEY";
    public final static String PUBLIC_KEY = "PUBLIC_KEY";

    public PrivateKey generatePrivateKeyPKCS8(KeyPairAlgorithm algorithm, String privateKey) {
        Assert.INSTANCE.notBlank(privateKey, "privateKey can't be blank");
        byte[] privateKeyBytes = Base64Util.INSTANCE.decode(privateKey);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm.getAlgorithm());
            KeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new MatrixErrorException(e);
        }
    }

    public PublicKey generatePublicKeyX509(KeyPairAlgorithm algorithm, String publicKey) {
        Assert.INSTANCE.notBlank(publicKey, "publicKey can't be blank");
        byte[] publicKeyBytes = Base64Util.INSTANCE.decode(publicKey);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm.getAlgorithm());
            KeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new MatrixErrorException(e);
        }
    }

    public KeyPairString keyPair(KeyPairAlgorithm algorithm) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm.getAlgorithm());
            keyPairGenerator.initialize(1024);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
            byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();
            return new KeyPairString(Base64Util.INSTANCE.encode(privateKeyBytes), Base64Util.INSTANCE.encode(publicKeyBytes));
        } catch (NoSuchAlgorithmException e) {
            throw new MatrixErrorException(e);
        }
    }

    public String generateKey(String algorithm, int length, String saltkey) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance(algorithm);
            SecureRandom random = SecureRandom.getInstance(SecureRandomAlgorithm.SHA1PRNG.name());
            random.setSeed(saltkey.getBytes());
            kgen.init(length * 8, random);
            Key key = kgen.generateKey();
            byte[] bytes = key.getEncoded();
            return BytesUtil.INSTANCE.toHexString(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new MatrixErrorException(e);
        }
    }

}
