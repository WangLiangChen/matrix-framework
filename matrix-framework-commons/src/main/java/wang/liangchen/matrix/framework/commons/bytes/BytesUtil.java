package wang.liangchen.matrix.framework.commons.bytes;


import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import wang.liangchen.matrix.framework.commons.StringUtil;
import wang.liangchen.matrix.framework.commons.exception.ExceptionLevel;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author LiangChen.Wang
 */
public enum BytesUtil {
    /**
     *
     */
    INSTANCE;
    private static final Schema<ProtostuffWrapper> PROTOSTUFF_WRAPPER_SCHEMA = RuntimeSchema.getSchema(ProtostuffWrapper.class);

    private final String[] hexDigits = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    public byte[] toBytes(InputStream inputStream) {
        byte[] buffer = new byte[4096];
        int len;
        try (InputStream innerInputStream = inputStream; ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            while (-1 != (len = innerInputStream.read(buffer))) {
                outputStream.write(buffer, 0, len);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new MatrixErrorException(e);
        }
    }

    public byte[] toBytes(Object object) {
        ValidationUtil.INSTANCE.notNull(ExceptionLevel.WARN, object, "object must not be null");
        ValidationUtil.INSTANCE.isTrue(object instanceof Serializable, "object must be Serializable");
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ObjectOutputStream output = new ObjectOutputStream(outputStream);
            output.writeObject(object);
            output.flush();
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new MatrixErrorException(e);
        }
    }

    public byte[] toBytes(char data) {
        // char is 2 bytes
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (data);
        bytes[1] = (byte) (data >> 8);
        return bytes;
    }

    public byte[] toBytes(short data) {
        // short is 2 bytes
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data & 0xff00) >> 8);
        return bytes;
    }

    public byte[] toBytes(int data) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data & 0xff00) >> 8);
        bytes[2] = (byte) ((data & 0xff0000) >> 16);
        bytes[3] = (byte) ((data & 0xff000000) >> 24);
        return bytes;
    }

    public byte[] toBytes(long data) {
        byte[] bytes = new byte[8];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data >> 8) & 0xff);
        bytes[2] = (byte) ((data >> 16) & 0xff);
        bytes[3] = (byte) ((data >> 24) & 0xff);
        bytes[4] = (byte) ((data >> 32) & 0xff);
        bytes[5] = (byte) ((data >> 40) & 0xff);
        bytes[6] = (byte) ((data >> 48) & 0xff);
        bytes[7] = (byte) ((data >> 56) & 0xff);
        return bytes;
    }

    public byte[] toBytes(float data) {
        int intBits = Float.floatToIntBits(data);
        return toBytes(intBits);
    }

    public byte[] toBytes(double data) {
        long intBits = Double.doubleToLongBits(data);
        return toBytes(intBits);
    }

    public byte[] toBytes(String data, String charsetName) {
        Charset charset = Charset.forName(charsetName);
        return toBytes(data, charset);
    }

    public byte[] toBytes(String data) {
        return toBytes(data, StandardCharsets.UTF_8);
    }

    public byte[] toBytes(String data, Charset charset) {
        if (StringUtil.INSTANCE.isEmpty(data)) {
            return new byte[0];
        }
        ValidationUtil.INSTANCE.notNull(ExceptionLevel.WARN, charset, "charset must not be null");
        return data.getBytes(charset);
    }

    public short toShort(byte[] bytes) {
        ValidationUtil.INSTANCE.notEmpty(ExceptionLevel.WARN, bytes, "bytes must not be null or empty");
        return (short) ((0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)));
    }

    public char toChar(byte[] bytes) {
        ValidationUtil.INSTANCE.notEmpty(ExceptionLevel.WARN, bytes, "bytes must not be null or empty");
        return (char) ((0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)));
    }

    public int toInt(byte[] bytes) {
        ValidationUtil.INSTANCE.notEmpty(ExceptionLevel.WARN, bytes, "bytes must not be null or empty");
        return (0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)) | (0xff0000 & (bytes[2] << 16)) | (0xff000000 & (bytes[3] << 24));
    }

    public long toLong(byte[] bytes) {
        ValidationUtil.INSTANCE.notEmpty(ExceptionLevel.WARN, bytes, "bytes must not be null or empty");
        return (0xffL & (long) bytes[0]) | (0xff00L & ((long) bytes[1] << 8)) | (0xff0000L & ((long) bytes[2] << 16)) | (0xff000000L & ((long) bytes[3] << 24))
                | (0xff00000000L & ((long) bytes[4] << 32)) | (0xff0000000000L & ((long) bytes[5] << 40)) | (0xff000000000000L & ((long) bytes[6] << 48)) | (0xff00000000000000L & ((long) bytes[7] << 56));
    }

    public float toFloat(byte[] bytes) {
        ValidationUtil.INSTANCE.notEmpty(ExceptionLevel.WARN, bytes, "bytes must not be null or empty");
        return Float.intBitsToFloat(toInt(bytes));
    }

    public double toDouble(byte[] bytes) {
        ValidationUtil.INSTANCE.notEmpty(ExceptionLevel.WARN, bytes, "bytes must not be null or empty");
        long l = toLong(bytes);
        return Double.longBitsToDouble(l);
    }

    public String toString(byte[] bytes) {
        ValidationUtil.INSTANCE.notEmpty(ExceptionLevel.WARN, bytes, "bytes must not be null or empty");
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public String toString(byte[] bytes, Charset charset) {
        ValidationUtil.INSTANCE.notEmpty(ExceptionLevel.WARN, bytes, "bytes must not be null or empty");
        ValidationUtil.INSTANCE.notNull(ExceptionLevel.WARN, charset, "charset must not be null");
        return new String(bytes, charset);
    }

    @SuppressWarnings("unchecked")
    public <T> T toObject(byte[] bytes) {
        ValidationUtil.INSTANCE.notEmpty(ExceptionLevel.WARN, bytes, "bytes must not be null or empty");
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
            ObjectInputStream intput = new ObjectInputStream(inputStream);
            Object object = intput.readObject();
            return (T) object;
        } catch (IOException | ClassNotFoundException e) {
            throw new MatrixErrorException(e);
        }
    }

    public String toHexString(byte[] bytes) {
        ValidationUtil.INSTANCE.notEmpty(ExceptionLevel.WARN, bytes, "bytes must not be null or empty");
        StringBuilder resultSb = new StringBuilder();
        for (byte b : bytes) {
            resultSb.append(toHexString(b));
        }
        return resultSb.toString();
    }

    public String toHexString(byte b) {
        int n = b;
        if (n < 0) {
            n = 256 + n;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    public <T> byte[] protostuffSerializer(T object) {
        ProtostuffWrapper<T> wrapper = new ProtostuffWrapper<>();
        wrapper.setObject(object);
        LinkedBuffer allocate = LinkedBuffer.allocate();
        try {
            return ProtostuffIOUtil.toByteArray(wrapper, PROTOSTUFF_WRAPPER_SCHEMA, allocate);
        } finally {
            allocate.clear();
        }
    }

    public <T> T protostuffDeserializer(byte[] bytes) {
        ProtostuffWrapper<T> wrapper = new ProtostuffWrapper<>();
        ProtostuffIOUtil.mergeFrom(bytes, wrapper, PROTOSTUFF_WRAPPER_SCHEMA);
        return wrapper.getObject();
    }


    static class ProtostuffWrapper<T> {
        private T object;

        public T getObject() {
            return object;
        }

        public void setObject(T object) {
            this.object = object;
        }
    }

}
