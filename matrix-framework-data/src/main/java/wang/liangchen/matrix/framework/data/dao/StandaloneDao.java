package wang.liangchen.matrix.framework.data.dao;

import wang.liangchen.matrix.framework.data.dao.criteria.*;
import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;
import wang.liangchen.matrix.framework.data.mybatis.MybatisExecutor;
import wang.liangchen.matrix.framework.data.pagination.PaginationResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Liangchen.Wang 2021-10-20 14:21
 */
public class StandaloneDao extends AbstractDao {
    @Override
    public <E extends RootEntity> int insert(E entity) {
        return MybatisExecutor.INSTANCE.insert(sqlSessionTemplate, entity);
    }
    @Override
    public <E extends RootEntity> int insert(Collection<E> entities) {
        return MybatisExecutor.INSTANCE.insert(sqlSessionTemplate, entities);
    }
    @Override
    public <E extends RootEntity> int delete(E entity) {
        return MybatisExecutor.INSTANCE.delete(sqlSessionTemplate, entity);
    }
    @Override
    public <E extends RootEntity> int delete(SubCriteria<E> subCriteria) {
        CriteriaParameter<E> criteriaParameter = CriteriaResolver.INSTANCE.resolve(subCriteria);
        return MybatisExecutor.INSTANCE.delete(sqlSessionTemplate, criteriaParameter);
    }
    @Override
    public <E extends RootEntity> int update(E entity) {
        return MybatisExecutor.INSTANCE.update(sqlSessionTemplate, entity);
    }
    @Override
    public <E extends RootEntity> int update(UpdateCriteria<E> updateCriteria) {
        CriteriaParameter<E> criteriaParameter = CriteriaResolver.INSTANCE.resolve(updateCriteria);
        return MybatisExecutor.INSTANCE.update(sqlSessionTemplate, criteriaParameter);
    }
    @Override
    public <E extends RootEntity> int count(Criteria<E> criteria) {
        CriteriaParameter<E> criteriaParameter = CriteriaResolver.INSTANCE.resolve(criteria);
        return MybatisExecutor.INSTANCE.count(sqlSessionTemplate, criteriaParameter);
    }
    @Override
    public <E extends RootEntity> List<E> list(Criteria<E> criteria) {
        CriteriaParameter<E> criteriaParameter = CriteriaResolver.INSTANCE.resolve(criteria);
        return MybatisExecutor.INSTANCE.list(sqlSessionTemplate, criteriaParameter);
    }
    @Override
    public <E extends RootEntity> PaginationResult<E> pagination(Criteria<E> criteria) {
        CriteriaParameter<E> criteriaParameter = CriteriaResolver.INSTANCE.resolve(criteria);
        int count = MybatisExecutor.INSTANCE.count(sqlSessionTemplate, criteriaParameter);
        PaginationResult<E> paginationResult = PaginationResult.newInstance();
        paginationResult.setTotalRecords(count);
        paginationResult.setPageNumber(criteriaParameter.getPageNumber());
        paginationResult.setPageSize(criteriaParameter.getPageSize());
        if (0 == count) {
            paginationResult.setDatas(Collections.emptyList());
            return paginationResult;
        }
        List<E> datas = MybatisExecutor.INSTANCE.list(sqlSessionTemplate, criteriaParameter);
        paginationResult.setDatas(datas);
        return paginationResult;
    }
}
