package wang.liangchen.matrix.framework.generator;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Liangchen.Wang 2022-04-27 10:16
 */
public class GeneratorProperties {
    private String output;
    private String basePackage;
    private Map<String, EntityProperties> entityProperties = new HashMap<>();

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public Map<String, EntityProperties> getEntityProperties() {
        return entityProperties;
    }

    public void setEntityProperties(Map<String, EntityProperties> entityProperties) {
        this.entityProperties = entityProperties;
    }

    static class EntityProperties {
        private String table;
        private String className;
        private String subPackage;
        private String columnVersion;
        private String columnMarkDelete;
        private String columnMarkDeleteValue;
        private boolean camelCase;

        public String getTable() {
            return table;
        }

        public void setTable(String table) {
            this.table = table;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getSubPackage() {
            return subPackage;
        }

        public void setSubPackage(String subPackage) {
            this.subPackage = subPackage;
        }

        public String getColumnVersion() {
            return columnVersion;
        }

        public void setColumnVersion(String columnVersion) {
            this.columnVersion = columnVersion;
        }

        public String getColumnMarkDelete() {
            return columnMarkDelete;
        }

        public void setColumnMarkDelete(String columnMarkDelete) {
            this.columnMarkDelete = columnMarkDelete;
        }

        public String getColumnMarkDeleteValue() {
            return columnMarkDeleteValue;
        }

        public void setColumnMarkDeleteValue(String columnMarkDeleteValue) {
            this.columnMarkDeleteValue = columnMarkDeleteValue;
        }

        public boolean isCamelCase() {
            return camelCase;
        }

        public void setCamelCase(boolean camelCase) {
            this.camelCase = camelCase;
        }
    }
}
