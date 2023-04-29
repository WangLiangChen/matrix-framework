package wang.liangchen.matrix.framework.data.dao.criteria;

import java.util.StringJoiner;

/**
 * @author Liangchen.Wang 2023-04-29 10:20
 */
public class VersionMeta {
    private final String versionColumnName;
    private final Object versionOldValue;
    private final Object versionNewValue;

    private VersionMeta(String versionColumnName, Object versionOldValue, Object versionNewValue) {
        this.versionColumnName = versionColumnName;
        this.versionOldValue = versionOldValue;
        this.versionNewValue = versionNewValue;
    }

    public static VersionMeta newInstance(String versionColumnName, Object versionOldValue, Object versionNewValue) {
        if (null == versionNewValue && null != versionOldValue) {
            versionNewValue = (Integer) versionOldValue + 1;
        }
        return new VersionMeta(versionColumnName, versionOldValue, versionNewValue);
    }

    public String getVersionColumnName() {
        return versionColumnName;
    }

    public Object getVersionOldValue() {
        return versionOldValue;
    }

    public Object getVersionNewValue() {
        return versionNewValue;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "VersionMeta[", "]")
                .add("versionColumnName='" + versionColumnName + "'")
                .add("versionOldValue=" + versionOldValue)
                .add("versionNewValue=" + versionNewValue)
                .toString();
    }
}
