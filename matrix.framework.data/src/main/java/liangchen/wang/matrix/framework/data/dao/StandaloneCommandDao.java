package liangchen.wang.matrix.framework.data.dao;

import liangchen.wang.matrix.framework.data.dao.entity.RootEntity;
import liangchen.wang.matrix.framework.data.mybatis.MybatisStatementIdBuilder;
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

    public void delete(RootEntity entity) {
        MybatisStatementIdBuilder mybatisStatementIdBuilder = new MybatisStatementIdBuilder(sqlSessionTemplate,entityMeta(entity.getClass()));
    }
}
