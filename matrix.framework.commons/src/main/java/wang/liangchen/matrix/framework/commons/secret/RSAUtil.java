package wang.liangchen.matrix.framework.commons.secret;

import wang.liangchen.matrix.framework.commons.exception.AssertUtil;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.io.IOUtil;
import wang.liangchen.matrix.framework.commons.utils.StringUtil;

import javax.crypto.Cipher;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author LiangChen.Wang
 */
public enum RSAUtil {
    /**
     * INSTANCE
     */
    INSTANCE;
    private final String privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDSf8C4cbm/sN81v7DRUNMTWNNmZ/dddLSLuSIbr3RqtgXCvDPYcdY/MeD5Hfk0fhiXC9N2+nbwAtTTPlX32jfDl767WZtWfOFZ7hYFYn9AdSrk/5tQmtyF/iXGXIRSpLeNqiwshjWZpYMIQZ+nPI2fKgvsbY/LWQF+OLkdbip+ExT18WWfBGS8ewioZgwM0hAUiLCa8ZIa4fpHkRInBXDDpL5PSJl0qGD2VpTbH6bf4y8lDVB5um6GI5Wvh3GxjxyQSwq+EcWxnyxLZUpOLHkOF8V4AVmFQ5QORMt3rSnLX2up/74emeI+a4/3unXFH7k6SAi8sjFEAWKzjNK8dWjlAgMBAAECggEAUXa1i+wfWctGpxN3h5pDBeLDdYdY2Sc4VDait5owcLffBN03J1Xu92gDTAXIkwMV2ybtgMcvznUaDmK6fIBMQnrwiqsEDfWodNhKV20mGDkAD73ISuqT25te3SSi5STwEHhCPjz2c271hfV3pw4tNOLd2HMSSj048bNcGMKw2Tcvh5Xm++OTLFdUmverpnYOlRzO4bf+UgG1vDl4rNDJvvwUG8J/QBDBAs5JSfmcE540I2zqYpVCuGqqCvBz4SN36LmKYDk3eNOihDGLFA89TEFrT8/7Ts/EXGRPmykSVSXhBAytINpnWnM/726WoGclaL7jniJHwQjNf7BVY/AfRQKBgQDv9MA+ettfAgf2MzBEdjeiC+q9U/OkgEFS/vuD47pRfdGtLxIWPyc/HdtCXf7f1qJt58FjxgdPfRHdtKYgXDdx+apIFTW/m0nl3I306diFLqWKu5ni+9x92w0+pBjlRNhlpUQqoiJQZgS11tV2b9HIYbhtMybQAPH1Bahx/HszBwKBgQDgksuwF1iVAW59fu1iG4RDoR/+iUlyZ0gqtf66++hu0kv0gzaiMLCXPzW08AXRP5fdHHZJ6O0N9ehxI1wlisN2vxu1JZktj/AVu90VhkyccD1QWpDBDHHyDciiHm92Rhvj2lfmzV7oyq2+mMd2oLHWZvdiepOwHmzwVXw+OkOtswKBgEQOyyfteKLt1IxD4IDKduUDNEUWtpgFuIFtyLCTupi6cuoH271rlBoWwcWFG3EpU1CQ1w0Rcald899KCYRMI320LlPbkC8UQFVtxOWeHcdIf7NlmjQC83rO0mbd7CG68RWDVl4xNkJPbS6WDF1XQczvyntOcse0POwd+rS5w8epAoGBAJUFtHaQt26BebF7ZckBm35JUHLW5U8ubDwzcurfuPi5Qj/qRnnQN47WGiyMTj/xpDPM7E3VbGEkOm/BWCdtcpG89YT6gzAx4M57UJU0/medL4K/5mjEemR2cxnpemuQ0Hcd79CvnXh67lqZBVuZ/QjaYPUPhCuNtRqyj3S3sytzAoGAZVQKU9uAzXro+MwMVQrjTn3qxU1trqkfJlT+WHwlK/oGl9MgsMPSg88CIf9OGqpzvkB0GJ2smLK3WcvTx91gwrXqZbmsAoOEooqpSOyFbfyiBzz9SFGZl+5JPFUms0N0UtdW3G6rG/W9oTif2JYBYVyQFZ+Nz6DqpXQSjQ7f1l4=";
    // publicKey:D27FC0B871B9BFB0DF35BFB0D150D31358D36667F75D74B48BB9221BAF746AB605C2BC33D871D63F31E0F91DF9347E18970BD376FA76F002D4D33E55F7DA37C397BEBB599B567CE159EE1605627F40752AE4FF9B509ADC85FE25C65C8452A4B78DAA2C2C863599A58308419FA73C8D9F2A0BEC6D8FCB59017E38B91D6E2A7E1314F5F1659F0464BC7B08A8660C0CD2101488B09AF1921AE1FA479112270570C3A4BE4F489974A860F65694DB1FA6DFE32F250D5079BA6E862395AF8771B18F1C904B0ABE11C5B19F2C4B654A4E2C790E17C57801598543940E44CB77AD29CB5F6BA9FFBE1E99E23E6B8FF7BA75C51FB93A4808BCB231440162B38CD2BC7568E5
    private final byte[] privateKeyBytes = Base64Util.INSTANCE.decode(privateKey);

    public String decrypt(String string) {
        AssertUtil.INSTANCE.notBlank(string, "string can not be blank");
        try {
            // 取得私钥
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            // 对密文base64解密
            byte[] ciphertextBytes = Base64Util.INSTANCE.decode(string);
            byte[] decryptBytes = cipher.doFinal(ciphertextBytes);
            return new String(decryptBytes);
        } catch (Exception e) {
            throw new MatrixErrorException(e);
        }
    }

    public String rsa256(String content, String privateKey, String charset) {
        AssertUtil.INSTANCE.notBlank(content, "参数content不能为空");
        AssertUtil.INSTANCE.notBlank(privateKey, "参数privateKey不能为空");
        try {
            PrivateKey priKey = getPrivateKeyFromPKCS8("RSA", new ByteArrayInputStream(privateKey.getBytes()));
            Signature signature = Signature.getInstance("SHA256WithRSA");
            signature.initSign(priKey);
            if (StringUtil.INSTANCE.isBlank(charset)) {
                signature.update(content.getBytes(StandardCharsets.UTF_8));
            } else {
                signature.update(content.getBytes(charset));
            }
            byte[] signed = signature.sign();
            return Base64Util.INSTANCE.encode(signed);
        } catch (Exception e) {
            throw new MatrixErrorException("数据签名失败:{}", e.getLocalizedMessage());
        }
    }

    public boolean rsa256Verify(String content, String sign, String publicKey, String charset) {
        AssertUtil.INSTANCE.notBlank(content, "参数content不能为空");
        AssertUtil.INSTANCE.notBlank(sign, "参数sign不能为空");
        AssertUtil.INSTANCE.notBlank(publicKey, "参数publicKey不能为空");
        try {
            PublicKey pubKey = getPublicKeyFromX509("RSA", new ByteArrayInputStream(publicKey.getBytes()));
            Signature signature = Signature.getInstance("SHA256WithRSA");
            signature.initVerify(pubKey);

            if (StringUtil.INSTANCE.isBlank(charset)) {
                signature.update(content.getBytes(StandardCharsets.UTF_8));
            } else {
                signature.update(content.getBytes(charset));
            }

            return signature.verify(Base64Util.INSTANCE.decode(sign));
        } catch (Exception e) {
            throw new MatrixErrorException(e, "RSA content = {},sign={},charset = {}", content, sign, charset);
        }
    }

    private PrivateKey getPrivateKeyFromPKCS8(String algorithm, InputStream inputStream) {
        AssertUtil.INSTANCE.notBlank(algorithm, "参数algorithm不能为空");
        AssertUtil.INSTANCE.notNull(inputStream, "参数inputStream不能为null");
        byte[] encodedKey = IOUtil.INSTANCE.read(inputStream).getBytes();
        encodedKey = Base64Util.INSTANCE.decode(encodedKey);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
            return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
        } catch (Exception e) {
            throw new MatrixErrorException(e);
        }
    }

    private PublicKey getPublicKeyFromX509(String algorithm, InputStream inputStream) {
        AssertUtil.INSTANCE.notBlank(algorithm, "参数algorithm不能为空");
        AssertUtil.INSTANCE.notNull(inputStream, "参数inputStream不能为null");
        StringWriter writer = new StringWriter();
        IOUtil.INSTANCE.io(new InputStreamReader(inputStream), writer);
        byte[] encodedKey = writer.toString().getBytes();
        encodedKey = Base64Util.INSTANCE.decode(encodedKey);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
            return keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
        } catch (Exception e) {
            throw new MatrixErrorException(e);
        }
    }
}
