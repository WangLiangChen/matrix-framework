package liangchen.wang.matrix.framework.data.dao;

import java.util.HashSet;
import java.util.Set;

public class EntityMeta {
    private String tableName;
    private Set<String> ids = new HashSet<>();
    private Set<String> fields = new HashSet<>();

    public EntityMeta(String tableName, Set<String> ids, Set<String> fields) {
        this.tableName = tableName;
        this.ids = ids;
        this.fields = fields;
    }

    public String getTableName() {
        return tableName;
    }

    public Set<String> getIds() {
        return ids;
    }

    public Set<String> getFields() {
        return fields;
    }
}
