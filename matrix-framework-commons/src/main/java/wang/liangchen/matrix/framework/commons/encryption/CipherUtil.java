package wang.liangchen.matrix.framework.commons.encryption;

import wang.liangchen.matrix.framework.commons.bytes.BytesUtil;
import wang.liangchen.matrix.framework.commons.encryption.enums.CipherAsymmetricAlgorithm;
import wang.liangchen.matrix.framework.commons.encryption.enums.CipherSymmetricAlgorithm;
import wang.liangchen.matrix.framework.commons.encryption.enums.KeyAlgorithm;
import wang.liangchen.matrix.framework.commons.exception.Assert;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author Liangchen.Wang 2022-04-11 17:04
 */
public enum CipherUtil {
    /**
     * instance
     */
    INSTANCE;

    public String encrypt(CipherSymmetricAlgorithm algorithm, String key, String data) {
        Assert.INSTANCE.notBlank(key, "key can not be blank");
        return encrypt(algorithm, key.getBytes(StandardCharsets.UTF_8), data);
    }

    public String encrypt(CipherSymmetricAlgorithm algorithm, byte[] keyBytes, String data) {
        Assert.INSTANCE.notEmpty(keyBytes, "keyBytes can not be blank");
        Assert.INSTANCE.notBlank(data, "data can not be blank");
        validateKeyLength(algorithm, keyBytes);
        try {
            Cipher cipher = Cipher.getInstance(algorithm.getAlgorithm());
            Key secretKey = new SecretKeySpec(keyBytes, algorithm.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] bytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            String encrypt = Base64Util.INSTANCE.encode(bytes);
            return encrypt;
        } catch (Exception e) {
            throw new MatrixErrorException(e);
        }
    }

    public String decrypt(CipherSymmetricAlgorithm algorithm, String key, String data) {
        Assert.INSTANCE.notBlank(key, "key can not be blank");
        return decrypt(algorithm, key.getBytes(StandardCharsets.UTF_8), data);
    }

    public String decrypt(CipherSymmetricAlgorithm algorithm, byte[] keyBytes, String data) {
        Assert.INSTANCE.notEmpty(keyBytes, "keyBytes can not be blank");
        Assert.INSTANCE.notBlank(data, "data can not be blank");
        validateKeyLength(algorithm, keyBytes);
        try {
            Cipher cipher = Cipher.getInstance(algorithm.getAlgorithm());
            Key secretKey = new SecretKeySpec(keyBytes, algorithm.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] bytes = cipher.doFinal(Base64Util.INSTANCE.decode(data));
            String decrypt = BytesUtil.INSTANCE.toString(bytes);
            return decrypt;
        } catch (Exception e) {
            throw new MatrixErrorException(e);
        }
    }

    private void validateKeyLength(CipherSymmetricAlgorithm algorithm, byte[] keyBytes) {
        // AES 128, 192 or 256
        // DES 56
        // DESede 112 168
        int bit = keyBytes.length;
        KeyAlgorithm keyAlgorithm = algorithm.getKeyAlgorithm();
        switch (keyAlgorithm) {
            case AES:
                if (bit != 16 && bit != 24 && bit != 32) {
                    throw new MatrixErrorException("keyAlgorithm:'{}',size must be equal to 16, 24 or 32", keyAlgorithm);
                }
                break;
            case DES:
                if (bit != 8) {
                    throw new MatrixErrorException("keyAlgorithm:'{}',size must be equal to 8", keyAlgorithm);
                }
                break;
            case DESede:
                if (bit != 24) {
                    throw new MatrixErrorException("keyAlgorithm:'{}',size must be equal to 24", keyAlgorithm);
                }
                break;
            default:
                break;
        }
    }

    public String encrypt(CipherAsymmetricAlgorithm algorithm, String publicKey, String data) {
        Assert.INSTANCE.notBlank(publicKey, "publicKey can not be blank");
        Assert.INSTANCE.notBlank(data, "data can not be blank");
        try {
            Cipher cipher = Cipher.getInstance(algorithm.getTransformation());
            PublicKey pubKey = SecretKeyUtil.INSTANCE.generatePublicKeyX509(algorithm.getKeyPairAlgorithm(), publicKey);
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            byte[] encryptBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64Util.INSTANCE.encode(encryptBytes);
        } catch (Exception e) {
            throw new MatrixErrorException(e);
        }
    }

    public String decrypt(CipherAsymmetricAlgorithm algorithm, String privateKey, String data) {
        Assert.INSTANCE.notBlank(privateKey, "privateKey can not be blank");
        Assert.INSTANCE.notBlank(data, "data can not be blank");
        try {
            Cipher cipher = Cipher.getInstance(algorithm.getTransformation());
            PrivateKey priKey = SecretKeyUtil.INSTANCE.generatePrivateKeyPKCS8(algorithm.getKeyPairAlgorithm(), privateKey);
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            // 对密文base64解密
            byte[] ciphertextBytes = Base64Util.INSTANCE.decode(data);
            byte[] decryptBytes = cipher.doFinal(ciphertextBytes);
            return new String(decryptBytes);
        } catch (Exception e) {
            throw new MatrixErrorException(e);
        }
    }
}
