package wang.liangchen.matrix.framework.commons.object;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

/**
 * @author LiangChen.Wang 2021/4/12
 */
public enum ProtostuffUtil {
    //
    INSTANCE;
    private final Schema<ValueWrapper> schema;

    ProtostuffUtil() {
        schema = RuntimeSchema.getSchema(ValueWrapper.class);
    }

    public byte[] object2Bytes(Object object) {
        final LinkedBuffer buffer = LinkedBuffer.allocate();
        return ProtostuffIOUtil.toByteArray(new ValueWrapper(object), schema, buffer);
    }

    public <T> T bytes2Object(byte[] bytes) {
        ValueWrapper valueWrapper = new ValueWrapper();
        ProtostuffIOUtil.mergeFrom(bytes, valueWrapper, schema);
        Object data = valueWrapper.getData();
        return ObjectUtil.INSTANCE.cast(data);
    }

    public static class ValueWrapper {
        private Object data;

        public ValueWrapper() {
        }

        public ValueWrapper(Object data) {
            this.data = data;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }
}
