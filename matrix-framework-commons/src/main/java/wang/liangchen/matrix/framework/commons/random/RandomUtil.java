package wang.liangchen.matrix.framework.commons.random;

import java.util.Random;

/**
 * @author LiangChen.Wang
 */
public enum RandomUtil {
    /**
     *
     */
    INSTANCE;

    public int random(int min, int max) {
        Random rand = new Random();
        return rand.nextInt(max - min + 1) + min;
    }
}
