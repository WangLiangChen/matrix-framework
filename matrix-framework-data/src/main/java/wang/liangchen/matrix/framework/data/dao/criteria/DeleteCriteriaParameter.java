package wang.liangchen.matrix.framework.data.dao.criteria;

import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;

/**
 * @author Liangchen.Wang 2022-04-17 23:14
 */
public final class DeleteCriteriaParameter<E extends RootEntity> extends CriteriaParameter<E> {
    private final String deleteColumnName;
    private final Object deleteValue;

    public DeleteCriteriaParameter(String deleteColumnName, Object deleteValue) {
        this.deleteColumnName = deleteColumnName;
        this.deleteValue = deleteValue;
    }

    public String getDeleteColumnName() {
        return deleteColumnName;
    }

    public Object getDeleteValue() {
        return deleteValue;
    }
}
