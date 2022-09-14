package wang.liangchen.matrix.framework.commons.encryption;

import wang.liangchen.matrix.framework.commons.encryption.enums.HmacAligorithm;
import wang.liangchen.matrix.framework.commons.encryption.enums.SignatureAlgorithm;
import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.exception.ExceptionLevel;
import wang.liangchen.matrix.framework.commons.network.NetUtil;
import wang.liangchen.matrix.framework.commons.uid.NanoIdUtil;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Liangchen.Wang 2022-09-03 10:04
 * 签名的传递
 * {@code Signature: algorithm=MD5&nonce=${nonce}&timestamp=${timestamp}&signature=${signature}}
 * 签名内容以换行符分隔，最后一行不添加\n
 * uri\n
 * timestamp\n
 * nonce\n
 * body
 */
public class SignatureClaim implements Serializable {
    private final static String LINE = "\n";
    private final String uri;
    private final Long timestamp;
    private final String nonce;
    private final String body;
    private final String payload;

    private String algorithm;
    private String signature;

    public static SignatureClaim instance4Sign(String uri, String body) {
        ValidationUtil.INSTANCE.notBlank(ExceptionLevel.WARN,uri, "uri must not be blank");
        ValidationUtil.INSTANCE.notBlank(ExceptionLevel.WARN,body, "body must not be blank");
        return new SignatureClaim(uri, body);
    }

    public static SignatureClaim instance4Sign(String uri) {
        ValidationUtil.INSTANCE.notBlank(ExceptionLevel.WARN,uri, "uri must not be blank");
        return new SignatureClaim(uri, null);
    }

    public static SignatureClaim instance4Verify(String uri, String body, String signMessage) {
        ValidationUtil.INSTANCE.notBlank(ExceptionLevel.WARN,uri, "uri must not be blank");
        ValidationUtil.INSTANCE.notBlank(ExceptionLevel.WARN,body, "body must not be blank");
        ValidationUtil.INSTANCE.notBlank(ExceptionLevel.WARN,signMessage, "signMessage must not be blank");
        return new SignatureClaim(uri, body, signMessage);
    }

    public static SignatureClaim instance4Verify(String uri, String signMessage) {
        ValidationUtil.INSTANCE.notBlank(ExceptionLevel.WARN,uri, "uri must not be blank");
        ValidationUtil.INSTANCE.notBlank(ExceptionLevel.WARN,signMessage, "signMessage must not be blank");
        return new SignatureClaim(uri, null, signMessage);
    }

    private SignatureClaim(String uri, String body) {
        this.uri = uri;
        this.body = body;
        this.timestamp = System.currentTimeMillis();
        this.nonce = NanoIdUtil.INSTANCE.randomNanoId();
        this.payload = buildPayload();
    }

    private SignatureClaim(String uri, String body, String signMessage) {
        this.uri = uri;
        this.body = body;
        Map<String, String> parameters = NetUtil.INSTANCE.queryString2Map(signMessage);
        this.timestamp = Long.valueOf(parameters.get("timestamp"));
        this.nonce = parameters.get("nonce");
        this.payload = buildPayload();
        this.algorithm = parameters.get("algorithm");
        this.signature = parameters.get("signature");
    }


    public String sign(SignatureAlgorithm signatureAlgorithm, String privateKeyString) {
        this.algorithm = signatureAlgorithm.name();
        this.signature = DigestSignUtil.INSTANCE.sign(signatureAlgorithm, privateKeyString, this.payload);
        return buildSignatureMessage();
    }

    public String sign(HmacAligorithm hmacAligorithm, String secretKeyString) {
        this.algorithm = hmacAligorithm.name();
        this.signature = DigestSignUtil.INSTANCE.hmac(hmacAligorithm, secretKeyString, this.payload);
        return buildSignatureMessage();
    }

    public boolean verify(String key) {
        try {
            HmacAligorithm hmacAligorithm = HmacAligorithm.valueOf(this.algorithm);
            String verify = DigestSignUtil.INSTANCE.hmac(hmacAligorithm, key, this.payload);
            return this.signature.equals(verify);
        } catch (Exception e) {
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.valueOf(this.algorithm);
            return DigestSignUtil.INSTANCE.verify(signatureAlgorithm, key, this.signature, this.payload);
        }
    }

    public String getPayload() {
        return payload;
    }

    public String getSignature() {
        return signature;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    private String buildPayload() {
        StringBuilder payloadBuilder = new StringBuilder(this.uri).append(LINE)
                .append(timestamp).append(LINE)
                .append(nonce);
        if (null != this.body) {
            payloadBuilder.append(LINE).append(body);
        }
        return payloadBuilder.toString();
    }


    private String buildSignatureMessage() {
        // algorithm=${algorithm}&timestamp=${timestamp}&nonce=${nonce}&signature=${signature}
        StringBuilder signBuilder = new StringBuilder();
        signBuilder.append("algorithm").append(Symbol.EQUAL.getSymbol()).append(algorithm);
        signBuilder.append(Symbol.AND.getSymbol()).append("timestamp").append(Symbol.EQUAL.getSymbol()).append(timestamp);
        signBuilder.append(Symbol.AND.getSymbol()).append("nonce").append(Symbol.EQUAL.getSymbol()).append(nonce);
        signBuilder.append(Symbol.AND.getSymbol()).append("signature").append(Symbol.EQUAL.getSymbol()).append(signature);
        return signBuilder.toString();
    }

}
