package liangchen.wang.matrix.framework.data.mybatis;

import liangchen.wang.matrix.framework.commons.exception.AssertUtil;
import liangchen.wang.matrix.framework.commons.exception.MatrixInfoException;
import liangchen.wang.matrix.framework.data.annotation.Query;
import liangchen.wang.matrix.framework.data.query.Between;
import liangchen.wang.matrix.framework.data.dao.entity.EntityMeta;
import liangchen.wang.matrix.framework.data.dao.entity.RootEntity;
import liangchen.wang.matrix.framework.data.query.RootQuery;
import liangchen.wang.matrix.framework.data.query.AndOr;
import liangchen.wang.matrix.framework.data.query.Operator;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Table;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MybatisStatementIdBuilder {
    public final Logger logger = LoggerFactory.getLogger(MybatisStatementIdBuilder.class);
    private final SqlSessionTemplate sqlSessionTemplate;
    private final EntityMeta entityMeta;
    private final static Map<String, String> statementCache = new ConcurrentHashMap<>(128);

    public MybatisStatementIdBuilder(SqlSessionTemplate sqlSessionTemplate, EntityMeta entityMeta) {
        this.sqlSessionTemplate = sqlSessionTemplate;
        this.entityMeta = entityMeta;
    }

    public String insertId(Class<? extends RootEntity> entityClass) {
        String entityClassName = entityClass.getName();
        String cacheKey = String.format("%s.%s", entityClassName, "insert");
        statementCache.computeIfAbsent(cacheKey, statementId -> {
            Set<String> ids = entityMeta.getIds();
            Set<String> fields = entityMeta.getFields();
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("<script>insert into ").append(entityMeta.getTableName()).append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
            ids.forEach(pk -> sqlBuilder.append(pk).append(","));
            fields.forEach(field -> sqlBuilder.append(field).append(","));
            sqlBuilder.append("</trim>values<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
            ids.forEach(pk -> sqlBuilder.append("#{").append(pk).append("},"));
            fields.forEach(field -> sqlBuilder.append("#{").append(field).append("},"));
            sqlBuilder.append("</trim></script>");
            String sql = sqlBuilder.toString();
            buildMappedStatement(cacheKey, SqlCommandType.INSERT, sql, entityClass, Integer.class);
            logger.debug("create insertId:{},sql:{}", cacheKey, sql);
            return sql;
        });
        return cacheKey;
    }

    public <E extends RootEntity> String insertBatchId(Class<E> entityClass) {
        String entityClassName = entityClass.getName();
        String cacheKey = String.format("%s.%s", entityClassName, "insertBatch");
        statementCache.computeIfAbsent(cacheKey, statementId -> {
            Set<String> ids = entityMeta.getIds();
            Set<String> fields = entityMeta.getFields();
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("<script>insert into ").append(entityMeta.getTableName()).append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
            ids.forEach(pk -> sqlBuilder.append(pk).append(","));
            fields.forEach(field -> sqlBuilder.append(field).append(","));
            sqlBuilder.append("</trim>values");
            sqlBuilder.append("<foreach collection=\"list\" item=\"item\" separator=\",\">");
            sqlBuilder.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
            ids.forEach(pk -> sqlBuilder.append("#{item.").append(pk).append("},"));
            fields.forEach(field -> sqlBuilder.append("#{item.").append(field).append("},"));
            sqlBuilder.append("</trim>");
            sqlBuilder.append("</foreach>");
            sqlBuilder.append("</script>");
            String sql = sqlBuilder.toString();
            buildMappedStatement(cacheKey, SqlCommandType.INSERT, sql, entityClass, Integer.class);
            logger.debug("create insertBatchId:{},sql:{}", cacheKey, sql);
            return sql;
        });
        return cacheKey;
    }

    public String deleteByQueryId(Class<? extends RootQuery> queryClass) {
        String tableName = tableNameByQueryClass(queryClass);
        String queryClassName = queryClass.getName();
        String cacheKey = String.format("%s.%s", queryClassName, "delete");
        statementCache.computeIfAbsent(cacheKey, statementId -> {
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("<script>delete from ").append(tableName);
            sqlBuilder.append(findWhereSql(queryClass));
            sqlBuilder.append("</script>");
            String sql = sqlBuilder.toString();
            buildMappedStatement(cacheKey, SqlCommandType.DELETE, sql, queryClass, Integer.class);
            logger.debug("create deleteByQueryId:{},sql:{}", cacheKey, sql);
            return sql;
        });
        return cacheKey;
    }

    public String updateByQueryId(Class<? extends RootEntity> entityClass, Class<? extends RootQuery> queryClass) {
        return updateId(entityClass, queryClass);
    }

    private String updateId(Class<? extends RootEntity> entityClass, Class<? extends RootQuery> queryClass) {
        String cacheKey = null == queryClass ? String.format("%s.%s", entityClass.getName(), "update") : String.format("%s.%s", queryClass.getName(), "update");
        statementCache.computeIfAbsent(cacheKey, statementId -> {
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("<script>update ").append(entityMeta.getTableName()).append("<set>");
            entityMeta.getFields().forEach(e -> {
                sqlBuilder.append("<if test=\"@liangchen.wang.gradf.framework.data.mybatis.Ognl@isNotEmpty(entity.").append(e).append(")\">");
                sqlBuilder.append(e).append("=#{entity.").append(e).append("},");
                sqlBuilder.append("</if>");
            });

            sqlBuilder.append("<if test=\"@liangchen.wang.gradf.framework.data.mybatis.Ognl@isNotEmpty(entity.dynamicFields)\">");
            sqlBuilder.append("<foreach collection=\"entity.dynamicFields.keys\" item=\"key\" separator=\",\">");
            sqlBuilder.append("<if test=\"@liangchen.wang.gradf.framework.data.mybatis.Ognl@isNull(entity.dynamicFields[key])\">");
            sqlBuilder.append("${key} = null");
            sqlBuilder.append("</if>");
            sqlBuilder.append("<if test=\"@liangchen.wang.gradf.framework.data.mybatis.Ognl@isNotNull(entity.dynamicFields[key])\">");
            sqlBuilder.append("${key} = #{entity.dynamicFields.${key}}");
            sqlBuilder.append("</if>");
            sqlBuilder.append("</foreach>");
            sqlBuilder.append("</if>");
            sqlBuilder.append("</set>");
            sqlBuilder.append(findWhereSql(queryClass));
            sqlBuilder.append("</script>");
            String sql = sqlBuilder.toString();
            buildMappedStatement(cacheKey, SqlCommandType.UPDATE, sql, entityClass, Integer.class);
            logger.debug("create updateId:{},sql:{}", cacheKey, sql);
            return sql;
        });
        return cacheKey;
    }

    public String countId(Class<? extends RootQuery> queryClass) {
        String tableName = tableNameByQueryClass(queryClass);
        String cacheKey = String.format("%s.%s", queryClass.getName(), "count");

        statementCache.computeIfAbsent(cacheKey, statementId -> {
            StringBuilder sqlBuilder = new StringBuilder();
            // 根据mysql文档，count(0)和count(*)没有实现及性能上的差别,但count(*)符合标准语法
            // count(column)只计数非null
            sqlBuilder.append("<script>select count(*) from ").append(tableName);
            sqlBuilder.append(findWhereSql(queryClass));
            sqlBuilder.append("</script>");
            buildMappedStatement(cacheKey, SqlCommandType.SELECT, sqlBuilder.toString(), queryClass, Integer.class);
            String sql = sqlBuilder.toString();
            logger.debug("create countId:{},sql:{}", cacheKey, sql);
            return sql;
        });
        return cacheKey;
    }

    public String listId(Class<? extends RootQuery> queryClass, Class<? extends RootEntity> entityClass) {
        String cacheKey = String.format("%s.%s", entityClass.getName(), "list");
        statementCache.computeIfAbsent(cacheKey, statementId -> {
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("<script>select <trim suffixOverrides=\",\"><foreach collection=\"returnFields\" item=\"item\" index=\"index\" separator=\",\">${item}</foreach></trim> from ").append(entityMeta.getTableName());
            sqlBuilder.append(findWhereSql(queryClass));
            sqlBuilder.append("<if test=\"true==forUpdate\">").append("for update").append("</if>");
            sqlBuilder.append("<if test=\"@liangchen.wang.gradf.framework.data.mybatis.Ognl@isNotEmpty(orderBy)\"> order by <foreach collection=\"orderBy\" item=\"item\" index=\"index\" separator=\",\"> ${item.orderBy} ${item.direction} </foreach></if>");
            sqlBuilder.append("<if test=\"null!=offset and null!=rows\">limit #{offset},#{rows}</if>");
            sqlBuilder.append("</script>");
            String sql = sqlBuilder.toString();
            buildMappedStatement(cacheKey, SqlCommandType.SELECT, sql, queryClass, entityClass);
            logger.debug("create listId:{},sql:{}", cacheKey, sql);
            return sql;
        });
        return cacheKey;
    }

    private StringBuilder findWhereSql(Class<? extends RootQuery> queryClass) {
        StringBuilder whereSql = new StringBuilder();
        whereSql.append("<where>");
        Field[] fields = queryClass.getDeclaredFields();
        for (Field field : fields) {
            Query annotation = field.getAnnotation(Query.class);
            if (null == annotation) {
                continue;
            }
            String fieldName = field.getName();
            String columnName = annotation.column();
            if (columnName.length() == 0) {
                columnName = fieldName;
            }
            whereSql.append("<if test=\"@liangchen.wang.gradf.framework.data.mybatis.Ognl@isNotEmpty(").append(fieldName).append(")\">");
            AndOr andOr = annotation.andOr();
            whereSql.append(andOr.getAndOr()).append(columnName);
            Operator operator = annotation.operator();
            switch (operator) {
                case ISNULL:
                case ISNOTNULL:
                    whereSql.append(operator.getOperator());
                    break;
                case BETWEEN:
                case NOTBETWEEN:
                    Class<?> fieldType = field.getType();
                    if (fieldType.isAssignableFrom(Between.class)) {
                        whereSql.append(operator.getOperator()).append("#{").append(fieldName).append(".min}").append(" and #{").append(fieldName).append(".max}");
                        break;
                    }
                    throw new MatrixInfoException("使用操作符:{},字段:{}类型必须为Between", operator.getOperator(), fieldName);
                case EQUALS:
                case NOTEQUALS:
                    whereSql.append(operator.getOperator()).append("#{").append(fieldName).append("}");
                    break;
                case GREATERTHAN:
                case GREATERTHAN_OR_EQUALS:
                case LESSTHAN:
                case LESSTHAN_OR_EQUALS:
                    whereSql.append(" <![CDATA[").append(operator.getOperator()).append("]]> ").append("#{").append(fieldName).append("}");
                    break;
                case STARTWITH:
                case NOTSTARTWITH:
                    whereSql.append("<bind name=\"_").append(fieldName).append("\" value=\"_parameter." + fieldName + "+'%'\"/>");
                    whereSql.append(operator.getOperator()).append("#{").append("_").append(fieldName).append("}");
                    break;
                case ENDWITH:
                case NOTENDWITH:
                    whereSql.append("<bind name=\"_").append(fieldName).append("\" value=\"'%'+_parameter.").append(fieldName).append("\"/>");
                    whereSql.append(operator.getOperator()).append("#{").append("_").append(fieldName).append("}");
                    break;
                case CONTAINS:
                case NOTCONTAINS:
                    whereSql.append("<bind name=\"_").append(fieldName).append("\" value=\"'%'+_parameter.").append(fieldName).append("+'%'\"/>");
                    whereSql.append(operator.getOperator()).append("#{").append("_").append(fieldName).append("}");
                    break;
                case IN:
                case NOTIN:
                    fieldType = field.getType();
                    if (fieldType.isArray() || fieldType.isAssignableFrom(List.class) || fieldType.isAssignableFrom(Set.class)) {
                        whereSql.append(operator.getOperator()).append("<foreach open=\"(\" close=\")\" collection=\"").append(fieldName).append("\" item=\"item\" index=\"index\" separator=\",\">#{item}</foreach>");
                        break;
                    }
                    throw new MatrixInfoException("使用操作符:{},字段:{}类型必须为Array、List、Set", operator.getOperator(), fieldName);
                default:
                    throw new MatrixInfoException("不支持的运算符:{}", operator.getOperator());
            }
            whereSql.append("</if>");
        }
        whereSql.append("</where>");
        return whereSql;
    }

    private String tableNameByQueryClass(Class<? extends RootQuery> queryClass) {
        Table table = queryClass.getAnnotation(Table.class);
        AssertUtil.INSTANCE.notNull(table, "Query类必须使用注解@javax.persistence.Table(name=\"\")指定数据库表");
        String tableName = table.name();
        AssertUtil.INSTANCE.notBlank(tableName, "Query类必须使用注解@javax.persistence.Table(name=\"\")指定数据库表");
        return tableName;
    }


    private void buildMappedStatement(String mappedStatementId, SqlCommandType sqlCommandType, String sql, Class<?> parameterType, Class<?> resultType) {
        List<ResultMap> resultMaps = new ArrayList<>(1);
        Configuration configuration = sqlSessionTemplate.getConfiguration();
        resultMaps.add(new ResultMap.Builder(configuration, "defaultResultMap", resultType, Collections.emptyList()).build());
        SqlSource sqlSource;
        if (null == parameterType) {
            sqlSource = new StaticSqlSource(configuration, sql);
        } else {
            LanguageDriver languageDriver = configuration.getDefaultScriptingLanguageInstance();
            sqlSource = languageDriver.createSqlSource(configuration, sql, parameterType);
        }
        MappedStatement ms = new MappedStatement.Builder(configuration, mappedStatementId, sqlSource, sqlCommandType).resultMaps(resultMaps).build();
        configuration.addMappedStatement(ms);
    }
}
