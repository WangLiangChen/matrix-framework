package liangchen.wang.matrix.framework.data.dao;

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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MybatisStatementIdBuilder {
    protected final Logger logger = LoggerFactory.getLogger(MybatisStatementIdBuilder.class);
    private final SqlSessionTemplate sqlSessionTemplate;
    private final EntityMeta entityMeta;

    public MybatisStatementIdBuilder(SqlSessionTemplate sqlSessionTemplate, EntityMeta entityMeta) {
        this.sqlSessionTemplate = sqlSessionTemplate;
        this.entityMeta = entityMeta;
    }

    private final static Map<String, String> statementMap = new ConcurrentHashMap<>(100);

    protected String insertId(Class<? extends RootEntity> entityClass) {
        String entityClassName = entityClass.getName();
        String cacheKey = String.format("%s.%s", entityClassName, "insert");
        statementMap.computeIfAbsent(cacheKey, statementId -> {
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
            return sql;
        });
        return cacheKey;
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
