package wang.liangchen.matrix.framework.commons.test;

import org.junit.jupiter.api.Test;
import wang.liangchen.matrix.framework.commons.encryption.enums.CipherTransformation;

/**
 * @author Liangchen.Wang 2022-04-12 10:07
 */
public class CipherTests {
    @Test
    public void testSplit(){
        System.out.println(CipherTransformation.AES_CBC_NoPadding.getTransformation());
        System.out.println(CipherTransformation.AES_CBC_NoPadding.getAlgorithm());
        System.out.println(CipherTransformation.AES_CBC_NoPadding.getMode());
        System.out.println(CipherTransformation.AES_CBC_NoPadding.getPadding());
    }
}
