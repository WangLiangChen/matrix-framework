package wang.liangchen.matrix.framework.generator;

import wang.liangchen.matrix.framework.data.dao.criteria.ColumnMeta;

import java.util.List;

/**
 * @author Liangchen.Wang 2022-04-28 9:51
 */
public class GeneratorTemplate extends GeneratorProperties {
    private String domainPackage;
    private List<ColumnMeta> columnMetas;

    public String getDomainPackage() {
        return domainPackage;
    }

    public void setDomainPackage(String domainPackage) {
        this.domainPackage = domainPackage;
    }

    public List<ColumnMeta> getColumnMetas() {
        return columnMetas;
    }

    public void setColumnMetas(List<ColumnMeta> columnMetas) {
        this.columnMetas = columnMetas;
    }
}
