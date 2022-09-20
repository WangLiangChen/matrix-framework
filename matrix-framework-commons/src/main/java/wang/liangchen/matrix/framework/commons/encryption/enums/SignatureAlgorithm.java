package wang.liangchen.matrix.framework.commons.encryption.enums;

/**
 * @author Liangchen.Wang 2022-04-11 17:11
 */
public enum SignatureAlgorithm {
    SHA1withDSA(KeyPairAlgorithm.DSA),
    SHA1withRSA(KeyPairAlgorithm.RSA),
    SHA256withRSA(KeyPairAlgorithm.RSA),
    SHA384withRSA(KeyPairAlgorithm.RSA),
    SHA512withRSA(KeyPairAlgorithm.RSA);


    private final KeyPairAlgorithm keyAlgorithm;

    SignatureAlgorithm(KeyPairAlgorithm keyAlgorithm) {

        this.keyAlgorithm = keyAlgorithm;
    }

    public KeyPairAlgorithm getKeyPairAlgorithm() {
        return keyAlgorithm;
    }

}
