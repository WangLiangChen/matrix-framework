package wang.liangchen.matrix.framework.commons.encryption;

public class KeyPairString {
    private String privateKeyString;
    private String publicKeyString;

    public KeyPairString(String privateKeyString, String publicKeyString) {
        this.privateKeyString = privateKeyString;
        this.publicKeyString = publicKeyString;
    }

    public String getPrivateKeyString() {
        return privateKeyString;
    }

    public String getPublicKeyString() {
        return publicKeyString;
    }
}
