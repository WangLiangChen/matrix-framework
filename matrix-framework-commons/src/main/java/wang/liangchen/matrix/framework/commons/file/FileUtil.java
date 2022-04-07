package wang.liangchen.matrix.framework.commons.file;

import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    public void createFileOrDirectory(String first, String... more) {
        Path path = Paths.get(first, more);
        if (Files.exists(path)) {
            return;
        }
        try {
            if (Files.isDirectory(path)) {
                Files.createDirectory(path);
                return;
            }
            Files.createFile(path);
        } catch (IOException e) {
            throw new MatrixErrorException(e);
        }
    }
}
