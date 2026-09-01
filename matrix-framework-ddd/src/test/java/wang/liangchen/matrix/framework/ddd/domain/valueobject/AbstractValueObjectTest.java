package wang.liangchen.matrix.framework.ddd.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AbstractValueObjectTest {

    @Test
    void equalPrimitiveArraysHaveEqualHashCodes() {
        IntArrayValue left = new IntArrayValue(new int[]{1, 2, 3});
        IntArrayValue right = new IntArrayValue(new int[]{1, 2, 3});

        assertEquals(left, right);
        assertEquals(left.hashCode(), right.hashCode());
    }

    private static final class IntArrayValue extends AbstractValueObject {
        private final int[] values;

        private IntArrayValue(int[] values) {
            this.values = values;
        }
    }
}
