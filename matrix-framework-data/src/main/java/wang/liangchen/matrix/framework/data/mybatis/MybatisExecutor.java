package wang.liangchen.matrix.framework.data.mybatis;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.exception.Assert;
import wang.liangchen.matrix.framework.data.dao.criteria.*;
import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Liangchen.Wang
 * Mybatis Statement Cache & Executor
 */
public enum MybatisExecutor {
    /**
     * instance
     */
    INSTANCE;
    public final Logger logger = LoggerFactory.getLogger(MybatisExecutor.class);
    private final static Map<String, String> STATEMENT_CACHE = new ConcurrentHashMap<>(128);

    public <E extends RootEntity> int insert(final SqlSessionTemplate sqlSessionTemplate, final E entity) {
        Assert.INSTANCE.notNull(entity, "entity can not be null");
        Class<? extends RootEntity> entityClass = entity.getClass();
        String statementId = String.format("%s.%s", entityClass.getName(), "insert");
        STATEMENT_CACHE.computeIfAbsent(statementId, cacheKey -> {
            TableMeta entityTableMeta = TableMetas.INSTANCE.tableMeta(entityClass);
            Map<String, ColumnMeta> columnMetas = entityTableMeta.getColumnMetas();
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("<script>");
            sqlBuilder.append("insert into ").append(entityTableMeta.getTableName());
            sqlBuilder.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
            columnMetas.values().forEach(columnMeta -> sqlBuilder.append(columnMeta.getColumnName()).append(","));
            sqlBuilder.append("</trim>");
            sqlBuilder.append("values");
            sqlBuilder.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
            columnMetas.values().forEach(columnMeta -> sqlBuilder.append("#{").append(columnMeta.getFieldName()).append("},"));
            sqlBuilder.append("</trim>");
            sqlBuilder.append("</script>");
            String sql = sqlBuilder.toString();
            buildMappedStatement(sqlSessionTemplate, statementId, SqlCommandType.INSERT, sql, entityClass, Integer.class);
            logger.debug("create and cache insertId:{},sql:{}", statementId, sql);
            return sql;
        });
        return sqlSessionTemplate.insert(statementId, entity);
    }

    public <E extends RootEntity> int insert(final SqlSessionTemplate sqlSessionTemplate, final List<E> entities) {
        Assert.INSTANCE.notEmpty(entities, "entities can not be empty");
        E entity = entities.get(0);
        Class<? extends RootEntity> entityClass = entity.getClass();
        String statementId = String.format("%s.%s", entityClass.getName(), "insertBatch");
        STATEMENT_CACHE.computeIfAbsent(statementId, cacheKey -> {
            TableMeta entityTableMeta = TableMetas.INSTANCE.tableMeta(entityClass);
            Map<String, ColumnMeta> columnMetas = entityTableMeta.getColumnMetas();
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("<script>");
            sqlBuilder.append("insert into ").append(entityTableMeta.getTableName());
            sqlBuilder.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
            columnMetas.values().forEach(columnMeta -> sqlBuilder.append(columnMeta.getColumnName()).append(","));
            sqlBuilder.append("</trim>");
            sqlBuilder.append("values");
            sqlBuilder.append("<foreach collection=\"list\" item=\"item\" separator=\",\">");
            sqlBuilder.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
            columnMetas.values().forEach(columnMeta -> sqlBuilder.append("#{item.").append(columnMeta.getFieldName()).append("},"));
            sqlBuilder.append("</trim>");
            sqlBuilder.append("</foreach>");
            sqlBuilder.append("</script>");
            String sql = sqlBuilder.toString();
            buildMappedStatement(sqlSessionTemplate, statementId, SqlCommandType.INSERT, sql, List.class, Integer.class);
            logger.debug("create and cache insertBatchId:{},sql:{}", statementId, sql);
            return sql;
        });
        return sqlSessionTemplate.insert(statementId, entities);
    }

    public <E extends RootEntity> int delete(final SqlSessionTemplate sqlSessionTemplate, final E entity) {
        Assert.INSTANCE.notNull(entity, "entity can not be null");
        Class<? extends RootEntity> entityClass = entity.getClass();
        String statementId = String.format("%s.%s", entityClass.getName(), "delete");
        STATEMENT_CACHE.computeIfAbsent(statementId, cacheKey -> {
            TableMeta tableMeta = TableMetas.INSTANCE.tableMeta(entityClass);
            StringBuilder sqlBuilder = new StringBuilder();
            ColumnMeta columnDeleteMeta = tableMeta.getColumnDeleteMeta();
            if (null == columnDeleteMeta) {
                sqlBuilder.append("<script>");
                sqlBuilder.append("delete from ").append(tableMeta.getTableName());
                sqlBuilder.append(findWhereSql(tableMeta.getPkColumnMetas()));
                sqlBuilder.append("</script>");
            } else {
                // 通过扩展字段 增加deleteValue
                entity.addExtendedField("deleteValue", columnDeleteMeta.getDeleteValue());
                sqlBuilder.append("<script>");
                sqlBuilder.append("update ").append(tableMeta.getTableName());
                sqlBuilder.append(" set ");
                sqlBuilder.append(columnDeleteMeta.getColumnName()).append(Symbol.EQUAL.getSymbol()).append("#{extendedFields.deleteValue}");
                sqlBuilder.append(findWhereSql(tableMeta.getPkColumnMetas()));
                sqlBuilder.append("</script>");
            }
            String sql = sqlBuilder.toString();
            buildMappedStatement(sqlSessionTemplate, statementId, SqlCommandType.DELETE, sql, entityClass, Integer.class);
            logger.debug("create and cache deleteId:{},sql:{}", statementId, sql);
            return sql;
        });
        int rows = sqlSessionTemplate.delete(statementId, entity);
        // 使用后删除 deleteValue
        entity.removeExtendedField("deleteValue");
        return rows;
    }

    public <E extends RootEntity> int delete(final SqlSessionTemplate sqlSessionTemplate, final CriteriaParameter<E> criteriaParameter) {
        TableMeta tableMeta = criteriaParameter.getTableMeta();
        String statementId = String.format("%s.%s", tableMeta.getEntityClass().getName(), "deleteBatch");
        STATEMENT_CACHE.computeIfAbsent(statementId, cacheKey -> {
            StringBuilder sqlBuilder = new StringBuilder();
            ColumnMeta columnDeleteMeta = tableMeta.getColumnDeleteMeta();
            if (null == columnDeleteMeta) {
                sqlBuilder.append("<script>");
                sqlBuilder.append("delete from ").append(tableMeta.getTableName());
                sqlBuilder.append("<where>${whereSql}</where>");
                sqlBuilder.append("</script>");
            } else {
                // 通过扩展字段 增加deleteValue
                criteriaParameter.addExtendedField("deleteValue", columnDeleteMeta.getDeleteValue());
                sqlBuilder.append("<script>");
                sqlBuilder.append("update ").append(tableMeta.getTableName());
                sqlBuilder.append(" set ");
                sqlBuilder.append(columnDeleteMeta.getColumnName()).append(Symbol.EQUAL.getSymbol()).append("#{extendedFields.deleteValue}");
                sqlBuilder.append("<where>${whereSql}</where>");
                sqlBuilder.append("</script>");
            }
            String sql = sqlBuilder.toString();
            buildMappedStatement(sqlSessionTemplate, statementId, SqlCommandType.DELETE, sql, CriteriaParameter.class, Integer.class);
            logger.debug("create and cache deleteBatchId:{},sql:{}", statementId, sql);
            return sql;
        });
        return sqlSessionTemplate.delete(statementId, criteriaParameter);
    }

    public <E extends RootEntity> int update(final SqlSessionTemplate sqlSessionTemplate, E entity) {
        Assert.INSTANCE.notNull(entity, "entity can not be null");
        Class<? extends RootEntity> entityClass = entity.getClass();
        String statementId = String.format("%s.%s", entityClass.getName(), "update");
        STATEMENT_CACHE.computeIfAbsent(statementId, cacheKey -> {
            TableMeta entityTableMeta = TableMetas.INSTANCE.tableMeta(entityClass);
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("<script>");
            sqlBuilder.append("update ").append(entityTableMeta.getTableName());
            sqlBuilder.append("<set>");
            // 只更新非空项
            entityTableMeta.getNonPkColumnMetas().values().forEach(columnMeta -> {
                sqlBuilder.append("<if test=\"@wang.liangchen.matrix.framework.data.mybatis.Ognl@isNotNull(").append(columnMeta.getFieldName()).append(")\">");
                sqlBuilder.append(columnMeta.getColumnName()).append("=#{").append(columnMeta.getFieldName()).append("},");
                sqlBuilder.append("</if>");
            });
            // 更新强制项
            sqlBuilder.append("<if test=\"@wang.liangchen.matrix.framework.data.mybatis.Ognl@isNotEmpty(forceUpdateColumns)\">");
            sqlBuilder.append("<foreach collection=\"forceUpdateColumns.keys\" item=\"key\" separator=\",\">");
            sqlBuilder.append("${key} = #{forceUpdateColumns.${key}}");
            sqlBuilder.append("</foreach>");
            sqlBuilder.append("</if>");
            sqlBuilder.append("</set>");
            sqlBuilder.append(findWhereSql(entityTableMeta.getPkColumnMetas()));
            sqlBuilder.append("</script>");
            String sql = sqlBuilder.toString();
            buildMappedStatement(sqlSessionTemplate, statementId, SqlCommandType.UPDATE, sql, entityClass, Integer.class);
            logger.debug("create and cache updateId:{},sql:{}", statementId, sql);
            return sql;
        });
        return sqlSessionTemplate.update(statementId, entity);
    }

    public <E extends RootEntity> int update(final SqlSessionTemplate sqlSessionTemplate, final CriteriaParameter<E> criteriaParameter) {
        RootEntity entity = criteriaParameter.getEntity();
        Class<? extends RootEntity> entityClass = entity.getClass();
        String statementId = String.format("%s.%s", entityClass.getName(), "updateBatch");
        STATEMENT_CACHE.computeIfAbsent(statementId, cacheKey -> {
            TableMeta entityTableMeta = TableMetas.INSTANCE.tableMeta(entityClass);
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("<script>");
            sqlBuilder.append("update ").append(entityTableMeta.getTableName());
            sqlBuilder.append("<set>");
            entityTableMeta.getNonPkColumnMetas().values().forEach(columnMeta -> {
                sqlBuilder.append("<if test=\"@wang.liangchen.matrix.framework.data.mybatis.Ognl@isNotNull(entity.").append(columnMeta.getFieldName()).append(")\">");
                sqlBuilder.append(columnMeta.getColumnName()).append("=#{entity.").append(columnMeta.getFieldName()).append("},");
                sqlBuilder.append("</if>");
            });

            sqlBuilder.append("<if test=\"@wang.liangchen.matrix.framework.data.mybatis.Ognl@isNotEmpty(entity.forceUpdateColumns)\">");
            sqlBuilder.append("<foreach collection=\"entity.forceUpdateColumns.keys\" item=\"key\" separator=\",\">");
            sqlBuilder.append("${key} = #{entity.forceUpdateColumns.${key}}");
            sqlBuilder.append("</foreach>");
            sqlBuilder.append("</if>");
            sqlBuilder.append("</set>");
            sqlBuilder.append("<where>${whereSql}</where>");
            sqlBuilder.append("</script>");
            String sql = sqlBuilder.toString();
            buildMappedStatement(sqlSessionTemplate, statementId, SqlCommandType.UPDATE, sql, UpdateCriteria.class, Integer.class);
            logger.debug("create and cache updateBatchId:{},sql:{}", statementId, sql);
            return sql;
        });
        return sqlSessionTemplate.update(statementId, criteriaParameter);
    }


    public <E extends RootEntity> int count(final SqlSessionTemplate sqlSessionTemplate, final CriteriaParameter<E> criteriaParameter) {
        TableMeta tableMeta = criteriaParameter.getTableMeta();
        String statementId = String.format("%s.%s", tableMeta.getEntityClass().getName(), "count");
        STATEMENT_CACHE.computeIfAbsent(statementId, cacheKey -> {
            StringBuilder sqlBuilder = new StringBuilder();
            // 根据mysql文档，count(0)和count(*)没有实现及性能上的差别,但count(*)符合标准语法
            // count(column)只计数非null
            sqlBuilder.append("<script>");
            sqlBuilder.append("select count(*) from ").append(tableMeta.getTableName());
            sqlBuilder.append("<where>${whereSql}</where>");
            sqlBuilder.append("</script>");
            buildMappedStatement(sqlSessionTemplate, statementId, SqlCommandType.SELECT, sqlBuilder.toString(), CriteriaParameter.class, Integer.class);
            String sql = sqlBuilder.toString();
            logger.debug("create and cache countId:{},sql:{}", statementId, sql);
            return sql;
        });
        return sqlSessionTemplate.selectOne(statementId, criteriaParameter);
    }

    public <E extends RootEntity> List<E> list(final SqlSessionTemplate sqlSessionTemplate, final CriteriaParameter<E> criteriaParameter) {
        TableMeta tableMeta = criteriaParameter.getTableMeta();
        Class<?> entityClass = tableMeta.getEntityClass();
        String statementId = String.format("%s.%s", entityClass.getName(), "list");
        STATEMENT_CACHE.computeIfAbsent(statementId, cacheKey -> {
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("<script>");
            sqlBuilder.append("select ");
            sqlBuilder.append("<if test=\"true==distinct\">").append(" distinct ").append("</if>");
            sqlBuilder.append("<trim suffixOverrides=\",\"><foreach collection=\"resultColumns\" item=\"item\" index=\"index\" separator=\",\">${item}</foreach></trim>");
            sqlBuilder.append("from ").append(tableMeta.getTableName());
            sqlBuilder.append("<where>${whereSql}</where>");
            sqlBuilder.append("<if test=\"true==forUpdate\">").append("for update").append("</if>");
            sqlBuilder.append("<if test=\"@wang.liangchen.matrix.framework.data.mybatis.Ognl@isNotEmpty(orderBy)\"> order by <foreach collection=\"orderBy\" item=\"item\" index=\"index\" separator=\",\"> ${item.orderBy} ${item.direction} </foreach></if>");
            sqlBuilder.append("<if test=\"null!=offset and null!=rows\">limit #{offset},#{pageSize}</if>");
            sqlBuilder.append("</script>");
            String sql = sqlBuilder.toString();
            buildMappedStatement(sqlSessionTemplate, cacheKey, SqlCommandType.SELECT, sql, CriteriaParameter.class, entityClass);
            logger.debug("create and cache listId:{},sql:{}", statementId, sql);
            return sql;
        });
        return sqlSessionTemplate.selectList(statementId, criteriaParameter);
    }

    private StringBuilder findWhereSql(Map<String, ColumnMeta> pkColumnMetas) {
        StringBuilder whereSql = new StringBuilder();
        whereSql.append("<where>");
        pkColumnMetas.values().forEach(columnMeta -> whereSql.append("and ").append(columnMeta.getColumnName()).append("=#{").append(columnMeta.getFieldName()).append("}"));
        whereSql.append("</where>");
        return whereSql;
    }

    private void buildMappedStatement(SqlSessionTemplate sqlSessionTemplate, String mappedStatementId, SqlCommandType sqlCommandType, String sql, Class<?> parameterType, Class<?> resultType) {
        List<ResultMap> resultMaps = new ArrayList<>(1);
        Configuration configuration = sqlSessionTemplate.getConfiguration();
        resultMaps.add(new ResultMap.Builder(configuration, "defaultResultMap", resultType, Collections.emptyList()).build());
        LanguageDriver languageDriver = configuration.getDefaultScriptingLanguageInstance();
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, parameterType);
        MappedStatement ms = new MappedStatement.Builder(configuration, mappedStatementId, sqlSource, sqlCommandType).resultMaps(resultMaps).build();
        configuration.addMappedStatement(ms);
    }
}
