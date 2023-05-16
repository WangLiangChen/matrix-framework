package wang.liangchen.matrix.framework.data.dao.criteria;

import java.util.StringJoiner;

/**
 * @author Liangchen.Wang 2023-04-29 10:20
 */
public class DeleteMeta {
    private final String deleteColumnName;
    private final Object deleteValue;

    public DeleteMeta(String deleteColumnName, Object deleteValue) {
        this.deleteColumnName = deleteColumnName;
        this.deleteValue = deleteValue;
    }

    public static DeleteMeta newInstance(String deleteColumnName, Object deleteValue) {
        return new DeleteMeta(deleteColumnName, deleteValue);
    }

    public String getDeleteColumnName() {
        return deleteColumnName;
    }

    public Object getDeleteValue() {
        return deleteValue;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "MarkDeleteMeta[", "]")
                .add("deleteColumnName='" + deleteColumnName + "'")
                .add("deleteValue=" + deleteValue)
                .toString();
    }
}
