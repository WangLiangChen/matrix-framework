package wang.liangchen.matrix.framework.commons.encryption.enums;

import wang.liangchen.matrix.framework.commons.enumeration.Symbol;

/**
 * @author Liangchen.Wang 2022-04-11 17:10
 */
public enum CipherSymmetricAlgorithm {
    AES_CBC_PKCS5Padding("AES/CBC/PKCS5Padding"),
    AES_ECB_PKCS5Padding("AES/ECB/PKCS5Padding"),
    DES_CBC_PKCS5Padding("DES/CBC/PKCS5Padding"),
    DES_ECB_PKCS5Padding("DES/ECB/PKCS5Padding"),
    DESede_CBC_PKCS5Padding("DESede/CBC/PKCS5Padding"),
    DESede_ECB_PKCS5Padding("DESede/ECB/PKCS5Padding");
    private final String transformation;

    CipherSymmetricAlgorithm(String transformation) {
        this.transformation = transformation;
    }

    public String getTransformation() {
        return transformation;
    }

    public String getAlgorithm() {
        String transformation = this.transformation;
        int index = transformation.indexOf(Symbol.URI_SEPARATOR.getSymbol());
        return transformation.substring(0, index);
    }

    public KeyAlgorithm getKeyAlgorithm() {
        return KeyAlgorithm.valueOf(getAlgorithm());
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
