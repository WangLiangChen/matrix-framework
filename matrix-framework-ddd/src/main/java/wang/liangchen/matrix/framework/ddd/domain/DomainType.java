package wang.liangchen.matrix.framework.ddd.domain;

/**
 * @author Liangchen.Wang
 */
public enum DomainType {
    //
    Core("核心域"), Generic("通用域"), Supporting("支撑域");
    private final String summary;

    DomainType(String summary) {
        this.summary = summary;
    }
}