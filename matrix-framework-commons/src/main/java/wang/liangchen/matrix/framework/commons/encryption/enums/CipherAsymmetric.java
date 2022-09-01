package wang.liangchen.matrix.framework.commons.encryption.enums;

import wang.liangchen.matrix.framework.commons.enumeration.Symbol;

/**
 * @author Liangchen.Wang 2022-04-11 17:10
 */
public enum CipherAsymmetric {
    RSA_ECB_PKCS1Padding("RSA/ECB/PKCS1Padding"),
    RSA_ECB_OAEPWithSHA1AndMGF1Padding("RSA/ECB/OAEPWithSHA-1AndMGF1Padding"),
    RSA_ECB_OAEPWithSHA256AndMGF1Padding("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");

    private final String transformation;

    CipherAsymmetric(String transformation) {
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
