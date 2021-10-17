package liangchen.wang.matrix.framework.data.enumeration;

/**
 * @author LiangChen.Wang
 */
public enum DataStatus {
    /**
     *
     */
    INSTANCE;
    private boolean jdbcEnabled;

    public boolean isJdbcEnabled() {
        return jdbcEnabled;
    }

    public boolean isNotJdbcEnabled() {
        return !jdbcEnabled;
    }

    public void setJdbcEnabled(boolean jdbcEnabled) {
        this.jdbcEnabled = jdbcEnabled;
    }

}
