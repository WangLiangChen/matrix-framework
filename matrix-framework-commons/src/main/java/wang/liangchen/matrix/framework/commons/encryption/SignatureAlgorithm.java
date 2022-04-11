package wang.liangchen.matrix.framework.commons.encryption;

/**
 * @author Liangchen.Wang 2022-04-11 17:11
 */
public enum SignatureAlgorithm {
    SHA1withDSA("DSA"),
    SHA1withRSA("RSA"),
    SHA256withRSA("RSA");

    private final String keyAlgorithm;

    SignatureAlgorithm(String keyAlgorithm) {
        this.keyAlgorithm = keyAlgorithm;
    }

    public String getKeyAlgorithm() {
        return keyAlgorithm;
    }

    public String getAlgorithm() {
        return this.name();
    }
}
