package wang.liangchen.matrix.framework.data.pagination;


import wang.liangchen.matrix.framework.commons.exception.AssertUtil;

import java.util.Map;

/**
 * @author LiangChen.Wang
 */
public final class QueryParameter {
    private Map<String, String> search;
    private PaginationParameter pagination;

    public Map<String, String> getSearch() {
        return search;
    }

    public void setSearch(Map<String, String> search) {
        AssertUtil.INSTANCE.notEmpty(search, "Parameter 'search' can not be null");
        this.search = search;
    }

    public PaginationParameter getPagination() {
        return pagination;
    }

    public void setPagination(PaginationParameter pagination) {
        AssertUtil.INSTANCE.notNull(pagination, "Parameter 'pagination' can not be null");
        this.pagination = pagination;
    }
}
