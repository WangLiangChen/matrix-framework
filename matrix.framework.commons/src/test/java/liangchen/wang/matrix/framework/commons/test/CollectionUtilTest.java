package liangchen.wang.matrix.framework.commons.test;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class CollectionUtilTest {
    @Test
    public void testArray2Set() {
        System.out.println(isEmpty(new byte[]{1}));
        System.out.println(isEmpty(new byte[0]));
    }

    private boolean isEmpty(Object object) {
        if (null == object) {
            return true;
        }
        if (object instanceof String) {
            return object.toString().isEmpty();
        }
        if (object instanceof Map) {
            return ((Map<?, ?>) object).isEmpty();
        }
        if (object instanceof Collection) {
            return ((Collection<?>) object).isEmpty();
        }
        if (object instanceof Iterator) {
            return !((Iterator<?>) object).hasNext();
        }
        Class<?> type = object.getClass();
        if (type.isArray()) {
            return Array.getLength(object) == 0;
        }

        return true;
    }

}
