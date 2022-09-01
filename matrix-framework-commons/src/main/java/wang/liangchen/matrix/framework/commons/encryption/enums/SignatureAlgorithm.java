package wang.liangchen.matrix.framework.commons.encryption.enums;

/**
 * @author Liangchen.Wang 2022-04-11 17:11
 */
public enum SignatureAlgorithm {
    SHA1withDSA("SHA1withDSA", KeyPairAlgorithm.DSA),
    SHA1withRSA("SHA1withRSA", KeyPairAlgorithm.RSA),
    SHA256withRSA("SHA256withRSA", KeyPairAlgorithm.RSA);

    private final String algorithm;
    private final KeyPairAlgorithm keyAlgorithm;

    SignatureAlgorithm(String algorithm, KeyPairAlgorithm keyAlgorithm) {
        this.algorithm = algorithm;
        this.keyAlgorithm = keyAlgorithm;
    }

    public KeyPairAlgorithm getKeyPairAlgorithm() {
        return keyAlgorithm;
    }

    public String getAlgorithm() {
        return this.algorithm;
    }
}
