package wang.liangchen.matrix.framework.data.dao;

import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

import java.util.StringJoiner;

/**
 * @author Liangchen.Wang 2022-12-09 17:26
 */
public final class SequenceKey {
    private final String sequenceGroup;
    private final String sequenceKey;
    private final long initialValue;

    private SequenceKey(String sequenceGroup, String sequenceKey, Long initialValue) {
        this.sequenceGroup = ValidationUtil.INSTANCE.notBlank(sequenceGroup);
        this.sequenceKey = ValidationUtil.INSTANCE.notBlank(sequenceKey);
        this.initialValue = ValidationUtil.INSTANCE.notNull(initialValue);
    }

    public static SequenceKey newSequence(String sequenceGroup, String sequenceKey, long initialValue) {
        return new SequenceKey(sequenceGroup, sequenceKey, initialValue);
    }

    public String getSequenceGroup() {
        return sequenceGroup;
    }

    public String getSequenceKey() {
        return sequenceKey;
    }

    public long getInitialValue() {
        return initialValue;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SequenceKey.class.getSimpleName() + "[", "]")
                .add("sequenceGroup='" + sequenceGroup + "'")
                .add("sequenceKey='" + sequenceKey + "'")
                .add("initialValue=" + initialValue)
                .toString();
    }
}
