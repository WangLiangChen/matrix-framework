package liangchen.wang.matrix.framework.data.dao;

import liangchen.wang.matrix.framework.data.dao.entity.RootEntity;
import liangchen.wang.matrix.framework.data.mybatis.MybatisStatementIdBuilder;
import liangchen.wang.matrix.framework.data.query.RootQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Liangchen.Wang 2021-10-20 14:21
 */
@Repository
public class StandaloneCommandDao extends AbstractDao {

    public void insert(RootEntity entity) {
        entityManager.persist(entity);
    }

    public void insertBatch(List<? extends RootEntity> entities) {
        entities.forEach(entity -> insert(entity));
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
