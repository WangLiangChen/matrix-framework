package wang.liangchen.matrix.framework.data.dao.criteria;

/**
 * @author Liangchen.Wang 2022-04-15 17:06
 */
public abstract class Criteria<T> extends AbstractCriteria<T> {
    private EntityGetter<T>[] resultColumns;
    private Criteria(Class<T> entityClass) {
        super(entityClass);
    }

    public static <T> Criteria<T> of(Class<T> entityClass) {
        return new Criteria<T>(entityClass) {
        };
    }

    public Criteria<T> resultColumns(EntityGetter<T>... resultColumns) {
        this.resultColumns = resultColumns;
        return this;
    }

    @Override
    public Criteria<T> equals(EntityGetter<T> column, SqlValue sqlValue) {
        return (Criteria<T>) super.equals(column, sqlValue);
    }

    @Override
    public Criteria<T> OR(SubCriteria<T> subCriteria) {
        return (Criteria<T>) super.OR(subCriteria);
    }

    @Override
    public Criteria<T> AND(SubCriteria<T> subCriteria) {
        return (Criteria<T>) super.AND(subCriteria);
    }

    public EntityGetter<T>[] getResultColumns() {
        return resultColumns;
    }
}
