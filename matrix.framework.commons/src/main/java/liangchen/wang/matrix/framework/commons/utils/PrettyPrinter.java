package liangchen.wang.matrix.framework.commons.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public enum PrettyPrinter {
    INSTANCE;
    private static ThreadLocal<Map<String, List<Payload>>> threadLocal = new ThreadLocal<>();

    public void buffer(String message, Object... args) {
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
            System.out.println(String.format("%s %d %d", payload, duration.toMillis(), total.toMillis()));
        }
        payloads.clear();
        System.out.println(String.format("▲ ------ %s------", className));
        System.out.println();
    }

    private class Payload {
        private int lineNumber;
        private String methodName;
        private String message;
        private LocalDateTime dateTime;

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
