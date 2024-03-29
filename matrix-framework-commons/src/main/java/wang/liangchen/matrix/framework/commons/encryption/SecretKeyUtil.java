package wang.liangchen.matrix.framework.commons.encryption;

import wang.liangchen.matrix.framework.commons.encryption.enums.KeyAlgorithm;
import wang.liangchen.matrix.framework.commons.encryption.enums.KeyPairAlgorithm;
import wang.liangchen.matrix.framework.commons.encryption.enums.SecureRandomAlgorithm;
import wang.liangchen.matrix.framework.commons.exception.ExceptionLevel;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

import javax.crypto.KeyGenerator;
import java.nio.charset.StandardCharsets;
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

    public PrivateKey generatePrivateKeyPKCS8(KeyPairAlgorithm algorithm, String privateKeyString) {
        ValidationUtil.INSTANCE.notBlank(ExceptionLevel.WARN,privateKeyString, "privateKey must not be blank");
        byte[] privateKeyBytes = Base64Util.INSTANCE.decode(privateKeyString);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm.name());
            KeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new MatrixErrorException(e);
        }
    }

    public PublicKey generatePublicKeyX509(KeyPairAlgorithm algorithm, String publicKeyString) {
        ValidationUtil.INSTANCE.notBlank(ExceptionLevel.WARN,publicKeyString, "publicKey must not be blank");
        byte[] publicKeyBytes = Base64Util.INSTANCE.decode(publicKeyString);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm.name());
            KeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new MatrixErrorException(e);
        }
    }

    public KeyPairString keyPair(KeyPairAlgorithm algorithm) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm.name());
            keyPairGenerator.initialize(1024);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
            byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();
            return new KeyPairString(Base64Util.INSTANCE.encode(privateKeyBytes), Base64Util.INSTANCE.encode(publicKeyBytes));
        } catch (NoSuchAlgorithmException e) {
            throw new MatrixErrorException(e);
        }
    }

    public String keyGenerator(KeyAlgorithm keyAlgorithm, int bit, String saltkey) {
        // AES 128, 192 or 256
        // DES 56
        // DESede 112 168
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(keyAlgorithm.name());
            SecureRandom random = SecureRandom.getInstance(SecureRandomAlgorithm.SHA1PRNG.name());
            random.setSeed(saltkey.getBytes(StandardCharsets.UTF_8));
            keyGenerator.init(bit, random);
            Key key = keyGenerator.generateKey();
            byte[] bytes = key.getEncoded();
            return Base64Util.INSTANCE.encode(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new MatrixErrorException(e);
        }
    }

}
