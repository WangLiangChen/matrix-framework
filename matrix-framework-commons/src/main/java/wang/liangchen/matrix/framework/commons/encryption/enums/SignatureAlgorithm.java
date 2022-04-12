package wang.liangchen.matrix.framework.commons.encryption.enums;

/**
 * @author Liangchen.Wang 2022-04-11 17:11
 */
public enum SignatureAlgorithm {
    SHA1withDSA("SHA1withDSA", KeyAlgorithm.DSA),
    SHA1withRSA("SHA1withRSA", KeyAlgorithm.RSA),
    SHA256withRSA("SHA256withRSA", KeyAlgorithm.RSA);

    private final String algorithm;
    private final KeyAlgorithm keyAlgorithm;

    SignatureAlgorithm(String algorithm, KeyAlgorithm keyAlgorithm) {
        this.algorithm = algorithm;
        this.keyAlgorithm = keyAlgorithm;
    }

    public KeyAlgorithm getKeyAlgorithm() {
        return keyAlgorithm;
    }

    public String getAlgorithm() {
        return this.algorithm;
    }
}
