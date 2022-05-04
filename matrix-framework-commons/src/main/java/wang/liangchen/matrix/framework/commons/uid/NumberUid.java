package wang.liangchen.matrix.framework.commons.uid;

public enum NumberUid {
    INSTANCE;
    private final static byte nodeBit = 16;
    private final static byte timeBit = 39;
    private final static byte sequenceBit = 8;


    public void nextId() {
        int sequenceMask = 1 << sequenceBit - 1;
    }

    private long resolveTime(long timeMillis) {
        return timeMillis / 10;
    }

    private long elapsedTime(long startTimeMillis) {
        return resolveTime(System.currentTimeMillis()) - resolveTime(startTimeMillis);
    }
}
