package wang.liangchen.matrix.framework.generator;

import wang.liangchen.matrix.framework.data.dao.criteria.ColumnMeta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Liangchen.Wang 2022-04-28 9:51
 */
public class GeneratorTemplate extends GeneratorProperties {
    private String domainPackage;
    private Set<String> imports = new HashSet<>();
    private List<ColumnMeta> columnMetas = new ArrayList<>();

    public String getDomainPackage() {
        return domainPackage;
    }

    public void setDomainPackage(String domainPackage) {
        this.domainPackage = domainPackage;
    }

    public Set<String> getImports() {
        return imports;
    }

    public List<ColumnMeta> getColumnMetas() {
        return columnMetas;
    }
}
