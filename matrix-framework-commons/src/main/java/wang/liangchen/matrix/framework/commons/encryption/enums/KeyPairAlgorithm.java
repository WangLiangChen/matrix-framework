package wang.liangchen.matrix.framework.commons.encryption.enums;

/**
 * @author Liangchen.Wang 2022-04-12 9:22
 */
public enum KeyPairAlgorithm {
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
    DIFFIEHELLMAN("DiffieHellman");

    private final String algorithm;


    KeyPairAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getAlgorithm() {
        return algorithm;
    }

}
