package liangchen.wang.matrix.framework.commons.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum PrintUtil {
    INSTANCE;
    private static ThreadLocal<Map<String, List<MessageBody>>> threadLocal = new ThreadLocal<>();

    public void buffer(String message, String... args) {
        processMessage(message, args);
    }

    public void prettyPrint() {
        processMessage(null);
    }

    private StackTraceElement stackTraceElement() {
        StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
        String className;
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            className = stackTraceElement.getClassName();
            if ("java.lang.Thread".equals(className) || PrintUtil.class.getName().equals(className)) {
                continue;
            }
            return stackTraceElement;
        }
        return null;
    }


    private void processMessage(String message, String... args) {
        StackTraceElement stackTraceElement = stackTraceElement();
        String className = stackTraceElement.getClassName();
        String methodName = stackTraceElement.getMethodName();
        int lineNumber = stackTraceElement.getLineNumber();
        Thread thread = Thread.currentThread();
        String threadMethod = StringUtil.INSTANCE.format("%s-%s-%s-%s", thread.getId(), thread.getName(), className, methodName);
        Map<String, List<MessageBody>> threadMethodMap = threadLocal.get();
        if (null == threadMethodMap) {
            List<MessageBody> messages = new ArrayList<>();
            threadMethodMap = new HashMap<>();
            threadMethodMap.put(threadMethod, messages);
            threadLocal.set(threadMethodMap);
            messages.add(new MessageBody(String.format("====== %s.%s:%d Start======", className, methodName, lineNumber), System.nanoTime()));
        }
        List<MessageBody> messages = threadMethodMap.get(threadMethod);
        if (StringUtil.INSTANCE.isNotBlank(message)) {
            messages.add(new MessageBody(StringUtil.INSTANCE.format(message, args), System.nanoTime()));
            return;
        }
        messages.forEach(System.out::println);
    }

    class MessageBody {
        private String message;
        private long timestamp;

        public MessageBody(String message, long timestamp) {
            this.message = message;
            this.timestamp = timestamp;
        }

        public String getMessage() {
            return message;
        }

        public long getTimestamp() {
            return timestamp;
        }

        @Override
        public String toString() {
            return this.message;
        }
    }
}
