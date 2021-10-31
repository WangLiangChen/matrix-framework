package liangchen.wang.matrix.framework.data.dao;

import liangchen.wang.matrix.framework.commons.exception.AssertUtil;
import liangchen.wang.matrix.framework.commons.utils.CollectionUtil;
import liangchen.wang.matrix.framework.data.dao.entity.RootEntity;
import liangchen.wang.matrix.framework.data.mybatis.MybatisStatementIdBuilder;
import liangchen.wang.matrix.framework.data.pagination.PaginationResult;
import liangchen.wang.matrix.framework.data.query.RootQuery;

import java.util.Collections;
import java.util.List;

/**
 * @author Liangchen.Wang 2021-10-20 14:21
 */
public class StandaloneDao extends AbstractDao {

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
        if (null == query) {
            return 0;
        }
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

    public <E extends RootEntity> List<E> list(Class<E> entityClass, RootQuery query, String... columns) {
        if (null == query) {
            return Collections.emptyList();
        }
        if (CollectionUtil.INSTANCE.isEmpty(columns)) {
            query.setColumns(new String[]{"*"});
        } else {
            query.setColumns(columns);
        }
        String listId = MybatisStatementIdBuilder.INSTANCE.listId(sqlSessionTemplate, query.getClass(), entityClass);
        List<E> list = sqlSessionTemplate.selectList(listId, query);
        // 清除query的returnFields字段，以免影响缓存Key
        query.setColumns(null);
        return list;
    }

    public int count(RootQuery query) {
        if (null == query) {
            return 0;
        }
        String countId = MybatisStatementIdBuilder.INSTANCE.countId(sqlSessionTemplate, query.getClass());
        return sqlSessionTemplate.selectOne(countId, query);
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
