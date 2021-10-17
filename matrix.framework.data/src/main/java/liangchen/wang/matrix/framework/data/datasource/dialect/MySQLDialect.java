package liangchen.wang.matrix.framework.data.datasource.dialect;


import liangchen.wang.matrix.framework.commons.utils.CollectionUtil;
import liangchen.wang.matrix.framework.data.core.RootQuery;
import liangchen.wang.matrix.framework.data.pagination.OrderBy;

import java.util.List;

/**
 * @author LiangChen.Wang
 */
public final class MySQLDialect extends AbstractDialect {
    private static final String HIBERNATE_DIALECT_CLASS = "org.hibernate.dialect.MySQLDialect";

    @Override
    public String resolveCountSql(String targetSql) {
        StringBuilder sb = new StringBuilder("select count(0) from ");
        targetSql = targetSql.toLowerCase();

        if (targetSql.lastIndexOf("order") > targetSql.lastIndexOf(")")) {
            sb.append(targetSql, targetSql.indexOf("from") + 4, targetSql.lastIndexOf("order"));
        } else {
            sb.append(targetSql.substring(targetSql.indexOf("from") + 4));
        }
        return sb.toString();
    }

    @Override
    public String resolvePaginationSql(String targetSql, RootQuery rootQuery) {
        StringBuilder sb = new StringBuilder();
        sb.append(targetSql);
        //构造排序
        List<OrderBy> orderByList = rootQuery.getOrderBy();
        if (!CollectionUtil.INSTANCE.isEmpty(orderByList)) {
            sb.append(" order by ");
            for (OrderBy orderBy : orderByList) {
                sb.append(orderBy.getOrderBy()).append(" ").append(orderBy.getDirection()).append(",");
            }
            //去掉末尾的逗号
            sb.setLength(sb.length() - 1);
        }
        //构造分页
        Integer offset = rootQuery.getOffset();
        offset = null == offset ? 0 : offset;
        Integer rows = rootQuery.getRows();
        if (null != rows && rows > 0) {
            sb.append(" limit ").append(offset).append(" , ").append(rows);
        }
        return sb.toString();
    }

    @Override
    public String setHibernateDialectClass() {
        return HIBERNATE_DIALECT_CLASS;
    }

}
