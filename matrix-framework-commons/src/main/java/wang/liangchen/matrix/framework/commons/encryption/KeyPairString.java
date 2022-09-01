package wang.liangchen.matrix.framework.commons.encryption;

public class KeyPairString {
    private String privateKey;
    private String publicKey;

    public KeyPairString(String privateKey, String publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }
}
