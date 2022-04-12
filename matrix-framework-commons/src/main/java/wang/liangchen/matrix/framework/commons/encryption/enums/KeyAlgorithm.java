package wang.liangchen.matrix.framework.commons.encryption.enums;

/**
 * @author Liangchen.Wang 2022-04-12 9:22
 */
public enum KeyAlgorithm {
    /**
     * Keys for the Digital Signature Algorithm.
     */
    DSA("DSA"),
    /**
     * Keys for the RSA algorithm (Signature/Cipher).
     */
    RSA("RSA"),
    /**
     * Keys for the Diffie-Hellman KeyAgreement algorithm.
     */
    DIFFIEHELLMAN("DiffieHellman"),
    AES("AES"),
    DES("DES"),
    DESede("DESede");

    private final String algorithm;


    KeyAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getAlgorithm() {
        return algorithm;
    }

}
