package liangchen.wang.matrix.framework.data.datasource.dialect;


import liangchen.wang.matrix.framework.commons.exception.MatrixErrorException;
import liangchen.wang.matrix.framework.commons.exception.MatrixInfoException;
import liangchen.wang.matrix.framework.data.dao.RootQuery;

/**
 * @author LiangChen.Wang
 */
public abstract class AbstractDialect implements IDialect {
    private Object hibernateDialect;

    public abstract String resolveCountSql(String targetSql);

    public abstract String resolvePaginationSql(String targetSql, RootQuery rootQuery);

    public abstract String setHibernateDialectClass();

    public Object getHibernateDialect() {
        if (null != this.hibernateDialect) {
            return this.hibernateDialect;
        }
        String hibernateDialectClass = setHibernateDialectClass();
        if (null == hibernateDialectClass || hibernateDialectClass.trim().length() == 0) {
            throw new MatrixInfoException("未设置Hibernate Dialect 类");
        }
        try {
            Class<?> clazz = Class.forName(hibernateDialectClass);
            this.hibernateDialect = clazz.newInstance();
            return this.hibernateDialect;
        } catch (Exception e) {
            throw new MatrixErrorException("初始化Hibernate Dialect错误", e);
        }
    }
}
