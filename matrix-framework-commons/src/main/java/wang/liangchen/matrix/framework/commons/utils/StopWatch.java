package wang.liangchen.matrix.framework.commons.utils;


import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class StopWatch {
    private final static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss");
    private final ConcurrentMap<String, WatchTask> tasks = new ConcurrentHashMap<>();

    public WatchTask startTask(String taskName) {
        return tasks.computeIfAbsent(taskName, task -> new WatchTask(taskName));
    }

    public void stopTask(String taskName) {
        WatchTask task = tasks.get(taskName);
        stopTask(task);
    }

    public void stopTask(WatchTask task) {
        if (null != task) {
            task.stop();
        }
    }

    public void stopAll() {
        tasks.values().forEach(WatchTask::stop);
    }

    public void prettyPrint(String taskName) {
        WatchTask task = tasks.get(taskName);
        prettyPrint(task);
    }

    public void prettyPrint(WatchTask task) {
        if (null == task) {
            return;
        }
        task.prettyPrint(true);
    }

    public void prettyPrintAll() {
        System.out.println("| Start             | End               | Duration | Task Name");
        tasks.values().stream().sorted(Comparator.comparing(WatchTask::getStartDateTime)).forEach(task -> task.prettyPrint(false));
    }

    public static final class WatchTaskMessage {
        private final String message;
        private final LocalDateTime dateTime;

        public WatchTaskMessage(String message) {
            this.message = message;
            this.dateTime = LocalDateTime.now();
        }

        public String getMessage() {
            return message;
        }

        public LocalDateTime getDateTime() {
            return dateTime;
        }
    }

    public static final class WatchTask {
        private final String taskName;
        private final LocalDateTime startDateTime;
        private LocalDateTime stopDateTime;
        private final List<WatchTaskMessage> messages = new ArrayList<>();

        WatchTask(String taskName) {
            this.taskName = taskName;
            this.startDateTime = LocalDateTime.now();
        }

        public void stop() {
            if (null == stopDateTime) {
                stopDateTime = LocalDateTime.now();
            }
        }

        public void addMessage(String message) {
            messages.add(new WatchTaskMessage(message));
        }

        public void prettyPrint(boolean withTitle) {
            if (withTitle) {
                System.out.println("| Start             | End               | Duration | Task Name");
            }

            String startString = getStartDateTime().format(dateTimeFormatter);
            LocalDateTime stopDateTime = getStopDateTime();
            String stopString;
            Duration duration;
            if (null == stopDateTime) {
                stopString = "00-00-00 00:00:00";
                duration = Duration.between(getStartDateTime(), LocalDateTime.now());
            } else {
                stopString = stopDateTime.format(dateTimeFormatter);
                duration = Duration.between(getStartDateTime(), stopDateTime);
            }
            System.out.printf("| %s | %s | %-8s | %s%n", startString, stopString, duration.toMillis(), getTaskName());
            synchronized (messages) {
                Iterator<WatchTaskMessage> iterator = messages.iterator();
                while (iterator.hasNext()) {
                    WatchTaskMessage message = iterator.next();
                    System.out.printf("- %s %s%n", message.getDateTime().format(dateTimeFormatter), message.getMessage());
                    iterator.remove();
                }
            }
        }

        public String getTaskName() {
            return taskName;
        }

        public LocalDateTime getStartDateTime() {
            return startDateTime;
        }

        public LocalDateTime getStopDateTime() {
            return stopDateTime;
        }


    }
}
