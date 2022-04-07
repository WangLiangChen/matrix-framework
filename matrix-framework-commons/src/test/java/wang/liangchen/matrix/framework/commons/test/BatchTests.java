package wang.liangchen.matrix.framework.commons.test;

import org.junit.jupiter.api.Test;
import wang.liangchen.matrix.framework.commons.batch.BatchProcessor;

/**
 * @author Liangchen.Wang 2022-04-07 9:03
 */
public class BatchTests {
    @Test
    public void testBatch() throws InterruptedException {
        for (int batchSize = 1; batchSize < 22; batchSize++) {
            System.out.println("============================================");
            System.out.println("batchSize:" + batchSize);
            BatchProcessor<Integer> batchProcessor = BatchProcessor.newInstance(batchSize);
            batchProcessor.onConsume(System.out::println);
            int finalBatchSize = batchSize;
            batchProcessor.onFinish(() -> {
                System.out.println(Thread.currentThread().getName()+":finished batchSize:" + finalBatchSize);
            });
            for (int i = 0; i < 10; i++) {
                batchProcessor.put(i);
            }
            Thread.sleep(6000);
        }

    }
}
