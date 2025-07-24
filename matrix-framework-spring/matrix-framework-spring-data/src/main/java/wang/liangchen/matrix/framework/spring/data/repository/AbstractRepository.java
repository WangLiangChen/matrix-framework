package wang.liangchen.matrix.framework.spring.data.repository;

import jakarta.inject.Inject;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;

import java.sql.DatabaseMetaData;
import java.sql.ResultSetMetaData;

/**
 * @author Liangchen.Wang 2022-06-15 7:56
 */
public abstract class AbstractRepository implements IRepository {
    private final static Logger logger = LoggerFactory.getLogger(AbstractRepository.class);
    @Inject
    protected JdbcTemplate jdbcTemplate;
    @Inject
    protected SqlSessionTemplate sqlSessionTemplate;

    protected <I> I getMyBatisMapper(Class<I> type) {
        return this.sqlSessionTemplate.getMapper(type);
    }

    protected DatabaseMetaData databaseMetaData() {
        try {
            return this.jdbcTemplate.getDataSource().getConnection().getMetaData();
        } catch (Exception e) {
            throw new MatrixErrorException("Failed to get database metadata", e);
        }
    }

    protected ResultSetMetaData resultSetMetaData(String sql) {
        try {
            return this.jdbcTemplate.queryForObject(sql, ResultSetMetaData.class);
        } catch (Exception e) {
            throw new MatrixErrorException("Failed to get result set metadata", e);
        }
    }


}
