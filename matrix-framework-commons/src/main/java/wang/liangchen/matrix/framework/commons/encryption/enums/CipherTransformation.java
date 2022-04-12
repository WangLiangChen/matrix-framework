package wang.liangchen.matrix.framework.commons.encryption.enums;

import wang.liangchen.matrix.framework.commons.enumeration.Symbol;

/**
 * @author Liangchen.Wang 2022-04-11 17:10
 */
public enum CipherTransformation {
    AES_CBC_NoPadding("AES/CBC/NoPadding", KeyAlgorithm.AES),
    AES_CBC_PKCS5Padding("AES/CBC/PKCS5Padding", KeyAlgorithm.AES),
    AES_ECB_NoPadding("AES/ECB/NoPadding", KeyAlgorithm.AES),
    AES_ECB_PKCS5Padding("AES/ECB/PKCS5Padding", KeyAlgorithm.AES),
    DES_CBC_NoPadding("DES/CBC/NoPadding", KeyAlgorithm.DES),
    DES_CBC_PKCS5Padding("DES/CBC/PKCS5Padding", KeyAlgorithm.DES),
    DES_ECB_NoPadding("DES/ECB/NoPadding", KeyAlgorithm.DES),
    DES_ECB_PKCS5Padding("DES/ECB/PKCS5Padding", KeyAlgorithm.DESede),
    DESede_CBC_NoPadding("DESede/CBC/NoPadding", KeyAlgorithm.DESede),
    DESede_CBC_PKCS5Padding("DESede/CBC/PKCS5Padding", KeyAlgorithm.DESede),
    DESede_ECB_NoPadding("DESede/ECB/NoPadding", KeyAlgorithm.DESede),
    DESede_ECB_PKCS5Padding("DESede/ECB/PKCS5Padding", KeyAlgorithm.DESede),
    RSA_ECB_PKCS1Padding("RSA/ECB/PKCS1Padding", KeyAlgorithm.RSA),
    RSA_ECB_OAEPWithSHA1AndMGF1Padding("RSA/ECB/OAEPWithSHA-1AndMGF1Padding", KeyAlgorithm.RSA),
    RSA_ECB_OAEPWithSHA256AndMGF1Padding("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", KeyAlgorithm.RSA);

    private final String transformation;
    private final KeyAlgorithm keyAlgorithm;


    CipherTransformation(String transformation, KeyAlgorithm keyAlgorithm) {
        this.transformation = transformation;
        this.keyAlgorithm = keyAlgorithm;
    }

    public String getTransformation() {
        return transformation;
    }

    public KeyAlgorithm getKeyAlgorithm() {
        return keyAlgorithm;
    }

    public String getAlgorithm() {
        String transformation = this.transformation;
        int index = transformation.indexOf(Symbol.URI_SEPARATOR.getSymbol());
        return transformation.substring(0, index);
    }

    public String getMode() {
        String transformation = this.transformation;
        int index = transformation.indexOf(Symbol.URI_SEPARATOR.getSymbol());
        int lastIndex = transformation.lastIndexOf(Symbol.URI_SEPARATOR.getSymbol());
        return transformation.substring(index + 1, lastIndex);
    }

    public String getPadding() {
        String transformation = this.transformation;
        int lastIndex = transformation.lastIndexOf(Symbol.URI_SEPARATOR.getSymbol());
        return transformation.substring(lastIndex + 1);
    }
}
