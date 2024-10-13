package wang.liangchen.matrix.framework.commons.compression;


import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

/**
 * @author Liangchen.Wang
 */
public enum CompressUtil {
    //
    INSTANCE;

    public Stream<String> gz2StringStream(InputStream inputStream) {
        try (InputStream innerInputStream = inputStream;) {
            GZIPInputStream gzipInputStream = new GZIPInputStream(innerInputStream);
            InputStreamReader inputStreamReader = new InputStreamReader(gzipInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            return bufferedReader.lines();
        } catch (IOException e) {
            throw new MatrixErrorException(e);
        }
    }
}
