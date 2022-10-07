package wang.liangchen.matrix.framework.data.dao;


import org.springframework.cache.Cache;
import wang.liangchen.matrix.cache.sdk.cache.CacheManager;
import wang.liangchen.matrix.framework.commons.exception.MatrixWarnException;
import wang.liangchen.matrix.framework.commons.random.RandomUtil;
import wang.liangchen.matrix.framework.data.dao.criteria.*;
import wang.liangchen.matrix.framework.data.dao.entity.RootEntity;
import wang.liangchen.matrix.framework.data.mybatis.MybatisExecutor;
import wang.liangchen.matrix.framework.data.pagination.Pagination;
import wang.liangchen.matrix.framework.data.pagination.PaginationResult;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author Liangchen.Wang 2021-10-20 14:21
 */
public class StandaloneDao extends AbstractDao {
    private final CacheManager cacheManager;

    public StandaloneDao() {
        this.cacheManager = null;
    }

    public StandaloneDao(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public <E extends RootEntity> int insert(E entity) {
        int rows = MybatisExecutor.INSTANCE.insert(sqlSessionTemplate, entity);
        optionalCache(entity.getClass()).ifPresent(Cache::clear);
        return rows;
    }

    @Override
    public <E extends RootEntity> int insert(Collection<E> entities) {
        int rows = MybatisExecutor.INSTANCE.insert(sqlSessionTemplate, entities);
        for (E entity : entities) {
            optionalCache(entity.getClass()).ifPresent(Cache::clear);
            return rows;
        }
        return rows;
    }

    @Override
    public <E extends RootEntity> int delete(E primaryKey) {
        int rows = MybatisExecutor.INSTANCE.delete(sqlSessionTemplate, primaryKey);
        optionalCache(primaryKey.getClass()).ifPresent(Cache::clear);
        return rows;
    }

    @Override
    public <E extends RootEntity> int delete(DeleteCriteria<E> deleteCriteria) {
        CriteriaParameter<E> criteriaParameter = CriteriaResolver.INSTANCE.resolve(deleteCriteria);
        int rows = MybatisExecutor.INSTANCE.delete(sqlSessionTemplate, criteriaParameter);
        if (deleteCriteria.isFlushCache()) {
            optionalCache(deleteCriteria.getEntityClass()).ifPresent(Cache::clear);
        }
        return rows;
    }

    @Override
    public <E extends RootEntity> int update(E entity) {
        int rows = MybatisExecutor.INSTANCE.update(sqlSessionTemplate, entity);
        optionalCache(entity.getClass()).ifPresent(Cache::clear);
        return rows;
    }

    @Override
    public <E extends RootEntity> int update(UpdateCriteria<E> updateCriteria) {
        CriteriaParameter<E> criteriaParameter = CriteriaResolver.INSTANCE.resolve(updateCriteria);
        int rows = MybatisExecutor.INSTANCE.update(sqlSessionTemplate, criteriaParameter);
        if (updateCriteria.isFlushCache()) {
            optionalCache(updateCriteria.getEntityClass()).ifPresent(Cache::clear);
        }
        return rows;
    }

    @Override
    public <E extends RootEntity> E select(Criteria<E> criteria) {
        List<E> list = list(criteria);
        if (list.isEmpty()) {
            return null;
        }
        int size = list.size();
        if (1 == size) {
            return list.get(0);
        }
        throw new MatrixWarnException("Expected one result (or null) to be returned by select(), but found: {}", size);
    }

    @Override
    public <E extends RootEntity> int count(Criteria<E> criteria) {
        CriteriaParameter<E> criteriaParameter = CriteriaResolver.INSTANCE.resolve(criteria);
        if (criteria.isUseCache()) {
            return optionalCache(criteria.getEntityClass())
                    .map(cache -> cache.get(criteriaParameter.cacheKey(), () -> count(criteriaParameter)))
                    .orElseGet(() -> count(criteriaParameter));
        }
        return count(criteriaParameter);
    }

    public <E extends RootEntity> int count(CriteriaParameter<E> criteriaParameter) {
        return MybatisExecutor.INSTANCE.count(sqlSessionTemplate, criteriaParameter);
    }

    @Override
    public <E extends RootEntity> boolean exists(Criteria<E> criteria) {
        return count(criteria) > 0;
    }

    @Override
    public <E extends RootEntity> List<E> list(Criteria<E> criteria) {
        CriteriaParameter<E> criteriaParameter = CriteriaResolver.INSTANCE.resolve(criteria);
        if (criteria.isUseCache()) {
            return optionalCache(criteria.getEntityClass())
                    .map(cache -> cache.get(criteriaParameter.cacheKey(), () -> list(criteriaParameter)))
                    .orElseGet(() -> list(criteriaParameter));
        }
        return list(criteriaParameter);
    }

    private <E extends RootEntity> List<E> list(CriteriaParameter<E> criteriaParameter) {
        return MybatisExecutor.INSTANCE.list(sqlSessionTemplate, criteriaParameter);
    }

    @Override
    public <E extends RootEntity> PaginationResult<E> pagination(Criteria<E> criteria) {
        CriteriaParameter<E> criteriaParameter = CriteriaResolver.INSTANCE.resolve(criteria);
        if (criteria.isUseCache()) {
            return optionalCache(criteria.getEntityClass())
                    .map(cache -> cache.get(criteriaParameter.cacheKey(), () -> pagination(criteriaParameter)))
                    .orElseGet(() -> pagination(criteriaParameter));
        }
        return pagination(criteriaParameter);
    }

    private <E extends RootEntity> PaginationResult<E> pagination(CriteriaParameter<E> criteriaParameter) {
        int count = MybatisExecutor.INSTANCE.count(sqlSessionTemplate, criteriaParameter);
        PaginationResult<E> paginationResult = PaginationResult.newInstance();
        paginationResult.setTotalRecords(count);
        Pagination pagination = criteriaParameter.getPagination();
        paginationResult.setPageNumber(pagination.getPageNumber());
        paginationResult.setPageSize(pagination.getPageSize());
        if (0 == count) {
            paginationResult.setDatas(Collections.emptyList());
            return paginationResult;
        }
        List<E> datas = MybatisExecutor.INSTANCE.list(sqlSessionTemplate, criteriaParameter);
        paginationResult.setDatas(datas);
        return paginationResult;
    }


    private Optional<Cache> optionalCache(Class<?> entityClass) {
        if (null == cacheManager) {
            return Optional.empty();
        }
        return Optional.ofNullable(cacheManager.getCache(entityClass.getName(), Duration.ofMinutes(RandomUtil.INSTANCE.random(5, 60))));
    }
}
