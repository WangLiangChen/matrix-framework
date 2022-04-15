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
    private Integer page;
    private Integer rows;

    public List<E> getDatas() {
        return datas;
    }

    public void setDatas(List<E> datas) {
        Assert.INSTANCE.notNull(datas, "参数datas不能为空");
        this.datas = datas;
    }

    public Integer getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Integer totalRecord) {
        Assert.INSTANCE.notNull(totalRecord, "参数totalRecord不能为空");
        this.totalRecords = totalRecord;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        Assert.INSTANCE.notNull(page, "参数page不能为空");
        this.page = page;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        Assert.INSTANCE.notNull(rows, "参数rows不能为空");
        this.rows = rows;
    }

    public void circleDatas(Consumer<E> consumer) {
        if (CollectionUtil.INSTANCE.isEmpty(datas) || null == consumer) {
            return;
        }
        datas.forEach(consumer);
    }

    public <T> PaginationResult<T> copyTo(Class<T> clazz) {
        return copyTo(clazz, null);
    }

    public <T> PaginationResult<T> copyTo(Class<T> clazz, Consumer<T> consumer) {
        Assert.INSTANCE.notNull(clazz, "参数clazz不能为空");
        PaginationResult<T> paginationResult = new PaginationResult<>();
        paginationResult.setTotalRecords(this.totalRecords);
        paginationResult.setPage(this.page);
        paginationResult.setRows(this.rows);
        if (CollectionUtil.INSTANCE.isEmpty(datas)) {
            paginationResult.setDatas(Collections.emptyList());
            return paginationResult;
        }
        List<T> ts = new ArrayList<>(datas.size());
        datas.forEach(d -> {
            T t = ObjectUtil.INSTANCE.copyProperties(d, clazz);
            if (null != consumer) {
                consumer.accept(t);
            }
            ts.add(t);
        });
        paginationResult.setDatas(ts);
        return paginationResult;
    }

    @Override
    public String toString() {
        return null;
    }
}
