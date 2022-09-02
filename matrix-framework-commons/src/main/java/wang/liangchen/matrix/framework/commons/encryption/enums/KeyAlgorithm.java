package wang.liangchen.matrix.framework.commons.encryption.enums;

/**
 * @author Liangchen.Wang 2022-04-12 9:22
 */
public enum KeyAlgorithm {
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
