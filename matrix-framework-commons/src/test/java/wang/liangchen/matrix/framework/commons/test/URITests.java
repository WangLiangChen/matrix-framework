package wang.liangchen.matrix.framework.commons.test;

import org.junit.jupiter.api.Test;
import wang.liangchen.matrix.framework.commons.batch.BatchProcessor;
import wang.liangchen.matrix.framework.commons.network.URIUtil;

import java.net.URI;

/**
 * @author Liangchen.Wang 2022-04-07 9:03
 */
public class URITests {
    @Test
    public void testURI() throws InterruptedException {
        String uriString="http://www.liangchen.wang/cc/";
        String more="/a/b/c/";
        URI uri = URIUtil.INSTANCE.toURI(uriString, more);
        uri=URIUtil.INSTANCE.expandURI(uri,"c/d/e");
        System.out.println(uri);
    }
}
