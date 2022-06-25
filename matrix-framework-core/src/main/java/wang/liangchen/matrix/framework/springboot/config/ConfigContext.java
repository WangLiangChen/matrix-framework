package wang.liangchen.matrix.framework.springboot.config;

import wang.liangchen.matrix.framework.commons.enumeration.Symbol;

/**
 * @author Liangchen.Wang 2022-06-25 16:07
 */
public enum ConfigContext {
    INSTANCE;
    public static final String JDBC_PREFIX = "jdbc";
    public static final String LOGGER_PREFIX = "logger";
    public static final String AUTOSCAN_PREFIX = "autoscan";
    public static final String MARTIX_CONFIG = "/matrix-framework";
    private static final String EXTENSION_PATTERN = ".*";
    public static final String JDBC_PATTERN = Symbol.URI_SEPARATOR.getSymbol() + JDBC_PREFIX + EXTENSION_PATTERN;
    public static final String LOGGER_PATTERN = Symbol.URI_SEPARATOR.getSymbol() + LOGGER_PREFIX + EXTENSION_PATTERN;
    public static final String AUTOSCAN_PATTERN = Symbol.URI_SEPARATOR.getSymbol() + AUTOSCAN_PREFIX + EXTENSION_PATTERN;

    private String configRoot;

    public String getConfigRoot() {
        return configRoot;
    }

    public boolean setConfigRoot(String configRoot) {
        if (null == this.configRoot) {
            this.configRoot = configRoot;
            return true;
        }
        if (this.configRoot.equals(configRoot)) {
            return true;
        }
        return false;
    }
}
