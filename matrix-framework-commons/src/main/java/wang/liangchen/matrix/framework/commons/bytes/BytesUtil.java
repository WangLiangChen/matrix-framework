package wang.liangchen.matrix.framework.commons.bytes;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import wang.liangchen.matrix.framework.commons.exception.Assert;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.string.StringUtil;

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
    private final String[] hexDigits = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
    private final Kryo kryo = new Kryo();

    public byte[] toBytes(InputStream inputStream) {
        byte[] buffer = new byte[4096];
        int n;

        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            while (-1 != (n = inputStream.read(buffer))) {
                output.write(buffer, 0, n);
            }
            return output.toByteArray();
        } catch (IOException e) {
            throw new MatrixErrorException(e);
        }
    }

    public byte[] toBytes(Object object) {
        Assert.INSTANCE.notNull(object, "object can not be null");
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ObjectOutputStream output = new ObjectOutputStream(outputStream);
            output.writeObject(object);
            output.flush();
            byte[] bytes = outputStream.toByteArray();
            return bytes;
        } catch (IOException e) {
            throw new MatrixErrorException(e);
        }
    }

    public byte[] toKryoBytes(Object object) {
        Assert.INSTANCE.notNull(object, "object can not be null");
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Output output = new Output(outputStream);
            kryo.writeObject(output, object);
            output.flush();
            byte[] bytes = output.toBytes();
            return bytes;
        } catch (IOException e) {
            throw new MatrixErrorException(e);
        }
    }

    public byte[] toBytes(char data) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (data);
        bytes[1] = (byte) (data >> 8);
        return bytes;
    }

    public byte[] toBytes(short data) {
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
        if (StringUtil.INSTANCE.isBlank(data)) {
            return new byte[0];
        }
        Assert.INSTANCE.notNull(charset, "charset can not be null");
        return data.getBytes(charset);
    }

    public short toShort(byte[] bytes) {
        Assert.INSTANCE.notEmpty(bytes, "bytes can not be null or empty");
        return (short) ((0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)));
    }

    public char toChar(byte[] bytes) {
        Assert.INSTANCE.notEmpty(bytes, "bytes can not be null or empty");
        return (char) ((0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)));
    }

    public int toInt(byte[] bytes) {
        Assert.INSTANCE.notEmpty(bytes, "bytes can not be null or empty");
        return (0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)) | (0xff0000 & (bytes[2] << 16)) | (0xff000000 & (bytes[3] << 24));
    }

    public long toLong(byte[] bytes) {
        Assert.INSTANCE.notEmpty(bytes, "bytes can not be null or empty");
        return (0xffL & (long) bytes[0]) | (0xff00L & ((long) bytes[1] << 8)) | (0xff0000L & ((long) bytes[2] << 16)) | (0xff000000L & ((long) bytes[3] << 24))
                | (0xff00000000L & ((long) bytes[4] << 32)) | (0xff0000000000L & ((long) bytes[5] << 40)) | (0xff000000000000L & ((long) bytes[6] << 48)) | (0xff00000000000000L & ((long) bytes[7] << 56));
    }

    public float toFloat(byte[] bytes) {
        Assert.INSTANCE.notEmpty(bytes, "bytes can not be null or empty");
        return Float.intBitsToFloat(toInt(bytes));
    }

    public double toDouble(byte[] bytes) {
        Assert.INSTANCE.notEmpty(bytes, "bytes can not be null or empty");
        long l = toLong(bytes);
        return Double.longBitsToDouble(l);
    }

    public String toString(byte[] bytes) {
        Assert.INSTANCE.notEmpty(bytes, "bytes can not be null or empty");
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public String toString(byte[] bytes, Charset charset) {
        Assert.INSTANCE.notEmpty(bytes, "bytes can not be null or empty");
        Assert.INSTANCE.notNull(charset, "charset can not be null");
        return new String(bytes, charset);
    }

    @SuppressWarnings("unchecked")
    public <T> T Object(byte[] bytes) {
        Assert.INSTANCE.notEmpty(bytes, "bytes can not be null or empty");
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
            ObjectInputStream intput = new ObjectInputStream(inputStream);
            Object object = intput.readObject();
            return (T) object;
        } catch (IOException | ClassNotFoundException e) {
            throw new MatrixErrorException(e);
        }
    }

    public <T> T toKryoObject(byte[] bytes, Class<T> clazz) {
        Assert.INSTANCE.notEmpty(bytes, "bytes can not be null or empty");
        Assert.INSTANCE.notNull(clazz, "clazz can not bye null");
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
            Kryo kryo = new Kryo();
            Input input = new Input(inputStream);
            T object = kryo.readObject(input, clazz);
            input.close();
            return object;
        } catch (IOException e) {
            throw new MatrixErrorException(e);
        }
    }

    public String toHexString(byte[] bytes) {
        Assert.INSTANCE.notEmpty(bytes, "bytes can not be null or empty");
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
}
