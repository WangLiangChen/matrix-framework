package wang.liangchen.matrix.framework.commons.uid;

import wang.liangchen.matrix.framework.commons.datetime.DateTimeUtil;
import wang.liangchen.matrix.framework.commons.exception.MatrixWarnException;
import wang.liangchen.matrix.framework.commons.network.NetUtil;
import wang.liangchen.matrix.framework.commons.thread.ThreadUtil;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * @author Liangchen.Wang
 * <p>
 * +-------------------------------------------------------------------------+
 * |     Unused    | 39位时间精确到10ms |       8位序列号      |    16位节点号    |
 * +------------------------------------------------------------------------+
 * | 1 Bit Unused | 39 Bit Timestamp |  8 Bit Sequence ID  | 16 Bit Node ID |
 * +------------------------------------------------------------------------+
 */
public enum NumbericUid {
    /**
     * instance
     */
    INSTANCE;
    private final static byte timeBits = 39;
    private final static byte sequenceBits = 8;
    private final static byte nodeBits = 16;

    /**
     * max time limit
     */
    private final static long timeUpperLimit = 1L << timeBits;

    private final static int sequenceMask = (1 << sequenceBits) - 1;
    /**
     * 10ms 一个单位
     */
    private final static byte TIMEUNIT = 10;
    /**
     * 基准时间戳 10MS
     */
    private final static long baseTimestamp;
    /**
     * nodeId 根据低16位ip计算
     */
    private final static long nodeId;
    /**
     * 正在使用的时间戳
     */
    private long usingElapsedTime;
    /**
     * 当前时间戳
     */
    private long currentElapsedTime;
    private long sequence;

    static {
        // My daughter's birthday is base time
        LocalDateTime localDateTime = LocalDateTime.of(2014, 5, 24, 0, 0, 0, 0);
        // 10MS
        baseTimestamp = DateTimeUtil.INSTANCE.localDateTime2Ms(localDateTime) / TIMEUNIT;

        String privateIP = NetUtil.INSTANCE.getLocalHostAddress();
        nodeId = NetUtil.INSTANCE.ipV4Lower16BitsToLong(privateIP);
    }


    public synchronized long nextId() {
        // 从基准时间现在有多少个时间单位(10ms)
        currentElapsedTime = elapsedTime();
        // 已经超过一个时间单位，重置ID序列,从0开始
        if (usingElapsedTime < currentElapsedTime) {
            usingElapsedTime = currentElapsedTime;
            sequence = 0;
            return toID();
        }

        // -usingElapsedTime==currentElapsedTime,说明还在同一时间单位内,继续+1,当sequence达到上限时等待
        // -usingElapsedTime>currentElapsedTime,说明发生了时间回拨,继续+1，当sequence达到上限时等待
        sequence = (sequence + 1) & sequenceMask;
        // 同一时间单位内达到了最大序列号,掩码的作用就是这个，达到最大就是0
        if (sequence == 0) {
            // 增加一个周期
            usingElapsedTime++;
            sleep();
        }
        return toID();
    }

    private long toID() {
        if (usingElapsedTime >= timeUpperLimit) {
            // 时间已达到上限，继续生成下去的话，id就会重复
            throw new MatrixWarnException("over the time limit");
        }
        return usingElapsedTime << (sequenceBits + nodeBits)
                | sequence << nodeBits
                | nodeId;
    }


    private long elapsedTime() {
        return (System.currentTimeMillis() / TIMEUNIT) - baseTimestamp;
    }

    private void sleep() {
        // 间隔的时间单位
        long sleep = usingElapsedTime - currentElapsedTime;
        // 转换为毫秒
        sleep = sleep * TIMEUNIT;
        // 当前已经过去的毫秒数
        long passed = System.currentTimeMillis() % TIMEUNIT;
        ThreadUtil.INSTANCE.sleep(TimeUnit.MILLISECONDS, sleep - passed);
    }

}
