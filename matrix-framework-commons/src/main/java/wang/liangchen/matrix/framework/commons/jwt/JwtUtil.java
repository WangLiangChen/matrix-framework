package wang.liangchen.matrix.framework.commons.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import wang.liangchen.matrix.framework.commons.encryption.Base64Util;
import wang.liangchen.matrix.framework.commons.encryption.KeyPairString;
import wang.liangchen.matrix.framework.commons.encryption.SecretKeyUtil;
import wang.liangchen.matrix.framework.commons.encryption.enums.KeyPairAlgorithm;
import wang.liangchen.matrix.framework.commons.exception.Assert;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.exception.MatrixInfoException;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;

/**
 * @author Liangchen.Wang 2022-09-02 9:31
 */
public enum JwtUtil {
    INSTANCE;

    public String generateKey(int length) {
        //32 48 64
        Assert.INSTANCE.isTrue(length == 32 || length == 48 || length == 64, "length must be equal to 32,48,64");
        SecureRandom random = new SecureRandom();
        byte[] sharedSecret = new byte[length];
        random.nextBytes(sharedSecret);
        return Base64Util.INSTANCE.encode(sharedSecret);
    }

    public String sign(String secretKey, JWTClaimsSet claimsSet) {
        Assert.INSTANCE.notBlank(secretKey, "secretKey can not be blank");
        byte[] sharedSecret = Base64Util.INSTANCE.decode(secretKey);
        int length = sharedSecret.length;
        Assert.INSTANCE.isTrue(length == 32 || length == 48 || length == 64, "length must be equal to 32,48,64");
        JWSAlgorithm jwsAlgorithm;
        switch (length) {
            case 32:
                jwsAlgorithm = JWSAlgorithm.HS256;
                break;
            case 48:
                jwsAlgorithm = JWSAlgorithm.HS384;
                break;
            case 64:
                jwsAlgorithm = JWSAlgorithm.HS512;
                break;
            default:
                throw new MatrixInfoException("Not Supported Algorithm");
        }

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(jwsAlgorithm), claimsSet);
        try {
            JWSSigner signer = new MACSigner(sharedSecret);
            signedJWT.sign(signer);
        } catch (JOSEException e) {
            throw new MatrixErrorException(e);
        }
        return signedJWT.serialize();
    }

    public JWTClaimsSet verify(String secretKey, String jwtString) {
        Assert.INSTANCE.notBlank(secretKey, "secretKey can not be blank");
        Assert.INSTANCE.notBlank(jwtString, "jwtString can not be blank");
        byte[] sharedSecret = Base64Util.INSTANCE.decode(secretKey);
        int length = sharedSecret.length;
        Assert.INSTANCE.isTrue(length == 32 || length == 48 || length == 64, "length must be equal to 32,48,64");
        try {
            SignedJWT signedJWT = SignedJWT.parse(jwtString);
            JWSVerifier verifier = new MACVerifier(sharedSecret);
            if (signedJWT.verify(verifier)) {
                return signedJWT.getJWTClaimsSet();
            }
        } catch (JOSEException | ParseException e) {
            throw new MatrixErrorException(e);
        }
        return null;
    }

    public KeyPairString keyPair() {
        try {
            RSAKey rsaKey = new RSAKeyGenerator(2048).generate();
            byte[] privateKeyBytes = rsaKey.toPrivateKey().getEncoded();
            byte[] publicKeyBytes = rsaKey.toPublicKey().getEncoded();
            return new KeyPairString(Base64Util.INSTANCE.encode(privateKeyBytes), Base64Util.INSTANCE.encode(publicKeyBytes));
        } catch (JOSEException e) {
            throw new MatrixErrorException(e);
        }
    }

    public String rsaSign(String privateKeyString, JWTClaimsSet claimsSet) {
        Assert.INSTANCE.notBlank(privateKeyString, "privateKey can not be blank");
        PrivateKey privateKey = SecretKeyUtil.INSTANCE.generatePrivateKeyPKCS8(KeyPairAlgorithm.RSA, privateKeyString);
        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256).build(),
                claimsSet);
        JWSSigner signer = new RSASSASigner(privateKey);
        try {
            signedJWT.sign(signer);
        } catch (JOSEException e) {
            throw new MatrixErrorException(e);
        }
        return signedJWT.serialize();
    }

    public JWTClaimsSet rsaVerify(String publicKeyString, String jwtString) {
        Assert.INSTANCE.notBlank(publicKeyString, "publicKeyString can not be blank");
        Assert.INSTANCE.notBlank(jwtString, "jwtString can not be blank");
        PublicKey publicKey = SecretKeyUtil.INSTANCE.generatePublicKeyX509(KeyPairAlgorithm.RSA, publicKeyString);
        try {
            SignedJWT signedJWT = SignedJWT.parse(jwtString);
            JWSVerifier verifier = new RSASSAVerifier((RSAPublicKey) publicKey);
            if (signedJWT.verify(verifier)) {
                return signedJWT.getJWTClaimsSet();
            }
        } catch (JOSEException | ParseException e) {
            throw new MatrixErrorException(e);
        }
        return null;
    }
}
