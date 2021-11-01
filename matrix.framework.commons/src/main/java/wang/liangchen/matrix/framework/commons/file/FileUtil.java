package wang.liangchen.matrix.framework.commons.file;

import wang.liangchen.matrix.framework.commons.enumeration.Symbol;

/**
 * @author Liangchen.Wang 2021-09-30 13:43
 */
public enum FileUtil {
    /**
     * instance
     */
    INSTANCE;

    public String extension(String fileName) {
        int index = fileName.lastIndexOf(Symbol.DOT.getSymbol());
        return fileName.substring(index + 1);
    }
}
