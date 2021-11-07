package wang.liangchen.matrix.framework.commons.test;

import org.junit.jupiter.api.Test;
import wang.liangchen.matrix.framework.commons.file.FileUtil;
import wang.liangchen.matrix.framework.commons.utils.PrettyPrinter;

public class CommonsTests {
    @Test
    public void testPrettyPrinter() {
        PrettyPrinter.INSTANCE.buffer("a");
        PrettyPrinter.INSTANCE.buffer("b");
        PrettyPrinter.INSTANCE.buffer("c");
        PrettyPrinter.INSTANCE.buffer("d");
        PrettyPrinter.INSTANCE.buffer("e");
        PrettyPrinter.INSTANCE.buffer("g");
        PrettyPrinter.INSTANCE.flush();
    }

    @Test
    public void testFileExtension() {
        String fileName="abc.docx";
        String extension = FileUtil.INSTANCE.extension(fileName);
        System.out.println(extension);
    }
}
