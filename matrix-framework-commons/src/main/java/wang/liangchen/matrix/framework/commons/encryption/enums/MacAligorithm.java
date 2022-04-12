package wang.liangchen.matrix.framework.commons.encryption.enums;

/**
 * @author Liangchen.Wang 2022-04-11 16:42
 */
public enum MacAligorithm {
    HmacMD5("HmacMD5"),
    HmacSHA1("HmacSHA1"),
    HmacSHA256("HmacSHA256"),
    HmacSHA384("HmacSHA384"),
    HmacSHA512("HmacSHA512");

    private final String algorithm;

    MacAligorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getAlgorithm() {
        return algorithm;
    }
}
