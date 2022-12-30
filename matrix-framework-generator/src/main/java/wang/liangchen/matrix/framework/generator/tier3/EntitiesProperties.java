package wang.liangchen.matrix.framework.generator.tier3;

/**
 * @author Liangchen.Wang 2022-12-30 10:55
 */
public class EntitiesProperties {
    private String basePackage;
    private String datasource;
    private String author;
    private String output;

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
