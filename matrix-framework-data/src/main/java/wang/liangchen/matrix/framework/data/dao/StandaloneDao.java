package wang.liangchen.matrix.framework.data.dao;

import wang.liangchen.matrix.framework.commons.collection.CollectionUtil;
import wang.liangchen.matrix.framework.commons.exception.Assert;
import wang.liangchen.matrix.framework.data.dao.criteria.Criteria;
import wang.liangchen.matrix.framework.data.dao.criteria.CriteriaParameter;
import wang.liangchen.matrix.framework.data.dao.criteria.CriteriaResolver;
import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;
import wang.liangchen.matrix.framework.data.mybatis.MybatisExecutor;
import wang.liangchen.matrix.framework.data.pagination.PaginationResult;
import wang.liangchen.matrix.framework.data.query.RootQuery;

import java.util.Collections;
import java.util.List;

/**
 * @author Liangchen.Wang 2021-10-20 14:21
 */
public class StandaloneDao extends AbstractDao {

    public <E extends RootEntity> int insert(E entity) {
        Assert.INSTANCE.notNull(entity, "entity can not be null");
        return MybatisExecutor.INSTANCE.insert(sqlSessionTemplate, entity);
    }

    public <E extends RootEntity> int insert(List<E> entities) {
        Assert.INSTANCE.notEmpty(entities, "entities can not be empty");
        return MybatisExecutor.INSTANCE.insert(sqlSessionTemplate, entities);
    }


    public int deleteByQuery(RootQuery query) {
        if (null == query) {
            return 0;
        }
        Assert.INSTANCE.notNull(query, "query can not be null");
        String deleteByQueryId = MybatisExecutor.INSTANCE.deleteByQueryId(sqlSessionTemplate, query.getClass());
        return sqlSessionTemplate.delete(deleteByQueryId, query);
    }

    public int delete(RootEntity entity) {
        Assert.INSTANCE.notNull(entity, "entity can not be null");
        String deleteId = MybatisExecutor.INSTANCE.deleteId(sqlSessionTemplate, entity.getClass());
        return sqlSessionTemplate.delete(deleteId, entity);
    }

    public int updateByQuery(RootEntity entity, RootQuery query) {
        Assert.INSTANCE.notNull(entity, "entity can not be null");
        Assert.INSTANCE.notNull(query, "query can not be null");
        String updateByQueryId = MybatisExecutor.INSTANCE.updateByQueryId(sqlSessionTemplate, entity.getClass(), query.getClass());
        query.setEntity(entity);
        int rows = sqlSessionTemplate.update(updateByQueryId, query);
        query.setEntity(null);
        return rows;
    }

    public int update(RootEntity entity) {
        Assert.INSTANCE.notNull(entity, "entity can not be null");
        String updateId = MybatisExecutor.INSTANCE.updateId(sqlSessionTemplate, entity.getClass());
        return sqlSessionTemplate.update(updateId, entity);
    }

    public <E extends RootEntity> List<E> list(Class<E> entityClass, RootQuery query, String... columns) {
        if (null == query) {
            return Collections.emptyList();
        }
        if (CollectionUtil.INSTANCE.isEmpty(columns)) {
            query.setColumns(new String[]{"*"});
        } else {
            query.setColumns(columns);
        }
        String listId = MybatisExecutor.INSTANCE.listId(sqlSessionTemplate, query.getClass(), entityClass);
        List<E> list = sqlSessionTemplate.selectList(listId, query);
        // 清除query的returnFields字段，以免影响缓存Key
        query.setColumns(null);
        return list;
    }

    public int count(RootQuery query) {
        if (null == query) {
            return 0;
        }
        String countId = MybatisExecutor.INSTANCE.countId(sqlSessionTemplate, query.getClass());
        return sqlSessionTemplate.selectOne(countId, query);
    }

    public int count(Criteria criteria) {
        CriteriaParameter criteriaParameter = CriteriaResolver.INSTANCE.resolve(criteria);
        return MybatisExecutor.INSTANCE.count(sqlSessionTemplate, criteriaParameter);
    }

    public <E extends RootEntity> List<E> list(Criteria criteria) {
        CriteriaParameter criteriaParameter = CriteriaResolver.INSTANCE.resolve(criteria);
        return MybatisExecutor.INSTANCE.list(sqlSessionTemplate, criteriaParameter);
    }

    public <E extends RootEntity> PaginationResult<E> pagination(Class<E> entityClass, RootQuery query, String... columns) {
        int count = count(query);
        PaginationResult<E> paginationResult = PaginationResult.newInstance();
        paginationResult.setTotalRecords(count);
        paginationResult.setPage(query.getPage());
        paginationResult.setRows(query.getRows());
        if (0 == count) {
            paginationResult.setDatas(Collections.emptyList());
            return paginationResult;
        }
        List<E> datas = list(entityClass, query, columns);
        paginationResult.setDatas(datas);
        return paginationResult;
    }


}
