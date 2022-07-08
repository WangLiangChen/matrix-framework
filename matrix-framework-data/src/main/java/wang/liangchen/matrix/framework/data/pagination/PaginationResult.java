package wang.liangchen.matrix.framework.data.pagination;

import wang.liangchen.matrix.framework.commons.collection.CollectionUtil;
import wang.liangchen.matrix.framework.commons.exception.Assert;
import wang.liangchen.matrix.framework.commons.object.ObjectUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

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
        Assert.INSTANCE.notNull(datas, "datas can not be null");
        this.datas = datas;
    }

    public Integer getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Integer totalRecord) {
        Assert.INSTANCE.notNull(totalRecord, "totalRecord can not be null");
        this.totalRecords = totalRecord;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        Assert.INSTANCE.notNull(pageNumber, "pageNumber can not be null");
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        Assert.INSTANCE.notNull(pageSize, "pageSize can not be null");
        this.pageSize = pageSize;
    }

    public void loopDatas(Consumer<E> consumer) {
        if (CollectionUtil.INSTANCE.isEmpty(datas) || null == consumer) {
            return;
        }
        datas.forEach(consumer);
    }

    public <T> PaginationResult<T> to(Class<T> targetClass) {
        return to(targetClass, null);
    }

    private <T> PaginationResult<T> to(Class<T> targetClass, Consumer<T> consumer) {
        PaginationResult<T> paginationResult = new PaginationResult<>();
        paginationResult.setTotalRecords(this.totalRecords);
        paginationResult.setPageNumber(this.pageNumber);
        paginationResult.setPageSize(this.pageSize);
        if (CollectionUtil.INSTANCE.isEmpty(datas)) {
            paginationResult.setDatas(Collections.emptyList());
            return paginationResult;
        }
        List<T> targetList = new ArrayList<>(datas.size());
        datas.forEach(sourceObject -> {
            T targetObject = ObjectUtil.INSTANCE.copyProperties(sourceObject, targetClass);
            targetList.add(targetObject);
            if (null != consumer) {
                consumer.accept(targetObject);
            }
        });
        paginationResult.setDatas(targetList);
        return paginationResult;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PaginationResult{");
        builder.append("totalRecords = ").append(totalRecords).append(", ");
        builder.append("pageNumber = ").append(pageNumber).append(", ");
        builder.append("pageSize = ").append(pageSize).append(", ");
        builder.append("datas = ").append(datas);
        return builder.toString();
    }
}
