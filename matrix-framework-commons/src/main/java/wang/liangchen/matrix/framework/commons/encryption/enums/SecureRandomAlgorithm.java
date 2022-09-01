package wang.liangchen.matrix.framework.commons.encryption.enums;

public enum SecureRandomAlgorithm {
    NativePRNG,
    NativePRNGBlocking,
    NativePRNGNonBlocking,
    PKCS11,
    SHA1PRNG;
}
