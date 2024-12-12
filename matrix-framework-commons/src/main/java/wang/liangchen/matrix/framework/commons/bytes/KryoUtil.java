package wang.liangchen.matrix.framework.commons.bytes;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.Pool;
import wang.liangchen.matrix.framework.commons.encryption.DigestSignUtil;
import wang.liangchen.matrix.framework.commons.encryption.enums.DigestAlgorithm;
import wang.liangchen.matrix.framework.commons.object.ObjectUtil;

/**
 * @author Liangchen.Wang
 */
public enum KryoUtil {
    INSTANCE;
    private final Pool<Kryo> kryoPool = new Pool<Kryo>(true, false, 16) {
        protected Kryo create() {
            Kryo kryo = new Kryo();
            kryo.setReferences(false);
            kryo.setRegistrationRequired(false);
            return kryo;
        }
    };
    private final Pool<Output> outputPool = new Pool<Output>(true, false, 16) {
        protected Output create() {
            return new Output(1024, -1);
        }
    };
    private final Pool<Input> inputPool = new Pool<Input>(true, false, 16) {
        protected Input create() {
            return new Input(1024);
        }
    };

    public byte[] serializer(Object object) {
        Kryo kryo = kryoPool.obtain();
        Output output = outputPool.obtain();
        try {
            kryo.writeClassAndObject(output, object);
            return output.toBytes();
        } finally {
            outputPool.free(output);
            kryoPool.free(kryo);
        }
    }

    public String serializerMD5(Object object) {
        return DigestSignUtil.INSTANCE.digest(DigestAlgorithm.MD5, serializer(object));
    }

    public <T> T deserializer(byte[] bytes) {
        Kryo kryo = kryoPool.obtain();
        Input input = inputPool.obtain();
        input.setBuffer(bytes);
        try {
            return ObjectUtil.INSTANCE.cast(kryo.readClassAndObject(input));
        } finally {
            inputPool.free(input);
            kryoPool.free(kryo);
        }
    }
}
