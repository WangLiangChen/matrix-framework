package wang.liangchen.matrix.framework.commons.encryption;

/**
 * @author Liangchen.Wang 2022-04-11 16:33
 */
public enum DigestAlgorithm {
    MD2("MD2"),
    MD5("MD5"),
    SHA1("SHA-1"),
    SHA256("SHA-256"),
    SHA384("SHA-384"),
    SHA512("SHA-512");

    private final String algorithm;

    DigestAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getAlgorithm() {
        return algorithm;
    }
}
