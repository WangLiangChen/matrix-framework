package wang.liangchen.matrix.framework.commons.utils;

import org.apache.commons.exec.*;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.stream.QueueSpliterator;
import wang.liangchen.matrix.framework.commons.thread.ThreadPoolUtil;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * @author LiangChen.Wang 2020/10/20
 */
public enum CommandUtil {
    //
    INSTANCE;

    public Stream<String> stream(String command, String... args) {
        return stream(null, null, command, args);
    }

    public Stream<String> stream(Runnable commandCompletedRunnable, String command, String... args) {
        return stream(commandCompletedRunnable, null, command, args);
    }

    public Stream<String> stream(long timeout, TimeUnit timeUnit, String command, String... args) {
        return stream(null, timeout, timeUnit, command, args);
    }

    public Stream<String> stream(Runnable commandCompletedRunnable, long timeout, TimeUnit timeUnit, String command, String... args) {
        ExecuteWatchdog watchdog = null;
        if (timeout > 0 && null != timeUnit) {
            watchdog = new ExecuteWatchdog(timeUnit.toMillis(timeout));
        }
        return stream(commandCompletedRunnable, watchdog, command, args);
    }

    public Stream<String> stream(Runnable commandCompletedRunnable, ExecuteWatchdog watchdog, String command, String... args) {
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        ThreadPoolUtil.INSTANCE.getExecutor().execute(() -> {
            execute(queue::offer, watchdog, command, args);
            if (null != commandCompletedRunnable) {
                commandCompletedRunnable.run();
            }
        });
        return new QueueSpliterator<>(queue, 2, TimeUnit.SECONDS).stream(true);
    }

    public int execute(Consumer<String> consumer, String command, String... args) {
        return execute(consumer, null, command, args);
    }

    public int execute(Consumer<String> consumer, ExecuteWatchdog watchdog, String command, String... args) {
        PumpStreamHandler streamHandler = new PumpStreamHandler(new LogOutputStream() {
            @Override
            protected void processLine(String line, int i) {
                consumer.accept(line);
            }
        });
        return execute(streamHandler, watchdog, command, args);
    }

    public int execute(String command, String... args) {
        return execute(new PumpStreamHandler(null, null, null), null, command, args);
    }

    public int execute(long timeout, TimeUnit timeUnit, String command, String... args) {
        return execute(null, timeout, timeUnit, command, args);
    }

    public int execute(ExecuteStreamHandler streamHandler, long timeout, TimeUnit timeUnit, String command, String... args) {
        ExecuteWatchdog watchdog = null;
        if (timeout > 0 && null != timeUnit) {
            watchdog = new ExecuteWatchdog(timeUnit.toMillis(timeout));
        }
        return execute(streamHandler, watchdog, command, args);
    }

    public int execute(ExecuteStreamHandler streamHandler, String command, String... args) {
        return execute(streamHandler, null, command, args);
    }

    public int execute(ExecuteWatchdog watchdog, String command, String... args) {
        return execute(new PumpStreamHandler(null, null, null), watchdog, command, args);
    }

    public int execute(ExecuteStreamHandler streamHandler, ExecuteWatchdog watchdog, String command, String... args) {
        CommandLine cmdLine = buildCommanLine(command, args);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setProcessDestroyer(new ShutdownHookProcessDestroyer());
        executor.setExitValues(null);
        if (null == streamHandler) {
            executor.setStreamHandler(new PumpStreamHandler(null, null, null));
        } else {
            executor.setStreamHandler(streamHandler);
        }
        if (null != watchdog) {
            executor.setWatchdog(watchdog);
        }
        ResultHandler resultHandler = new ResultHandler();
        try {
            executor.execute(cmdLine, resultHandler);
            resultHandler.waitFor();
            ExecuteException exception = resultHandler.getException();
            if (null != exception) {
                throw exception;
            }
            return resultHandler.getExitValue();
        } catch (Exception e) {
            throw new MatrixErrorException(e);
        }
    }

    private CommandLine buildCommanLine(String command, String... args) {
        //第一个空格之后的所有参数都为参数
        CommandLine cmdLine = new CommandLine(command);
        cmdLine.addArguments(args);
        return cmdLine;
    }

    static class ResultHandler extends DefaultExecuteResultHandler {

    }

}
