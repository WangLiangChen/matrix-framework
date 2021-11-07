package wang.liangchen.matrix.framework.data.dao;


import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.ClassUtils;
import wang.liangchen.matrix.framework.data.util.SqlBuilder;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.List;
import java.util.Map;

/**
 * @author Liangchen.Wang 2021-10-19 18:35
 */
public abstract class AbstractDao {
    private final static Logger logger = LoggerFactory.getLogger(AbstractDao.class);
    @Inject
    protected JdbcTemplate jdbcTemplate;
    @Inject
    protected SqlSessionTemplate sqlSessionTemplate;
    @PersistenceContext
    protected EntityManager entityManager;

    protected <I> I getMapper(Class<I> type) {
        return this.sqlSessionTemplate.getMapper(type);
    }

    protected int executeSql(SqlBuilder sqlBuilder) {
        return jdbcTemplate.update(sqlBuilder.getSql(), sqlBuilder.getArgs());
    }

    protected int[] executeBatchSql(SqlBuilder sqlBuilder) {
        return jdbcTemplate.batchUpdate(sqlBuilder.getSql(), sqlBuilder.getBatchArgs());
    }

    protected <E> E queryForObject(Class<E> clazz, SqlBuilder sqlBuilder) {
        // queryForObject throw a DataAccessException while the ResultSet is empty
        try {
            if (ClassUtils.isPrimitiveOrWrapper(clazz) || String.class == clazz) {
                return jdbcTemplate.queryForObject(sqlBuilder.getSql(), clazz, sqlBuilder.getArgs());
            }
            return jdbcTemplate.queryForObject(sqlBuilder.getSql(), BeanPropertyRowMapper.newInstance(clazz), sqlBuilder.getArgs());
        } catch (DataAccessException e) {
            return null;
        }
    }

    protected Map<String, Object> queryForMap(SqlBuilder sqlBuilder) {
        return jdbcTemplate.queryForMap(sqlBuilder.getSql(), sqlBuilder.getArgs());
    }

    protected <E> List<E> queryForList(SqlBuilder sqlBuilder, Class<E> clazz) {
        if (ClassUtils.isPrimitiveOrWrapper(clazz) || String.class == clazz) {
            return jdbcTemplate.queryForList(sqlBuilder.getSql(), clazz, sqlBuilder.getArgs());
        }
        return jdbcTemplate.query(sqlBuilder.getSql(), BeanPropertyRowMapper.newInstance(clazz), sqlBuilder.getArgs());
    }

    protected List<Map<String, Object>> queryForList(SqlBuilder sqlBuilder) {
        return jdbcTemplate.queryForList(sqlBuilder.getSql(), sqlBuilder.getArgs());
    }

    protected ResultSetMetaData queryForMetaData(SqlBuilder sqlBuilder) {
        return jdbcTemplate.query(sqlBuilder.getSql(), ResultSet::getMetaData);
    }
}
