package liangchen.wang.matrix.framework.commons.utils;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public enum PrettyPrinter {
    INSTANCE;
    private static ThreadLocal<Map<String, List<Payload>>> threadLocal = new ThreadLocal<>();

    public void buffer(String message, String... args) {
        processMessage(message, args);
    }

    public void flush() {
        processMessage(null);
        threadLocal.remove();
    }

    private StackTraceElement stackTraceElement() {
        StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
        String className;
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            className = stackTraceElement.getClassName();
            if ("java.lang.Thread".equals(className) || PrettyPrinter.class.getName().equals(className)) {
                continue;
            }
            return stackTraceElement;
        }
        return null;
    }


    private void processMessage(String message, Object... args) {
        StackTraceElement stackTraceElement = stackTraceElement();
        String className = stackTraceElement.getClassName();
        String methodName = stackTraceElement.getMethodName();
        int lineNumber = stackTraceElement.getLineNumber();
        Thread thread = Thread.currentThread();
        Map<String, List<Payload>> threadMethodMap = threadLocal.get();
        // 构造线程方法Key
        String threadMethod = StringUtil.INSTANCE.format("%s-%s %s:%s", thread.getName(), thread.getId(), className, methodName);
        // 调用了flush
        if (null == message) {
            if (null == threadMethodMap) {
                return;
            }
            List<Payload> payloads = threadMethodMap.get(threadMethod);
            int size = payloads.size();
            for (int i = 0; i < size; i++) {
                Payload payload = payloads.get(i);
                if (0 == i) {
                    System.out.println(payload.getMessage());
                    continue;
                }
                Payload previous = payloads.get(i - 1);
                Payload first = payloads.get(0);
                Duration duration = Duration.between(previous.getDateTime(), payload.getDateTime());
                Duration total = Duration.between(first.getDateTime(), payload.getDateTime());
                System.out.println(String.format("%s %d %d", payload, duration.toMillis(), total.toMillis()));
            }
            System.out.println(String.format("▲ ====== %s#%s======", className, methodName));
            return;
        }
        // 调用了buffer
        LocalDateTime now = LocalDateTime.now();
        if (null == threadMethodMap) {
            threadLocal.set(threadMethodMap = new HashMap<String, List<Payload>>() {{
                put(threadMethod, new ArrayList<Payload>() {{
                    add(new Payload(lineNumber, String.format("▼ ====== %s#%s======", className, methodName), now));
                    //add(new Payload(lineNumber, String.format(" -LineNumber Message Timestamp Duration Total", className, methodName), now));
                }});
            }});
        }
        List<Payload> payloads = threadMethodMap.get(threadMethod);
        payloads.add(new Payload(lineNumber, StringUtil.INSTANCE.format(message, args), now));
    }

    private class Payload {
        private String message;
        private int lineNumber;
        private LocalDateTime dateTime;

        public Payload(int lineNumber, String message, LocalDateTime dateTime) {
            this.lineNumber = lineNumber;
            this.message = message;
            this.dateTime = dateTime;
        }

        public String getMessage() {
            return message;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public LocalDateTime getDateTime() {
            return dateTime;
        }

        @Override
        public String toString() {
            return String.format("  %d %s %s", this.lineNumber, this.message, this.dateTime);
        }
    }
}
