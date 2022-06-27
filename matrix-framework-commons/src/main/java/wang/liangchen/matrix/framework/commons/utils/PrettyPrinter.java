package wang.liangchen.matrix.framework.commons.utils;

import wang.liangchen.matrix.framework.commons.string.StringUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum PrettyPrinter {
    /**
     * instance
     */
    INSTANCE;
    private static final ThreadLocal<Map<String, List<Payload>>> threadLocal = new ThreadLocal<>();

    public PrettyPrinter buffer(String message, Object... args) {
        processMessage(message, args);
        return this;
    }

    public void flush() {
        processMessage(null);
        threadLocal.remove();
    }

    private void processMessage(String message, Object... args) {
        StackTraceElement stackTraceElement = stackTraceElement();
        String className = stackTraceElement.getClassName();
        String methodName = stackTraceElement.getMethodName();
        int lineNumber = stackTraceElement.getLineNumber();
        Thread thread = Thread.currentThread();
        // 构造线程方法Key
        String threadMethod = StringUtil.INSTANCE.format("{}-{} {}", thread.getName(), thread.getId(), className);
        Map<String, List<Payload>> threadMethodMap = threadLocal.get();
        // 调用buffer
        if (StringUtil.INSTANCE.isNotBlank(message)) {
            if (null == threadMethodMap) {
                threadLocal.set(threadMethodMap = new HashMap<>());
            }
            LocalDateTime now = LocalDateTime.now();
            threadMethodMap.putIfAbsent(threadMethod, new ArrayList<Payload>() {{
                add(new Payload(lineNumber, methodName, String.format("▼ ------ %s ------", className), now));
            }});
            List<Payload> payloads = threadMethodMap.get(threadMethod);
            payloads.add(new Payload(lineNumber, methodName, StringUtil.INSTANCE.format(message, args), now));
            return;
        }

        // 调用了flush
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
            System.out.printf("%s %d %d%n", payload, duration.toMillis(), total.toMillis());
        }
        payloads.clear();
        System.out.printf("▲ ------ %s------%n", className);
        System.out.println();
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

    private static class Payload {
        private final int lineNumber;
        private final String methodName;
        private final String message;
        private final LocalDateTime dateTime;

        public Payload(int lineNumber, String methodName, String message, LocalDateTime dateTime) {
            this.lineNumber = lineNumber;
            this.methodName = methodName;
            this.message = message;
            this.dateTime = dateTime;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public String getMethodName() {
            return methodName;
        }

        public String getMessage() {
            return message;
        }

        public LocalDateTime getDateTime() {
            return dateTime;
        }

        @Override
        public String toString() {
            return String.format("  %d %s {%s} %s", this.lineNumber, this.methodName, this.message, this.dateTime);
        }
    }
}
