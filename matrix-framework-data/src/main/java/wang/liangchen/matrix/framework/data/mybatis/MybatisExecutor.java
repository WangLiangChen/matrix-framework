package wang.liangchen.matrix.framework.data.mybatis;

import org.apache.ibatis.mapping.*;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.exception.ExceptionLevel;
import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.commons.type.ClassUtil;
import wang.liangchen.matrix.framework.commons.uid.NumbericUid;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;
import wang.liangchen.matrix.framework.data.annotation.IdStrategy;
import wang.liangchen.matrix.framework.data.dao.criteria.*;
import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;
import wang.liangchen.matrix.framework.data.mybatis.handler.JsonTypeHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Liangchen.Wang
 * Mybatis Statement Cache And Executor
 */
public enum MybatisExecutor {
    /**
     * instance
     */
    INSTANCE;
    public final Logger logger = LoggerFactory.getLogger(MybatisExecutor.class);
    private final static Map<String, String> STATEMENT_CACHE = new ConcurrentHashMap<>(128);
    private final static Map<String, IDGenerator> ID_METHOD_CACHE = new ConcurrentHashMap<>(128);

    public <E extends RootEntity> int insert(final SqlSessionTemplate sqlSessionTemplate, final E entity) {
        ValidationUtil.INSTANCE.notNull(ExceptionLevel.WARN, entity, "entity must not be null");
        Class<? extends RootEntity> entityClass = entity.getClass();
        String statementId = String.format("%s.%s", entityClass.getName(), "insert");
        STATEMENT_CACHE.computeIfAbsent(statementId, cacheKey -> {
            TableMeta tableMeta = TableMetas.INSTANCE.tableMeta(entityClass);
            // 缓存单一ID的setter
            cacheIdGenerator(cacheKey, entityClass, tableMeta.getPkColumnMetas());
            Map<String, ColumnMeta> columnMetas = tableMeta.getColumnMetas();
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("<script>");
            sqlBuilder.append("insert into ").append(tableMeta.getTableName()).append("(");
            columnMetas.values().forEach(columnMeta -> sqlBuilder.append(columnMeta.getColumnName()).append(","));
            // 去除最后一个逗号
            sqlBuilder.deleteCharAt(sqlBuilder.lastIndexOf(Symbol.COMMA.getSymbol()));
            sqlBuilder.append(")values(");
            columnMetas.values().forEach(columnMeta -> {
                String typeHandler = "";
                if (columnMeta.isJson()) {
                    typeHandler = ",typeHandler=wang.liangchen.matrix.framework.data.mybatis.handler.JsonTypeHandler";
                }
                sqlBuilder.append("#{").append(columnMeta.getFieldName()).append(typeHandler).append("},");
            });
            // 去除最后一个逗号
            sqlBuilder.deleteCharAt(sqlBuilder.lastIndexOf(Symbol.COMMA.getSymbol()));
            sqlBuilder.append(")");
            sqlBuilder.append("</script>");
            String sqlScript = sqlBuilder.toString();
            buildMappedStatement(sqlSessionTemplate, statementId, SqlCommandType.INSERT, sqlScript, entityClass, Integer.class);
            logger.debug("create and cache insertId:{},sqlScript:{}", statementId, sqlScript);
            return sqlScript;
        });
        populateId(statementId, Collections.singletonList(entity));
        return sqlSessionTemplate.insert(statementId, entity);
    }

    public <E extends RootEntity> int insert(final SqlSessionTemplate sqlSessionTemplate, final Collection<E> entities) {
        ValidationUtil.INSTANCE.notEmpty(ExceptionLevel.WARN, entities, "entities must not be empty");
        Iterator<E> iterator = entities.iterator();
        E entity = iterator.next();
        Class<? extends RootEntity> entityClass = entity.getClass();
        String statementId = String.format("%s.%s", entityClass.getName(), "insertBulk");
        STATEMENT_CACHE.computeIfAbsent(statementId, cacheKey -> {
            TableMeta tableMeta = TableMetas.INSTANCE.tableMeta(entityClass);
            // 缓存ID Setter
            cacheIdGenerator(cacheKey, entityClass, tableMeta.getPkColumnMetas());
            Map<String, ColumnMeta> columnMetas = tableMeta.getColumnMetas();
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("<script>");
            sqlBuilder.append("insert into ").append(tableMeta.getTableName()).append("(");
            columnMetas.values().forEach(columnMeta -> sqlBuilder.append(columnMeta.getColumnName()).append(","));
            // 去除最后一个逗号
            sqlBuilder.deleteCharAt(sqlBuilder.lastIndexOf(Symbol.COMMA.getSymbol()));
            sqlBuilder.append(")values");
            sqlBuilder.append("<foreach collection=\"list\" item=\"item\" separator=\",\">");
            sqlBuilder.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
            columnMetas.values().forEach(columnMeta -> {
                String typeHandler = "";
                if (columnMeta.isJson()) {
                    typeHandler = ",typeHandler=wang.liangchen.matrix.framework.data.mybatis.handler.JsonTypeHandler";
                }
                sqlBuilder.append("#{item.").append(columnMeta.getFieldName()).append(typeHandler).append("},");
            });
            sqlBuilder.append("</trim>");
            sqlBuilder.append("</foreach>");
            sqlBuilder.append("</script>");
            String sqlScript = sqlBuilder.toString();
            buildMappedStatement(sqlSessionTemplate, statementId, SqlCommandType.INSERT, sqlScript, Collection.class, Integer.class);
            logger.debug("create and cache insertBulkId:{},sqlScript:{}", statementId, sqlScript);
            return sqlScript;
        });
        populateId(statementId, entities);
        return sqlSessionTemplate.insert(statementId, entities);
    }

    public <E extends RootEntity> int delete(final SqlSessionTemplate sqlSessionTemplate, final E entity) {
        ValidationUtil.INSTANCE.notNull(ExceptionLevel.WARN, entity, "entity must not be null");
        Class<? extends RootEntity> entityClass = entity.getClass();
        String statementId = String.format("%s.%s", entityClass.getName(), "delete");
        STATEMENT_CACHE.computeIfAbsent(statementId, cacheKey -> {
            TableMeta tableMeta = TableMetas.INSTANCE.tableMeta(entityClass);
            StringBuilder sqlBuilder = new StringBuilder();
            ColumnMeta columnDeleteMeta = tableMeta.getColumnDeleteMeta();
            ColumnMeta columnVersionMeta = tableMeta.getColumnVersionMeta();
            sqlBuilder.append("<script>");
            if (null == columnDeleteMeta) {
                sqlBuilder.append("delete from ").append(tableMeta.getTableName());
            } else {
                // 通过扩展字段 增加deleteValue
                entity.addExtendedField("markDeleteValue", columnDeleteMeta.getMarkDeleteValue());
                sqlBuilder.append("update ").append(tableMeta.getTableName());
                sqlBuilder.append(" set ");
                if (null != columnVersionMeta) {
                    sqlBuilder.append("<if test=\"@wang.liangchen.matrix.framework.data.mybatis.Ognl@isNotNull(").append(columnVersionMeta.getFieldName()).append(")\">");
                    sqlBuilder.append(columnVersionMeta.getColumnName()).append("=").append(columnVersionMeta.getColumnName()).append("+1,");
                    sqlBuilder.append("</if>");
                }
                sqlBuilder.append(columnDeleteMeta.getColumnName()).append(Symbol.EQUAL.getSymbol()).append("#{extendedFields.markDeleteValue}");
            }
            sqlBuilder.append(pkWhereSql(tableMeta.getPkColumnMetas(), columnVersionMeta));
            sqlBuilder.append("</script>");
            String sqlScript = sqlBuilder.toString();
            buildMappedStatement(sqlSessionTemplate, statementId, SqlCommandType.DELETE, sqlScript, entityClass, Integer.class);
            logger.debug("create and cache deleteId:{},sqlScript:{}", statementId, sqlScript);
            return sqlScript;
        });
        int rows = sqlSessionTemplate.delete(statementId, entity);
        // 使用后删除 markDeleteValue
        entity.removeExtendedField("markDeleteValue");
        return rows;
    }

    public <E extends RootEntity> int delete(final SqlSessionTemplate sqlSessionTemplate, final CriteriaParameter<E> criteriaParameter) {
        TableMeta tableMeta = criteriaParameter.getTableMeta();
        String statementId = String.format("%s.%s", tableMeta.getEntityClass().getName(), "deleteBulk");
        STATEMENT_CACHE.computeIfAbsent(statementId, cacheKey -> {
            StringBuilder sqlBuilder = new StringBuilder();
            ColumnMeta columnDeleteMeta = tableMeta.getColumnDeleteMeta();
            sqlBuilder.append("<script>");
            if (null == columnDeleteMeta) {
                sqlBuilder.append("delete from ").append(tableMeta.getTableName());
            } else {
                // 通过扩展字段 增加deleteValue
                criteriaParameter.addExtendedField("markDeleteValue", columnDeleteMeta.getMarkDeleteValue());
                sqlBuilder.append("update ").append(tableMeta.getTableName());
                sqlBuilder.append(" set ");
                sqlBuilder.append(columnDeleteMeta.getColumnName()).append(Symbol.EQUAL.getSymbol()).append("#{extendedFields.markDeleteValue}");
            }
            sqlBuilder.append("<where>${whereSql}</where>");
            sqlBuilder.append("</script>");
            String sqlScript = sqlBuilder.toString();
            buildMappedStatement(sqlSessionTemplate, statementId, SqlCommandType.DELETE, sqlScript, CriteriaParameter.class, Integer.class);
            logger.debug("create and cache deleteBulkId:{},sqlScript:{}", statementId, sqlScript);
            return sqlScript;
        });
        return sqlSessionTemplate.delete(statementId, criteriaParameter);
    }

    public <E extends RootEntity> int update(final SqlSessionTemplate sqlSessionTemplate, E entity) {
        ValidationUtil.INSTANCE.notNull(ExceptionLevel.WARN, entity, "entity must not be null");
        Class<? extends RootEntity> entityClass = entity.getClass();
        String statementId = String.format("%s.%s", entityClass.getName(), "update");
        STATEMENT_CACHE.computeIfAbsent(statementId, cacheKey -> {
            TableMeta entityTableMeta = TableMetas.INSTANCE.tableMeta(entityClass);
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("<script>");
            sqlBuilder.append("update ").append(entityTableMeta.getTableName());
            sqlBuilder.append("<set>");
            // 版本列名称
            String fieldName, columnName;
            ColumnMeta columnVersionMeta = null;
            for (ColumnMeta columnMeta : entityTableMeta.getNonPkColumnMetas().values()) {
                fieldName = columnMeta.getFieldName();
                columnName = columnMeta.getColumnName();
                // version column
                if (columnMeta.isVersion()) {
                    columnVersionMeta = columnMeta;
                    sqlBuilder.append("<if test=\"@wang.liangchen.matrix.framework.data.mybatis.Ognl@isNotNull(").append(fieldName).append(")\">");
                    sqlBuilder.append(columnName).append("=").append(columnName).append("+1,");
                    sqlBuilder.append("</if>");
                    continue;
                }

                String typeHandler = columnMeta.isJson() ? ",typeHandler=wang.liangchen.matrix.framework.data.mybatis.handler.JsonTypeHandler" : "";
                // skip forceUpdateColumns
                sqlBuilder.append("<choose><when test=\"forceUpdateColumns.keySet().contains('").append(columnName).append("')\"></when>");
                // only not null columns
                sqlBuilder.append("<when test=\"@wang.liangchen.matrix.framework.data.mybatis.Ognl@isNotNull(").append(fieldName).append(")\">");
                sqlBuilder.append(columnName).append("=#{").append(fieldName).append(typeHandler).append("},");
                sqlBuilder.append("</when></choose>");
            }
            // 更新强制项
            sqlBuilder.append("<foreach collection=\"forceUpdateColumns.entrySet()\" index=\"key\" item=\"item\" separator=\",\">");
            sqlBuilder.append("${key} = #{item}");
            sqlBuilder.append("</foreach>");
            sqlBuilder.append("</set>");
            sqlBuilder.append(pkWhereSql(entityTableMeta.getPkColumnMetas(), columnVersionMeta));
            sqlBuilder.append("</script>");
            String sqlScript = sqlBuilder.toString();
            buildMappedStatement(sqlSessionTemplate, statementId, SqlCommandType.UPDATE, sqlScript, entityClass, Integer.class);
            logger.debug("create and cache updateId:{},sqlScript:{}", statementId, sqlScript);
            return sqlScript;
        });
        return sqlSessionTemplate.update(statementId, entity);
    }

    public <E extends RootEntity> int update(final SqlSessionTemplate sqlSessionTemplate, final CriteriaParameter<E> criteriaParameter) {
        RootEntity entity = criteriaParameter.getEntity();
        Class<? extends RootEntity> entityClass = entity.getClass();
        String statementId = String.format("%s.%s", entityClass.getName(), "updateBulk");
        STATEMENT_CACHE.computeIfAbsent(statementId, cacheKey -> {
            TableMeta entityTableMeta = TableMetas.INSTANCE.tableMeta(entityClass);
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("<script>");
            sqlBuilder.append("update ").append(entityTableMeta.getTableName());
            sqlBuilder.append("<set>");
            entityTableMeta.getNonPkColumnMetas().values().forEach(columnMeta -> {
                String fieldName = columnMeta.getFieldName();
                String columnName = columnMeta.getColumnName();
                String typeHandler = columnMeta.isJson() ? ",typeHandler=wang.liangchen.matrix.framework.data.mybatis.handler.JsonTypeHandler" : "";
                // skip forceUpdateColumns
                sqlBuilder.append("<choose><when test=\"entity.forceUpdateColumns.keySet().contains('").append(columnName).append("')\"></when>");
                // only not null columns
                sqlBuilder.append("<when test=\"@wang.liangchen.matrix.framework.data.mybatis.Ognl@isNotNull(entity.").append(fieldName).append(")\">");
                sqlBuilder.append(columnName).append("=#{entity.").append(fieldName).append(typeHandler).append("},");
                sqlBuilder.append("</when></choose>");
            });

            sqlBuilder.append("<foreach collection=\"entity.forceUpdateColumns.entrySet()\" index=\"key\" item=\"item\" separator=\",\">");
            sqlBuilder.append("${key} = #{item}");
            sqlBuilder.append("</foreach>");
            sqlBuilder.append("</set>");
            sqlBuilder.append("<where>${whereSql}</where>");
            sqlBuilder.append("</script>");
            String sqlScript = sqlBuilder.toString();
            buildMappedStatement(sqlSessionTemplate, statementId, SqlCommandType.UPDATE, sqlScript, UpdateCriteria.class, Integer.class);
            logger.debug("create and cache updateBatchId:{},sqlScript:{}", statementId, sqlScript);
            return sqlScript;
        });
        return sqlSessionTemplate.update(statementId, criteriaParameter);
    }


    public <E extends RootEntity> int count(final SqlSessionTemplate sqlSessionTemplate, final CriteriaParameter<E> criteriaParameter) {
        TableMeta tableMeta = criteriaParameter.getTableMeta();
        String statementId = String.format("%s.%s", tableMeta.getEntityClass().getName(), "count");
        STATEMENT_CACHE.computeIfAbsent(statementId, cacheKey -> {
            StringBuilder sqlBuilder = new StringBuilder();
            // 根据mysql文档，count(0)和count(*)没有性能上的差别,但count(*)符合标准语法; count(column)只计数非null
            sqlBuilder.append("<script>");
            sqlBuilder.append("select count(*) from ").append(tableMeta.getTableName());
            sqlBuilder.append("<where>${whereSql}</where>");
            sqlBuilder.append("</script>");
            buildMappedStatement(sqlSessionTemplate, statementId, SqlCommandType.SELECT, sqlBuilder.toString(), CriteriaParameter.class, Integer.class);
            String sqlScript = sqlBuilder.toString();
            logger.debug("create and cache countId:{},sqlScript:{}", statementId, sqlScript);
            return sqlScript;
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
            sqlBuilder.append("<if test=\"@wang.liangchen.matrix.framework.data.mybatis.Ognl@isNotEmpty(pagination.orderBys)\"> order by <foreach collection=\"pagination.orderBys\" item=\"item\" separator=\",\"> ${item.orderBy} ${item.direction} </foreach></if>");
            sqlBuilder.append("<if test=\"null!=pagination.offset and null!=pagination.rows\">");
            sqlBuilder.append("<choose>");
            sqlBuilder.append("<when test=\"'PostgreSQL'== dataSourceType\">");
            sqlBuilder.append("limit #{pagination.rows} offset #{pagination.offset}");
            sqlBuilder.append("</when>");
            sqlBuilder.append("<otherwise>");
            sqlBuilder.append("limit #{pagination.offset},#{pagination.rows}");
            sqlBuilder.append("</otherwise>");
            sqlBuilder.append("</choose>");
            sqlBuilder.append("</if>");
            sqlBuilder.append("</script>");
            String sqlScript = sqlBuilder.toString();
            buildMappedStatement(sqlSessionTemplate, cacheKey, SqlCommandType.SELECT, sqlScript, CriteriaParameter.class, entityClass, tableMeta);
            logger.debug("create and cache listId:{},sqlScript:{}", statementId, sqlScript);
            return sqlScript;
        });
        return sqlSessionTemplate.selectList(statementId, criteriaParameter);
    }

    private void cacheIdGenerator(String cacheKey, Class<? extends RootEntity> entityClass, Map<String, ColumnMeta> pkColumnMetas) {
        if (1 != pkColumnMetas.size()) {
            return;
        }
        for (ColumnMeta columnMeta : pkColumnMetas.values()) {
            String setterMethod = StringUtil.INSTANCE.getSetter(columnMeta.getFieldName());
            String getterMethod = StringUtil.INSTANCE.getGetter(columnMeta.getFieldName());
            ID_METHOD_CACHE.put(cacheKey, new IDGenerator(getterMethod, setterMethod, columnMeta.getIdStrategy()));
        }
    }

    private <E extends RootEntity> void populateId(String cacheKey, Collection<E> entities) {
        IDGenerator idGenerator = ID_METHOD_CACHE.get(cacheKey);
        if (null == idGenerator) {
            return;
        }
        IdStrategy.Strategy strategy = idGenerator.getStrategy();
        if (null == strategy || IdStrategy.Strategy.NONE == strategy) {
            return;
        }
        String setterMethod = idGenerator.getSetterMethod();
        for (E entity : entities) {
            if (IdStrategy.Strategy.MatrixFlake == strategy) {
                ClassUtil.INSTANCE.invokeSetter(entity, setterMethod, NumbericUid.INSTANCE.nextId());
            }
        }
    }

    private StringBuilder pkWhereSql(Map<String, ColumnMeta> pkColumnMetas, ColumnMeta columnVersionMeta) {
        StringBuilder whereSql = new StringBuilder();
        whereSql.append("<where>");
        pkColumnMetas.values().forEach(columnMeta -> whereSql.append("and ")
                .append(columnMeta.getColumnName()).append("=#{").append(columnMeta.getFieldName()).append("}"));
        if (null != columnVersionMeta) {
            whereSql.append("<if test=\"@wang.liangchen.matrix.framework.data.mybatis.Ognl@isNotNull(").append(columnVersionMeta.getFieldName()).append(")\">");
            whereSql.append(" and ").append(columnVersionMeta.getColumnName()).append("=").append("#{").append(columnVersionMeta.getColumnName()).append("}");
            whereSql.append("</if>");
        }
        whereSql.append("</where>");
        return whereSql;
    }

    private void buildMappedStatement(SqlSessionTemplate sqlSessionTemplate, String mappedStatementId, SqlCommandType sqlCommandType, String sqlScript, Class<?> parameterType, Class<?> resultType) {
        buildMappedStatement(sqlSessionTemplate, mappedStatementId, sqlCommandType, sqlScript, parameterType, resultType, null);
    }

    private void buildMappedStatement(SqlSessionTemplate sqlSessionTemplate, String mappedStatementId, SqlCommandType sqlCommandType, String sqlScript, Class<?> parameterType, Class<?> resultType, TableMeta tableMeta) {
        Configuration configuration = sqlSessionTemplate.getConfiguration();
        List<ResultMapping> resultMappings = new ArrayList<>();
        if (null != tableMeta) {
            tableMeta.getColumnMetas().forEach((k, v) -> {
                if (v.isJson()) {
                    resultMappings.add(new ResultMapping.Builder(configuration, k, v.getColumnName(), new JsonTypeHandler(v.getFieldClass(), v.getFieldType())).build());
                } else {
                    resultMappings.add(new ResultMapping.Builder(configuration, k, v.getColumnName(), v.getFieldClass()).build());
                }
            });
        }
        List<ResultMap> resultMaps = new ArrayList<ResultMap>() {{
            add(new ResultMap.Builder(configuration, "defaultResultMap", resultType, resultMappings).build());
        }};
        LanguageDriver languageDriver = configuration.getDefaultScriptingLanguageInstance();
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sqlScript, parameterType);
        MappedStatement ms = new MappedStatement.Builder(configuration, mappedStatementId, sqlSource, sqlCommandType).resultMaps(resultMaps).build();
        configuration.addMappedStatement(ms);
    }

    static class IDGenerator {
        private final String getterMethod;
        private final String setterMethod;
        private final IdStrategy.Strategy strategy;

        public IDGenerator(String getterMethod, String setterMethod, IdStrategy.Strategy strategy) {
            this.getterMethod = getterMethod;
            this.setterMethod = setterMethod;
            this.strategy = strategy;
        }

        public String getGetterMethod() {
            return getterMethod;
        }

        public String getSetterMethod() {
            return setterMethod;
        }

        public IdStrategy.Strategy getStrategy() {
            return strategy;
        }
    }
}
