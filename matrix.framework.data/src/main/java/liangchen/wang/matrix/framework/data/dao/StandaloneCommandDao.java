package liangchen.wang.matrix.framework.data.dao;

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

    public void insert(RootEntity entity) {
        if (null == entity) {
            return;
        }
        entityManager.persist(entity);
    }

    public void insertBatch(List<? extends RootEntity> entities) {
        if (CollectionUtil.INSTANCE.isEmpty(entities)) {
            return;
        }
        RootEntity entity = entities.get(0);
        String insertBatchId = MybatisStatementIdBuilder.INSTANCE.insertBatchId(sqlSessionTemplate, entity.getClass());
        sqlSessionTemplate.insert(insertBatchId, entities);
    }

    int deleteByQuery(RootQuery query) {
        String deleteByQueryId = MybatisStatementIdBuilder.INSTANCE.deleteByQueryId(sqlSessionTemplate, query.getClass());
        return sqlSessionTemplate.delete(deleteByQueryId, query);
    }

    int updateByQuery(RootEntity entity, RootQuery query) {
        String updateByQueryId = MybatisStatementIdBuilder.INSTANCE.updateByQueryId(sqlSessionTemplate, entity.getClass(), query.getClass());
        query.setEntity(entity);
        return sqlSessionTemplate.update(updateByQueryId, query);
    }

    void update(RootEntity entity) {
        entityManager.persist(entity);
    }
}
