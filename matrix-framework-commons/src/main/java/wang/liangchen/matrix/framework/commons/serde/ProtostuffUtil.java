package wang.liangchen.matrix.framework.commons.serde;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import wang.liangchen.matrix.framework.commons.crypto.DigestSignUtil;
import wang.liangchen.matrix.framework.commons.crypto.enums.DigestAlgorithm;

/**
 * @author Liangchen.Wang
 */
public enum ProtostuffUtil {
    INSTANCE;
    private final Schema<ProtostuffWrapper> PROTOSTUFF_WRAPPER_SCHEMA = RuntimeSchema.getSchema(ProtostuffWrapper.class);

    public <T> byte[] serializer(T object) {
        ProtostuffWrapper<T> wrapper = new ProtostuffWrapper<>();
        wrapper.setObject(object);
        LinkedBuffer allocate = LinkedBuffer.allocate();
        try {
            return ProtostuffIOUtil.toByteArray(wrapper, PROTOSTUFF_WRAPPER_SCHEMA, allocate);
        } finally {
            allocate.clear();
        }
    }

    public <T> String serializerMD5(T object) {
        return DigestSignUtil.INSTANCE.digest(DigestAlgorithm.MD5, serializer(object));
    }

    public <T> T deserializer(byte[] bytes) {
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
