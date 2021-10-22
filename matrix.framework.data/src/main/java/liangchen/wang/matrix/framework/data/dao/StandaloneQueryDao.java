package liangchen.wang.matrix.framework.data.dao;

import liangchen.wang.matrix.framework.commons.utils.CollectionUtil;
import liangchen.wang.matrix.framework.data.dao.entity.RootEntity;
import liangchen.wang.matrix.framework.data.mybatis.MybatisStatementIdBuilder;
import liangchen.wang.matrix.framework.data.pagination.PaginationResult;
import liangchen.wang.matrix.framework.data.query.RootQuery;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

/**
 * @author Liangchen.Wang 2021-10-20 14:21
 */
@Repository
public class StandaloneQueryDao extends AbstractDao {

    public <E extends RootEntity> List<E> list(Class<E> entityClass, RootQuery query, String... returnFields) {
        if (null == query) {
            return Collections.emptyList();
        }
        if (CollectionUtil.INSTANCE.isEmpty(returnFields)) {
            query.setReturnFields(new String[]{"*"});
        } else {
            query.setReturnFields(returnFields);
        }
        String listId = MybatisStatementIdBuilder.INSTANCE.listId(sqlSessionTemplate, query.getClass(), entityClass);
        List<E> list = sqlSessionTemplate.selectList(listId, query);
        // 清除query的returnFields字段，以免影响缓存Key
        query.setReturnFields(null);
        return list;
    }

    public int count(RootQuery query) {
        if (null == query) {
            return 0;
        }
        String countId = MybatisStatementIdBuilder.INSTANCE.countId(sqlSessionTemplate, query.getClass());
        return sqlSessionTemplate.selectOne(countId, query);
    }

    public <E extends RootEntity> PaginationResult<E> pagination(Class<E> entityClass, RootQuery query, String... returnFields) {
        int count = count(query);
        PaginationResult<E> paginationResult = PaginationResult.newInstance();
        paginationResult.setTotalRecords(count);
        paginationResult.setPage(query.getPage());
        paginationResult.setRows(query.getRows());
        if (count > 0) {
            List<E> datas = list(entityClass, query, returnFields);
            paginationResult.setDatas(datas);
        } else {
            paginationResult.setDatas(Collections.emptyList());
        }
        return paginationResult;
    }

}
