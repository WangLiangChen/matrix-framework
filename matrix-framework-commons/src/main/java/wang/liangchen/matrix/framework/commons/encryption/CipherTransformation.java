package wang.liangchen.matrix.framework.commons.encryption;

/**
 * @author Liangchen.Wang 2022-04-11 17:10
 */
public enum CipherTransformation {
    AES_CBC_NoPadding("AES/CBC/NoPadding","AES"),
    AES_CBC_PKCS5Padding("AES/CBC/PKCS5Padding","AES"),
    AES_ECB_NoPadding("AES/ECB/NoPadding","AES"),
    AES_ECB_PKCS5Padding("AES/ECB/PKCS5Padding","AES"),
    DES_CBC_NoPadding("DES/CBC/NoPadding","DES"),
    DES_CBC_PKCS5Padding("DES/CBC/PKCS5Padding","DES"),
    DES_ECB_NoPadding("DES/ECB/NoPadding","DES"),
    DES_ECB_PKCS5Padding("DES/ECB/PKCS5Padding","DES"),
    DESede_CBC_NoPadding("DESede/CBC/NoPadding","DESede"),
    DESede_CBC_PKCS5Padding("DESede/CBC/PKCS5Padding","DESede"),
    DESede_ECB_NoPadding("DESede/ECB/NoPadding","DESede"),
    DESede_ECB_PKCS5Padding("DESede/ECB/PKCS5Padding","DESede"),
    RSA_ECB_PKCS1Padding("RSA/ECB/PKCS1Padding","RSA"),
    RSA_ECB_OAEPWithSHA1AndMGF1Padding("RSA/ECB/OAEPWithSHA-1AndMGF1Padding","RSA"),
    RSA_ECB_OAEPWithSHA256AndMGF1Padding("RSA/ECB/OAEPWithSHA-256AndMGF1Padding","RSA");

    private final String transformation;
    private final String algorithm;

    CipherTransformation(String transformation,String algorithm) {
        this.transformation = transformation;
        this.algorithm = algorithm;
    }

    public String getTransformation() {
        return transformation;
    }

    public String getAlgorithm() {
        return algorithm;
    }
}
