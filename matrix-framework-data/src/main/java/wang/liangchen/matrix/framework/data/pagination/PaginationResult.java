package wang.liangchen.matrix.framework.data.pagination;

import wang.liangchen.matrix.framework.commons.collection.CollectionUtil;
import wang.liangchen.matrix.framework.commons.exception.ExceptionLevel;
import wang.liangchen.matrix.framework.commons.object.ObjectUtil;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.BiConsumer;

/**
 * @author LiangChen.Wang
 */
public final class PaginationResult<E> {
    public static <E> PaginationResult<E> newInstance() {
        return new PaginationResult<>();
    }

    private List<E> datas;
    private Integer totalRecords;
    private Integer pageNumber;
    private Integer pageSize;

    public List<E> getDatas() {
        return datas;
    }

    public void setDatas(List<E> datas) {
        ValidationUtil.INSTANCE.notNull(ExceptionLevel.WARN, datas, "datas must not be null");
        this.datas = datas;
    }

    public Integer getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Integer totalRecord) {
        ValidationUtil.INSTANCE.notNull(ExceptionLevel.WARN, totalRecord, "totalRecord must not be null");
        this.totalRecords = totalRecord;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        ValidationUtil.INSTANCE.notNull(ExceptionLevel.WARN, pageNumber, "pageNumber must not be null");
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        ValidationUtil.INSTANCE.notNull(ExceptionLevel.WARN, pageSize, "pageSize must not be null");
        this.pageSize = pageSize;
    }

    public <T> PaginationResult<T> to(Class<T> targetClass) {
        return to(targetClass, null);
    }

    public <T> PaginationResult<T> to(Class<T> targetClass, BiConsumer<E, T> biConsumer) {
        PaginationResult<T> paginationResult = new PaginationResult<>();
        paginationResult.setTotalRecords(this.totalRecords);
        paginationResult.setPageNumber(this.pageNumber);
        paginationResult.setPageSize(this.pageSize);
        if (CollectionUtil.INSTANCE.isEmpty(this.datas)) {
            paginationResult.setDatas(Collections.emptyList());
            return paginationResult;
        }
        paginationResult.setDatas(ObjectUtil.INSTANCE.copyProperties(datas, targetClass, biConsumer));
        return paginationResult;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PaginationResult.class.getSimpleName() + "[", "]")
                .add("datas=" + datas)
                .add("totalRecords=" + totalRecords)
                .add("pageNumber=" + pageNumber)
                .add("pageSize=" + pageSize)
                .toString();
    }
}
