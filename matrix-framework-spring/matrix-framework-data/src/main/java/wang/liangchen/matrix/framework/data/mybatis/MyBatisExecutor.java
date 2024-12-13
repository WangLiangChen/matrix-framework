package wang.liangchen.matrix.framework.data.mybatis;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.liangchen.matrix.framework.commons.StringUtil;
import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.type.ClassUtil;
import wang.liangchen.matrix.framework.commons.uid.NanoIdUtil;
import wang.liangchen.matrix.framework.commons.uid.NumbericUid;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;
import wang.liangchen.matrix.framework.data.annotation.IdStrategy;
import wang.liangchen.matrix.framework.data.criteria.CriteriaParameter;
import wang.liangchen.matrix.framework.data.datasource.dialect.PostgreSQLDialect;
import wang.liangchen.matrix.framework.data.entity.RootEntity;
import wang.liangchen.matrix.framework.data.mybatis.handler.ExtendedColumnTypeHandler;
import wang.liangchen.matrix.framework.data.mybatis.handler.JsonTypeHandler;
import wang.liangchen.matrix.framework.data.resolver.EntityMeta;
import wang.liangchen.matrix.framework.data.resolver.FieldLabel;
import wang.liangchen.matrix.framework.data.resolver.FieldMeta;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author Liangchen.Wang
 * Mybatis Statement Cache And Executor
 */
public enum MyBatisExecutor {
    /**
     * instance
     */
    INSTANCE;
    private final Logger logger = LoggerFactory.getLogger(MyBatisExecutor.class);
    private final Map<String, String> STATEMENT_CACHE = new ConcurrentHashMap<>(128);
    private final Map<String, IDGenerator> ID_METHOD_CACHE = new ConcurrentHashMap<>(128);
    private final TransmittableThreadLocal<EntityMeta> entityMetaContext = new TransmittableThreadLocal<>();

    public <E extends RootEntity> int insert(final SqlSessionTemplate sqlSessionTemplate, final E entity) {
        ValidationUtil.INSTANCE.notNull(entity, "The entity can not be null");
        Class<? extends RootEntity> entityClass = entity.getClass();
        String statementId = String.format("%s.%s", entityClass.getName(), "insert");
        EntityMeta entityMeta = entity.getEntityMeta();
        STATEMENT_CACHE.computeIfAbsent(statementId, cacheKey -> {
            String tableName = entityMeta.getTableName();
            // 缓存单一ID的setterMethod
            IdStrategy.Strategy idStrategy = cacheIdGenerator(cacheKey, entityMeta.getPkFieldMeta());
            // all columns or non pk columns
            Collection<FieldMeta> columnMetas = IdStrategy.Strategy.AUTO_INCREMENT == idStrategy ? entityMeta.getNonPkFieldMetas().values() : entityMeta.getFieldMetas().values();
            StringBuilder sqlBuilder = new StringBuilder();
            String insertedColumnNames = columnMetas.stream().map(FieldMeta::getColumnName).collect(Collectors.joining(Symbol.COMMA.getSymbol()));
            sqlBuilder.append("<script>");
            sqlBuilder.append("insert into ").append(tableName).append(" (");
            sqlBuilder.append(insertedColumnNames);
            sqlBuilder.append(") values (");
            columnMetas.forEach(columnMeta -> {
                String typeHandler = "";
                if (columnMeta.checkFieldLabel(FieldLabel.JSON)) {
                    typeHandler = ", typeHandler=wang.liangchen.matrix.framework.data.mybatis.handler.JsonTypeHandler";
                } else if (columnMeta.checkFieldLabel(FieldLabel.EXTENDED)) {
                    typeHandler = ", typeHandler=wang.liangchen.matrix.framework.data.mybatis.handler.ExtendedColumnTypeHandler";
                }
                sqlBuilder.append("#{").append(columnMeta.getFieldName()).append(typeHandler).append("},");
            });
            // 去除最后一个逗号
            sqlBuilder.deleteCharAt(sqlBuilder.lastIndexOf(Symbol.COMMA.getSymbol()));
            sqlBuilder.append(")");
            sqlBuilder.append("</script>");
            String sqlScript = sqlBuilder.toString();
            buildMappedStatement(sqlSessionTemplate, statementId, SqlCommandType.INSERT, sqlScript, entityClass, Integer.class, entityMeta);
            logger.debug("create and cache insertId:{}, sqlScript:{}", statementId, sqlScript);
            return sqlScript;
        });
        populateId(statementId, Collections.singletonList(entity));
        return populateContext(entityMeta, () -> sqlSessionTemplate.insert(statementId, entity));
    }

    public <E extends RootEntity> int insert(final SqlSessionTemplate sqlSessionTemplate, final Collection<E> entities) {
        ValidationUtil.INSTANCE.notNullAndEmpty(entities, "{Collection.NotEmpty}");
        Iterator<E> iterator = entities.iterator();
        E entity = iterator.next();
        Class<? extends RootEntity> entityClass = entity.getClass();
        String statementId = String.format("%s.%s", entityClass.getName(), "insertBulk");
        EntityMeta entityMeta = entity.getEntityMeta();
        STATEMENT_CACHE.computeIfAbsent(statementId, cacheKey -> {
            String tableName = entityMeta.getTableName();
            // 缓存单一ID的setterMethod
            IdStrategy.Strategy idStrategy = cacheIdGenerator(cacheKey, entityMeta.getPkFieldMeta());
            // all columns or non pk columns
            Collection<FieldMeta> columnMetas = IdStrategy.Strategy.AUTO_INCREMENT == idStrategy ? entityMeta.getNonPkFieldMetas().values() : entityMeta.getFieldMetas().values();
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("<script>");
            sqlBuilder.append("insert into ").append(tableName).append(" (");
            String insertedColumnNames = columnMetas.stream().map(FieldMeta::getColumnName).collect(Collectors.joining(Symbol.COMMA.getSymbol()));
            sqlBuilder.append(insertedColumnNames);
            sqlBuilder.append(") values ");
            sqlBuilder.append("<foreach collection=\"collection\" item=\"item\" separator=\",\">");
            sqlBuilder.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
            columnMetas.forEach(columnMeta -> {
                String typeHandler = "";
                if (columnMeta.checkFieldLabel(FieldLabel.JSON)) {
                    typeHandler = ", typeHandler=wang.liangchen.matrix.framework.data.mybatis.handler.JsonTypeHandler";
                } else if (columnMeta.checkFieldLabel(FieldLabel.EXTENDED)) {
                    typeHandler = ", typeHandler=wang.liangchen.matrix.framework.data.mybatis.handler.ExtendedColumnTypeHandler";
                }
                sqlBuilder.append("#{item.").append(columnMeta.getFieldName()).append(typeHandler).append("},");
            });
            sqlBuilder.append("</trim>");
            sqlBuilder.append("</foreach>");
            sqlBuilder.append("</script>");
            String sqlScript = sqlBuilder.toString();
            buildMappedStatement(sqlSessionTemplate, statementId, SqlCommandType.INSERT, sqlScript, Collection.class, Integer.class, entityMeta);
            logger.debug("create and cache insertBulkId:{}, sqlScript:{}", statementId, sqlScript);
            return sqlScript;
        });
        populateId(statementId, entities);
        return populateContext(entityMeta, () -> sqlSessionTemplate.insert(statementId, entities));
    }

    public <E extends RootEntity> int delete(final SqlSessionTemplate sqlSessionTemplate, final E entity) {
        ValidationUtil.INSTANCE.notNull(entity, "{Parameter.NotNull}");
        Class<? extends RootEntity> entityClass = entity.getClass();
        String statementId = String.format("%s.%s", entityClass.getName(), "delete");
        EntityMeta entityMeta = entity.getEntityMeta();
        STATEMENT_CACHE.computeIfAbsent(statementId, cacheKey -> {
            FieldMeta softDeleteFieldMeta = entityMeta.getSoftDeleteFieldMeta();
            if (null != softDeleteFieldMeta) {
                entity.addExtendedField("softDeleteValue", softDeleteFieldMeta.getSoftDeleteValue());
            }
            String tableName = entityMeta.getTableName();
            FieldMeta versionFieldMeta = entityMeta.getVersionFieldMeta();
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("<script>");
            if (null == softDeleteFieldMeta) {
                sqlBuilder.append("delete from ").append(tableName);
            } else {
                sqlBuilder.append("update ").append(tableName).append("<set>");
                if (null != versionFieldMeta) {
                    sqlBuilder.append("<if test=\"null!=").append(versionFieldMeta.getFieldName()).append("\">");
                    sqlBuilder.append(versionFieldMeta.getColumnName()).append("=").append(versionFieldMeta.getColumnName()).append("+1, ");
                    sqlBuilder.append("</if>");
                }
                sqlBuilder.append(softDeleteFieldMeta.getColumnName()).append(Symbol.EQUAL.getSymbol()).append("#{extendedFields.softDeleteValue}");
                sqlBuilder.append("</set>");
            }
            sqlBuilder.append(pkWhereSql(entityMeta.getPkFieldMetas(), versionFieldMeta));
            sqlBuilder.append("</script>");
            String sqlScript = sqlBuilder.toString();
            buildMappedStatement(sqlSessionTemplate, statementId, SqlCommandType.DELETE, sqlScript, entityClass, Integer.class, entityMeta);
            logger.debug("create and cache deleteId:{},sqlScript:{}", statementId, sqlScript);
            return sqlScript;
        });
        int rows = sqlSessionTemplate.delete(statementId, entity);
        // 使用后删除 markDeleteValue
        entity.removeExtendedField("softDeleteValue");
        return rows;
    }

    public <E extends RootEntity> int delete(final SqlSessionTemplate sqlSessionTemplate, final CriteriaParameter<E> criteriaParameter) {
        String entityClassName = criteriaParameter.getEntityClass().getName();
        String statementId = String.format("%s.%s", entityClassName, "deleteBulk");
        STATEMENT_CACHE.computeIfAbsent(statementId, cacheKey -> {
            String tableName = criteriaParameter.getTableName();
            FieldMeta softDeleteFieldMeta = criteriaParameter.getEntityMeta().getSoftDeleteFieldMeta();
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("<script>");
            if (null == softDeleteFieldMeta) {
                sqlBuilder.append("delete from ").append(tableName);
            } else {
                sqlBuilder.append("<choose><when test=\"true==hardDelete\">");
                sqlBuilder.append("delete from ").append(tableName);
                sqlBuilder.append("</when><otherwise>");
                sqlBuilder.append("update ").append(tableName).append("<set>");
                sqlBuilder.append(softDeleteFieldMeta.getColumnName()).append(Symbol.EQUAL.getSymbol()).append("#{softDeleteColumnValue}");
                sqlBuilder.append("</set></otherwise></choose>");
            }
            sqlBuilder.append("<where>${whereSql}</where>");
            sqlBuilder.append("</script>");
            String sqlScript = sqlBuilder.toString();
            buildMappedStatement(sqlSessionTemplate, statementId, SqlCommandType.DELETE, sqlScript, CriteriaParameter.class, Integer.class, criteriaParameter.getEntityMeta());
            logger.debug("create and cache deleteBulkId:{},sqlScript:{}", statementId, sqlScript);
            return sqlScript;
        });
        return sqlSessionTemplate.delete(statementId, criteriaParameter);
    }

    public <E extends RootEntity> int update(final SqlSessionTemplate sqlSessionTemplate, E entity) {
        ValidationUtil.INSTANCE.notNull(entity, "{Parameter.NotNull}");
        Class<? extends RootEntity> entityClass = entity.getClass();
        String statementId = String.format("%s.%s", entityClass.getName(), "update");
        EntityMeta entityMeta = entity.getEntityMeta();
        String tableName = entityMeta.getTableName();
        STATEMENT_CACHE.computeIfAbsent(statementId, cacheKey -> {
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("<script>");
            sqlBuilder.append("update ").append(tableName).append("<set>");
            // 版本列名称
            String fieldName, columnName;
            FieldMeta versionFieldMeta = null;
            for (FieldMeta fieldMeta : entityMeta.getNonPkFieldMetas().values()) {
                fieldName = fieldMeta.getFieldName();
                columnName = fieldMeta.getColumnName();
                // version column
                if (fieldMeta.checkFieldLabel(FieldLabel.VERSION)) {
                    versionFieldMeta = fieldMeta;
                    sqlBuilder.append("<if test=\"null!=").append(fieldName).append("\">");
                    sqlBuilder.append(columnName).append("=").append(columnName).append("+1,");
                    sqlBuilder.append("</if>");
                    continue;
                }

                String typeHandler = "";
                if (fieldMeta.checkFieldLabel(FieldLabel.JSON)) {
                    typeHandler = ",typeHandler=wang.liangchen.matrix.framework.data.mybatis.handler.JsonTypeHandler";
                } else if (fieldMeta.checkFieldLabel(FieldLabel.EXTENDED)) {
                    typeHandler = ",typeHandler=wang.liangchen.matrix.framework.data.mybatis.handler.ExtendedColumnTypeHandler";
                }
                // only not null columns
                sqlBuilder.append("<if test=\"null!=").append(fieldName).append("\">");
                sqlBuilder.append(columnName).append("=#{").append(fieldName).append(typeHandler).append("},");
                sqlBuilder.append("</if>");
            }
            // updateToNullColumns
            sqlBuilder.append("<foreach collection=\"updateToNullColumns\" index=\"index\" item=\"item\" separator=\",\">");
            sqlBuilder.append("${item} = null");
            sqlBuilder.append("</foreach></set>");
            sqlBuilder.append(pkWhereSql(entityMeta.getPkFieldMetas(), versionFieldMeta));
            sqlBuilder.append("</script>");
            String sqlScript = sqlBuilder.toString();
            buildMappedStatement(sqlSessionTemplate, statementId, SqlCommandType.UPDATE, sqlScript, entityClass, Integer.class, entityMeta);
            logger.debug("create and cache updateId:{},sqlScript:{}", statementId, sqlScript);
            return sqlScript;
        });
        return populateContext(entityMeta, () -> sqlSessionTemplate.update(statementId, entity));
    }

    public <E extends RootEntity> int update(final SqlSessionTemplate sqlSessionTemplate, final CriteriaParameter<E> criteriaParameter) {
        Class<? extends RootEntity> entityClass = criteriaParameter.getEntityClass();
        String statementId = String.format("%s.%s", entityClass.getName(), "updateBulk");
        EntityMeta entityMeta = criteriaParameter.getEntityMeta();
        String tableName = entityMeta.getTableName();
        STATEMENT_CACHE.computeIfAbsent(statementId, cacheKey -> {
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("<script>");
            sqlBuilder.append("update ").append(tableName).append("<set>");
            entityMeta.getNonPkFieldMetas().values().forEach(fieldMeta -> {
                String fieldName = fieldMeta.getFieldName();
                String columnName = fieldMeta.getColumnName();
                String typeHandler = "";
                if (fieldMeta.checkFieldLabel(FieldLabel.JSON)) {
                    typeHandler = ",typeHandler=wang.liangchen.matrix.framework.data.mybatis.handler.JsonTypeHandler";
                } else if (fieldMeta.checkFieldLabel(FieldLabel.EXTENDED)) {
                    typeHandler = ",typeHandler=wang.liangchen.matrix.framework.data.mybatis.handler.ExtendedColumnTypeHandler";
                }
                // only not null columns
                sqlBuilder.append("<if test=\"null!=entity.").append(fieldName).append("\">");
                sqlBuilder.append(columnName).append("=#{entity.").append(fieldName).append(typeHandler).append("},");
                sqlBuilder.append("</if>");
            });

            sqlBuilder.append("<foreach collection=\"entity.updateToNullColumns\" index=\"index\" item=\"item\" separator=\",\">");
            sqlBuilder.append("${item} = null");
            sqlBuilder.append("</foreach></set>");
            sqlBuilder.append("<where>${whereSql}</where>");
            sqlBuilder.append("</script>");
            String sqlScript = sqlBuilder.toString();
            buildMappedStatement(sqlSessionTemplate, statementId, SqlCommandType.UPDATE, sqlScript, CriteriaParameter.class, Integer.class, entityMeta);
            logger.debug("create and cache updateBatchId:{},sqlScript:{}", statementId, sqlScript);
            return sqlScript;
        });
        return populateContext(entityMeta, () -> sqlSessionTemplate.update(statementId, criteriaParameter));
    }


    public <E extends RootEntity> int count(final SqlSessionTemplate sqlSessionTemplate, final CriteriaParameter<E> criteriaParameter) {
        Class<? extends RootEntity> entityClass = criteriaParameter.getEntityClass();
        String statementId = String.format("%s.%s", entityClass.getName(), "count");
        String tableName = criteriaParameter.getTableName();
        STATEMENT_CACHE.computeIfAbsent(statementId, cacheKey -> {
            StringBuilder sqlBuilder = new StringBuilder();
            // 根据mysql文档，count(0)和count(*)没有性能上的差别,但count(*)符合标准语法; count(column)只计数非null
            sqlBuilder.append("<script>");
            sqlBuilder.append("select count(*) from ").append(tableName);
            sqlBuilder.append("<where>${whereSql}</where>");
            sqlBuilder.append("</script>");
            buildMappedStatement(sqlSessionTemplate, statementId, SqlCommandType.SELECT, sqlBuilder.toString(), CriteriaParameter.class, Integer.class, criteriaParameter.getEntityMeta());
            String sqlScript = sqlBuilder.toString();
            logger.debug("create and cache countId:{},sqlScript:{}", statementId, sqlScript);
            return sqlScript;
        });
        return sqlSessionTemplate.selectOne(statementId, criteriaParameter);
    }

    public <E extends RootEntity> List<E> list(final SqlSessionTemplate sqlSessionTemplate, final CriteriaParameter<E> criteriaParameter) {
        Class<? extends RootEntity> entityClass = criteriaParameter.getEntityClass();
        String statementId = String.format("%s.%s", entityClass.getName(), "list");
        String tableName = criteriaParameter.getTableName();
        STATEMENT_CACHE.computeIfAbsent(statementId, cacheKey -> {
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("<script>");
            sqlBuilder.append("select ");
            sqlBuilder.append("<if test=\"true==distinct\">").append(" distinct ").append("</if>");
            sqlBuilder.append("<trim suffixOverrides=\",\"><foreach collection=\"selectColumns\" item=\"item\" index=\"index\" separator=\",\">${item}</foreach></trim>");
            sqlBuilder.append("from ").append(tableName);
            sqlBuilder.append("<where>${whereSql}</where>");
            sqlBuilder.append("<if test=\"true==forUpdate\">").append("for update").append("</if>");
            sqlBuilder.append("<if test=\"@wang.liangchen.matrix.framework.data.mybatis.Ognl@isNotEmpty(pagination.orderBys)\"> order by <foreach collection=\"pagination.orderBys\" item=\"item\" separator=\",\"> ${item.orderBy} ${item.direction} </foreach></if>");
            sqlBuilder.append("<if test=\"null!=pagination.offset and null!=pagination.rows\">");
            sqlBuilder.append("<choose>");
            sqlBuilder.append("<when test=\"'").append(PostgreSQLDialect.DRIVER_CLASS_NAME).append("'== driverClassName\">");
            sqlBuilder.append("limit #{pagination.rows} offset #{pagination.offset}");
            sqlBuilder.append("</when>");
            sqlBuilder.append("<otherwise>");
            sqlBuilder.append("limit #{pagination.offset},#{pagination.rows}");
            sqlBuilder.append("</otherwise>");
            sqlBuilder.append("</choose>");
            sqlBuilder.append("</if>");
            sqlBuilder.append("</script>");
            String sqlScript = sqlBuilder.toString();
            buildMappedStatement(sqlSessionTemplate, cacheKey, SqlCommandType.SELECT, sqlScript, CriteriaParameter.class, entityClass, criteriaParameter.getEntityMeta());
            logger.debug("create and cache listId:{},sqlScript:{}", statementId, sqlScript);
            return sqlScript;
        });
        return populateContext(criteriaParameter.getEntityMeta(), () -> sqlSessionTemplate.selectList(statementId, criteriaParameter));
    }

    private <T> T populateContext(EntityMeta entityMeta, Supplier<T> supplier) {
        // populate context
        entityMetaContext.set(entityMeta);
        try {
            return supplier.get();
        } finally {
            entityMetaContext.remove();
        }
    }

    private IdStrategy.Strategy cacheIdGenerator(String cacheKey, FieldMeta pkFieldMeta) {
        // if composite id, skip
        if (null == pkFieldMeta) {
            return IdStrategy.Strategy.NONE;
        }
        // if auto increment, skip
        IdStrategy.Strategy idStrategy = pkFieldMeta.getIdStrategy();
        if (IdStrategy.Strategy.AUTO_INCREMENT == idStrategy) {
            return IdStrategy.Strategy.AUTO_INCREMENT;
        }
        // cache method
        String setterMethod = StringUtil.INSTANCE.getSetter(pkFieldMeta.getFieldName());
        String getterMethod = StringUtil.INSTANCE.getGetter(pkFieldMeta.getFieldName());
        ID_METHOD_CACHE.put(cacheKey, new IDGenerator(pkFieldMeta.getFieldClass(), getterMethod, setterMethod, idStrategy));
        return idStrategy;
    }

    private <E extends RootEntity> void populateId(String cacheKey, Collection<E> entities) {
        IDGenerator idGenerator = ID_METHOD_CACHE.get(cacheKey);
        if (null == idGenerator) {
            return;
        }
        IdStrategy.Strategy strategy = idGenerator.getStrategy();
        if (null == strategy || IdStrategy.Strategy.NONE == strategy || IdStrategy.Strategy.AUTO_INCREMENT == strategy) {
            return;
        }
        Class<?> valueClass = idGenerator.getValueClass();
        String setterMethod = idGenerator.getSetterMethod();
        String getterMethod = idGenerator.getGetterMethod();
        for (E entity : entities) {
            Object getterValue = ClassUtil.INSTANCE.invokeGetter(entity, getterMethod);
            String getterValueString = String.valueOf(getterValue);
            if (null != getterValue && !getterValueString.isEmpty() && !"0".equals(getterValueString)) {
                continue;
            }
            Object value = null;
            // populate id if null
            switch (strategy) {
                case UUID:
                    value = UUID.randomUUID().toString();
                    break;
                case NANO:
                    value = NanoIdUtil.INSTANCE.randomNanoId();
                    break;
                default:
                    value = NumbericUid.INSTANCE.nextId();
                    break;
            }

            if (String.class.isAssignableFrom(valueClass)) {
                value = String.valueOf(value);
            }
            ClassUtil.INSTANCE.invokeSetter(entity, setterMethod, value);
        }
    }

    private StringBuilder pkWhereSql(Map<String, FieldMeta> pkFieldMetas, FieldMeta versionFieldMeta) {
        StringBuilder whereSql = new StringBuilder();
        whereSql.append("<where>");
        pkFieldMetas.values().forEach(fieldMeta -> whereSql.append("and ")
                .append(fieldMeta.getColumnName()).append("=#{").append(fieldMeta.getFieldName()).append("}"));
        if (null != versionFieldMeta) {
            whereSql.append("<if test=\"@wang.liangchen.matrix.framework.data.mybatis.Ognl@isNotNull(").append(versionFieldMeta.getFieldName()).append(")\">");
            whereSql.append(" and ").append(versionFieldMeta.getColumnName()).append("=").append("#{").append(versionFieldMeta.getFieldName()).append("}");
            whereSql.append("</if>");
        }
        whereSql.append("</where>");
        return whereSql;
    }


    private void buildMappedStatement(SqlSessionTemplate sqlSessionTemplate, String mappedStatementId, SqlCommandType sqlCommandType, String sqlScript, Class<?> parameterType, Class<?> resultType, EntityMeta entityMeta) {
        Configuration configuration = sqlSessionTemplate.getConfiguration();
        LanguageDriver languageDriver = configuration.getDefaultScriptingLanguageInstance();
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sqlScript, parameterType);
        MappedStatement.Builder statementBuilder = new MappedStatement.Builder(configuration, mappedStatementId, sqlSource, sqlCommandType);
        if (SqlCommandType.INSERT == sqlCommandType) {
            FieldMeta pkFieldMeta = entityMeta.getPkFieldMeta();
            // auto increment
            if (null != pkFieldMeta && IdStrategy.Strategy.AUTO_INCREMENT == pkFieldMeta.getIdStrategy()) {
                statementBuilder.keyGenerator(new Jdbc3KeyGenerator()).keyProperty(pkFieldMeta.getFieldName()).keyColumn(pkFieldMeta.getColumnName());
            }
            configuration.addMappedStatement(statementBuilder.build());
            return;
        }
        if (SqlCommandType.SELECT == sqlCommandType) {
            List<ResultMapping> resultMappings = new ArrayList<>();
            // result mappings
            entityMeta.getFieldMetas().forEach((k, v) -> {
                if (v.checkFieldLabel(FieldLabel.JSON)) {
                    resultMappings.add(new ResultMapping.Builder(configuration, k, v.getColumnName(), new JsonTypeHandler(v.getFieldClass(), v.getFieldType())).build());
                } else if (v.checkFieldLabel(FieldLabel.EXTENDED)) {
                    resultMappings.add(new ResultMapping.Builder(configuration, k, v.getColumnName(), new ExtendedColumnTypeHandler(v.getFieldClass(), v.getFieldType())).build());
                } else {
                    resultMappings.add(new ResultMapping.Builder(configuration, k, v.getColumnName(), v.getFieldClass()).build());
                }
            });
            List<ResultMap> resultMaps = new ArrayList<ResultMap>() {{
                add(new ResultMap.Builder(configuration, "defaultResultMap", resultType, resultMappings).build());
            }};
            statementBuilder.resultMaps(resultMaps);
            configuration.addMappedStatement(statementBuilder.build());
        }
    }

    static class IDGenerator {
        private final Class<?> valueClass;
        private final String getterMethod;
        private final String setterMethod;
        private final IdStrategy.Strategy strategy;

        public IDGenerator(Class<?> valueClass, String getterMethod, String setterMethod, IdStrategy.Strategy strategy) {
            this.valueClass = valueClass;
            this.getterMethod = getterMethod;
            this.setterMethod = setterMethod;
            this.strategy = strategy;
        }

        public Class<?> getValueClass() {
            return valueClass;
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
