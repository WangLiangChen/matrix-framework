package wang.liangchen.matrix.framework.data.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.thread.ThreadPoolUtil;
import wang.liangchen.matrix.framework.springboot.context.BeanLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * @author LiangChen.Wang
 */
public enum TransactionUtil {
    /**
     *
     */
    INSTANCE;
    private final PlatformTransactionManager transactionManager = BeanLoader.INSTANCE.getBean(PlatformTransactionManager.class);
    private final AfterCommitExecutor afterCommitExecutor = new AfterCommitExecutor();

    public void afterCommit(Runnable runnable) {
        afterCommitExecutor.execute(runnable);
    }

    public <T> T execute(Supplier<T> supplier) {
        return execute(supplier, Propagation.REQUIRED, Isolation.DEFAULT);
    }

    public <T> T execute(Supplier<T> supplier, Propagation propagation) {
        return execute(supplier, propagation, Isolation.DEFAULT);
    }

    public <T> T execute(Supplier<T> supplier, Isolation isolationLevel) {
        return execute(supplier, Propagation.REQUIRED, isolationLevel);
    }

    public <T> T execute(Supplier<T> supplier, Propagation propagation, Isolation isolationLevel) {
        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setPropagationBehavior(propagation.value());
        transactionDefinition.setIsolationLevel(isolationLevel.value());
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
        try {
            T t = supplier.get();
            transactionManager.commit(transactionStatus);
            return t;
        } catch (Exception e) {
            transactionManager.rollback(transactionStatus);
            throw new MatrixErrorException(e);
        }
    }

    public void execute(Runnable runnable) {
        execute(runnable, Propagation.REQUIRED, Isolation.DEFAULT);
    }

    public void execute(Runnable runnable, Propagation propagation) {
        execute(runnable, propagation, Isolation.DEFAULT);
    }

    public void execute(Runnable runnable, Isolation isolationLevel) {
        execute(runnable, Propagation.REQUIRED, isolationLevel);
    }

    public void execute(Runnable runnable, Propagation propagation, Isolation isolationLevel) {
        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setPropagationBehavior(propagation.value());
        transactionDefinition.setIsolationLevel(isolationLevel.value());
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
        try {
            runnable.run();
            transactionManager.commit(transactionStatus);
        } catch (Exception e) {
            transactionManager.rollback(transactionStatus);
            throw e;
        }
    }

    static class AfterCommitExecutor implements TransactionSynchronization, Executor {
        private static final Logger logger = LoggerFactory.getLogger(AfterCommitExecutor.class);
        private static final ThreadLocal<List<Runnable>> runnables = ThreadLocal.withInitial(() -> new ArrayList<>(10));
        private static final ThreadLocal<Boolean> registed = ThreadLocal.withInitial(() -> false);
        private final Executor executor;

        public AfterCommitExecutor() {
            this.executor = ThreadPoolUtil.INSTANCE.getUnboundedExecutor();
        }

        @Override
        public void execute(Runnable runnable) {
            logger.debug("Submitting new runnable {} to run after commit", runnable);
            // 如果事务同步不可用则事务已提交,立即执行
            if (!TransactionSynchronizationManager.isSynchronizationActive()) {
                logger.debug("Transaction synchronization is not active. Executing right now runnable {}", runnable);
                executor.execute(runnable);
                return;
            }
            // 同一个事务的 在一起
            runnables.get().add(runnable);
            if (!registed.get()) {
                TransactionSynchronizationManager.registerSynchronization(this);
                registed.set(true);
            }
        }


        @Override
        public void afterCommit() {
            List<Runnable> threadRunnables = runnables.get();
            logger.debug("Transaction successfully committed, executing {} runnables", threadRunnables.size());
            threadRunnables.forEach(runnable -> {
                logger.debug("Executing runnable {}", runnable);
                try {
                    executor.execute(runnable);
                } catch (RuntimeException e) {
                    logger.error("Failed to execute runnable " + runnable, e);
                }
            });
        }

        @Override
        public void afterCompletion(int status) {
            logger.debug("Transaction completed with status {}", status == STATUS_COMMITTED ? "COMMITTED" : "ROLLED_BACK");
            runnables.remove();
            registed.remove();
        }
    }
}
