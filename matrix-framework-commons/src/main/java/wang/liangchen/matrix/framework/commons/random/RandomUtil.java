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
        return ThreadLocalRandom.current().nextInt(max - min + 1) + min;
    }

    public int random() {
        return ThreadLocalRandom.current().nextInt();
    }
}
