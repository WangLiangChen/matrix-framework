package wang.liangchen.matrix.framework.commons.random;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author LiangChen.Wang
 */
public enum RandomUtil {
    /**
     *
     */
    INSTANCE;

    public int random(int min, int max) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return random.nextInt(max - min + 1) + min;
    }
}
