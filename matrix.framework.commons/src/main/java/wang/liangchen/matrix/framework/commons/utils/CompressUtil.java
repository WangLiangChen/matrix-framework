package wang.liangchen.matrix.framework.commons.utils;


import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

public enum CompressUtil {
    //
    INSTANCE;

    public Stream<String> gz2StringStream(InputStream inputStream) {
        try {
            GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
            InputStreamReader inputStreamReader = new InputStreamReader(gzipInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            Stream<String> lines = bufferedReader.lines();
            return lines;
        } catch (IOException e) {
            throw new MatrixErrorException(e);
        }
    }
}
