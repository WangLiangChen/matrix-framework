package liangchen.wang.matrix.framework.data.dao;

import liangchen.wang.matrix.framework.commons.exception.AssertUtil;
import liangchen.wang.matrix.framework.commons.utils.CollectionUtil;
import liangchen.wang.matrix.framework.data.dao.entity.RootEntity;
import liangchen.wang.matrix.framework.data.mybatis.MybatisStatementIdBuilder;
import liangchen.wang.matrix.framework.data.query.RootQuery;
import org.springframework.stereotype.Repository;

import java.lang.reflect.TypeVariable;
import java.util.List;

/**
 * @author Liangchen.Wang 2021-10-20 14:21
 */
@Repository
public class StandaloneCommandDao extends AbstractDao {

    public int insert(RootEntity entity) {
        AssertUtil.INSTANCE.notNull(entity, "entity can not be null");
        String insertId = MybatisStatementIdBuilder.INSTANCE.insertId(sqlSessionTemplate, entity.getClass());
        return sqlSessionTemplate.insert(insertId, entity);
    }

    public int insertBatch(List<? extends RootEntity> entities) {
        AssertUtil.INSTANCE.notEmpty(entities, "entities can not be empty");
        RootEntity entity = entities.get(0);
        String insertBatchId = MybatisStatementIdBuilder.INSTANCE.insertBatchId(sqlSessionTemplate, entity.getClass());
        return sqlSessionTemplate.insert(insertBatchId, entities);
    }

    public int deleteByQuery(RootQuery query) {
        AssertUtil.INSTANCE.notNull(query, "query can not be null");
        String deleteByQueryId = MybatisStatementIdBuilder.INSTANCE.deleteByQueryId(sqlSessionTemplate, query.getClass());
        return sqlSessionTemplate.delete(deleteByQueryId, query);
    }

    public int delete(RootEntity entity) {
        AssertUtil.INSTANCE.notNull(entity, "entity can not be null");
        String deleteId = MybatisStatementIdBuilder.INSTANCE.deleteId(sqlSessionTemplate, entity.getClass());
        return sqlSessionTemplate.delete(deleteId, entity);
    }

    public int updateByQuery(RootEntity entity, RootQuery query) {
        AssertUtil.INSTANCE.notNull(entity, "entity can not be null");
        AssertUtil.INSTANCE.notNull(query, "query can not be null");
        String updateByQueryId = MybatisStatementIdBuilder.INSTANCE.updateByQueryId(sqlSessionTemplate, entity.getClass(), query.getClass());
        query.setEntity(entity);
        int rows = sqlSessionTemplate.update(updateByQueryId, query);
        query.setEntity(null);
        return rows;
    }

    public int update(RootEntity entity) {
        AssertUtil.INSTANCE.notNull(entity, "entity can not be null");
        String updateId = MybatisStatementIdBuilder.INSTANCE.updateId(sqlSessionTemplate, entity.getClass());
        return sqlSessionTemplate.update(updateId, entity);
    }
}
