package wang.liangchen.matrix.framework.data.mybatis.interceptor;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.data.datasource.MultiDataSourceContext;
import wang.liangchen.matrix.framework.data.datasource.dialect.AbstractDialect;
import wang.liangchen.matrix.framework.data.query.RootQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author LiangChen.Wang
 */
@Intercepts(@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}))
public class PaginationInterceptor implements Interceptor {
    private static final Logger logger = LoggerFactory.getLogger(PaginationInterceptor.class);
    private static final ReflectorFactory DEFAULT_REFLECTOR_FACTORY = new DefaultReflectorFactory();
    private static final Pattern PATTERN_SQL_BLANK = Pattern.compile("\\s+");
    private static final String BLANK = " ";
    //private static final String FIELD_DELEGATE = "delegate";
    // private static final String FIELD_ROWBOUNDS = "rowBounds";
    //private static final String FIELD_MAPPEDSTATEMENT = "mappedStatement";
    //private static final String FIELD_SQL = "sql";
    private static final String SELECT = "select";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        //RoutingStatementHandler routingStatementHandler = (RoutingStatementHandler) invocation.getTarget();
        //StatementHandler statementHandler = (StatementHandler) ClassUtil.INSTANCE.getPrivateFieldValue(routingStatementHandler, FIELD_DELEGATE);
        //Configuration configuration = (Configuration) metaObject.getValue("delegate.configuration");
        // 在方法plugin中，已经限定了本intercepto的使用条件，这里就不需要在判断什么
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = statementHandler.getBoundSql();
        String targetSql = replaceSqlBlank(boundSql.getSql());
        if (!targetSql.startsWith(SELECT)) {
            return invocation.proceed();
        }
        logger.info("mybatis拦截分页SQL:{}", targetSql);
        // 获取动态数据库dialect
        Connection connection = (Connection) invocation.getArgs()[0];
        AbstractDialect dialect = MultiDataSourceContext.INSTANCE.getDialect();
        String countSql = dialect.resolveCountSql(targetSql);
        logger.info("mybatis构造分页计数SQL:{}", countSql);
        // 获取方法参数
        Object parameterObject = statementHandler.getParameterHandler().getParameterObject();
        RootQuery rootQuery = (RootQuery) parameterObject;

        // 用户获取取或设置statementHandler原本不可访问的属性
        MetaObject metaObject = this.getMetaObject(statementHandler);
        int count = executeCountSql(connection, metaObject, countSql);
        rootQuery.setTotalRecords(count);

        String paginationSql = dialect.resolvePaginationSql(targetSql, rootQuery);
        logger.info("mybatis构造分页SQL:{}", targetSql);
        // ClassUtil.INSTANCE.setDeclaredFieldValue(boundSql, FIELD_SQL, pagingSql);
        metaObject.setValue("delegate.boundSql.sql", paginationSql);

        // RowBounds rowBounds = (RowBounds)ClassUtil.INSTANCE.getPrivateFieldValue(handler, FIELD_ROWBOUNDS);
        // 关闭mybatis自己的分页
        metaObject.setValue("delegate.rowBounds.offset", RowBounds.NO_ROW_OFFSET);
        metaObject.setValue("delegate.rowBounds.limit", RowBounds.NO_ROW_LIMIT);
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (!(target instanceof RoutingStatementHandler)) {
            return target;
        }
        StatementHandler statementHandler = (StatementHandler) target;
        // 获取方法参数
        Object parameterObject = statementHandler.getParameterHandler().getParameterObject();
        if (!(parameterObject instanceof RootQuery)) {
            return target;
        }
        RootQuery rootQuery = (RootQuery) parameterObject;
        Boolean autoPagination = rootQuery.getAutoPagination();
        if (null == autoPagination || !autoPagination) {
            return target;
        }
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

    private String replaceSqlBlank(String originalSql) {
        Matcher matcher = PATTERN_SQL_BLANK.matcher(originalSql);
        originalSql = matcher.replaceAll(BLANK);
        return originalSql.toLowerCase();
    }

    private MetaObject getMetaObject(StatementHandler statementHandler) {
        MetaObject metaStatementHandler = MetaObject.forObject(statementHandler, SystemMetaObject.DEFAULT_OBJECT_FACTORY, SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLECTOR_FACTORY);
        // 分离代理对象链
        while (metaStatementHandler.hasGetter("h")) {
            Object object = metaStatementHandler.getValue("h");
            metaStatementHandler = MetaObject.forObject(object, SystemMetaObject.DEFAULT_OBJECT_FACTORY, SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLECTOR_FACTORY);
        }
        // 分离最后一个代理对象的目标类
        while (metaStatementHandler.hasGetter("target")) {
            Object object = metaStatementHandler.getValue("target");
            metaStatementHandler = MetaObject.forObject(object, SystemMetaObject.DEFAULT_OBJECT_FACTORY, SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLECTOR_FACTORY);
        }
        return metaStatementHandler;
    }

    private int executeCountSql(Connection connection, MetaObject metaObject, String countSql) {
        // MappedStatement mappedStatement = (MappedStatement)ClassUtil.INSTANCE.getPrivateFieldValue(statementHandler, FIELD_MAPPEDSTATEMENT);
        //MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        //List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        //Object parameterObject = boundSql.getParameterObject();
        //BoundSql totalBoundSql = new BoundSql(mappedStatement.getConfiguration(), countSql, parameterMappings, parameterObject);
        //ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, parameterObject, totalBoundSql);
        //使用上面的方式得到parameterHandler，不能使用<bind标签中绑定的值，小面的方法是使用原来的，上面的时新建的
        ParameterHandler parameterHandler = (ParameterHandler) metaObject.getValue("delegate.parameterHandler");
        int totalRecord = 0;
        try (PreparedStatement pstmt = connection.prepareStatement(countSql)) {
            parameterHandler.setParameters(pstmt);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    totalRecord = rs.getInt(1);
                }
            } catch (Exception e) {
                throw new MatrixErrorException(e);
            }

        } catch (Exception e) {
            throw new MatrixErrorException(e);
        }
        return totalRecord;
    }
}
