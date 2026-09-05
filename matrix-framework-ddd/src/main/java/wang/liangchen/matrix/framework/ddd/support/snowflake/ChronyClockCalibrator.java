package wang.liangchen.matrix.framework.ddd.support.snowflake;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 基于chrony的平滑时钟校准器，实现{@link ClockSource}。
 * <p>
 * 原理：chrony作为NTP客户端持续校准系统时钟，但校准过程可能产生微小的时间跳变。
 * 本校准器通过定期读取{@code chronyc tracking}获取系统时钟与NTP时间的偏差，
 * 并以可配置的速率平滑地将偏差应用到时间戳上，避免突变导致Snowflake时钟回拨。
 * <p>
 * chrony不可用时自动降级：停止校准但保留最后的平滑偏移量继续生效，
 * 时间戳不发生跳变；chrony恢复后通过限速收敛，同样保证平滑过渡。
 * <p>
 * 生命周期：构造时同步执行首次校准（避免首次异步调整造成的回拨窗口），
 * 之后由后台守护线程周期校准；使用完毕应调用{@link #close()}释放线程。
 *
 * <pre>
 * chronyc tracking 输出示例：
 * System time     : 0.000012345 seconds slow of NTP time
 * System time     : 0.000012345 seconds fast of NTP time
 * </pre>
 *
 * @author Liangchen.Wang
 */
public class ChronyClockCalibrator implements ClockSource {

    private static final System.Logger logger = System.getLogger(ChronyClockCalibrator.class.getName());

    private static final Pattern SYSTEM_TIME_PATTERN = Pattern.compile(
            "System time\\s*:\\s*([0-9]+(?:\\.[0-9]+)?)\\s*seconds\\s*(slow|fast)\\s*of\\s*NTP\\s*time"
    );

    private static volatile boolean chronyUnavailableWarned = false;

    private final AtomicLong smoothOffsetNanos = new AtomicLong(0L);

    private final long calibrationIntervalSeconds;
    private final double maxAdjustRateNanosPerSecond;
    private final ScheduledExecutorService scheduler;

    private volatile boolean chronyAvailable = true;

    public ChronyClockCalibrator() {
        this(30L, 1_000_000.0);
    }

    /**
     * @param calibrationIntervalSeconds   校准间隔（秒），必须为正数，默认30秒
     * @param maxAdjustRateNanosPerSecond  每秒最大平滑调整量（纳秒），必须非负，默认1ms/s，
     *                                     即每30秒校准周期最多调整30ms
     */
    public ChronyClockCalibrator(long calibrationIntervalSeconds, double maxAdjustRateNanosPerSecond) {
        if (calibrationIntervalSeconds <= 0) {
            throw new IllegalArgumentException(
                    "calibrationIntervalSeconds must be positive, got: " + calibrationIntervalSeconds);
        }
        if (maxAdjustRateNanosPerSecond < 0) {
            throw new IllegalArgumentException(
                    "maxAdjustRateNanosPerSecond must not be negative, got: " + maxAdjustRateNanosPerSecond);
        }
        this.calibrationIntervalSeconds = calibrationIntervalSeconds;
        this.maxAdjustRateNanosPerSecond = maxAdjustRateNanosPerSecond;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "chrony-clock-calibrator");
            t.setDaemon(true);
            return t;
        });
        // 同步执行首次校准，确保首个ID生成时平滑偏移已就绪，消除异步首调的回拨窗口
        calibrate();
        startCalibration();
    }

    /**
     * 获取校准后的当前时间戳（毫秒）。
     * 平滑偏移量以纳秒精度维护，输出时转换为毫秒。
     * chrony不可用时保留最后的平滑偏移量继续生效（平滑降级，时间不跳变）。
     *
     * @return 校准后的时间戳（ms）
     */
    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis() + smoothOffsetNanos.get() / 1_000_000;
    }

    @Override
    public void close() {
        scheduler.shutdown();
    }

    private void startCalibration() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                calibrate();
            } catch (Exception e) {
                logger.log(System.Logger.Level.WARNING,
                        "Chrony calibration failed, will retry next cycle: {0}", e.getMessage());
            }
        }, calibrationIntervalSeconds, calibrationIntervalSeconds, TimeUnit.SECONDS);
    }

    /**
     * 执行一次校准，由单线程调度器调用，calibrate本身不保证线程安全。
     * <p>
     * 实现假设：仅有一个写入者（单线程调度器），因此对{@code smoothOffsetNanos}的
     * get-计算-set 操作无需 CAS 循环。{@link #currentTimeMillis()} 仅读取，
     * 与写入者之间通过{@link AtomicLong}的原子读写保证可见性。
     */
    private void calibrate() {
        double offsetSeconds = readChronyOffset();
        if (Double.isNaN(offsetSeconds)) {
            if (chronyAvailable) {
                chronyAvailable = false;
                if (!chronyUnavailableWarned) {
                    chronyUnavailableWarned = true;
                    logger.log(System.Logger.Level.WARNING,
                            "Chrony not available, keeping last smooth offset (no time jump)");
                }
            }
            return;
        }
        if (!chronyAvailable) {
            logger.log(System.Logger.Level.INFO,
                    "Chrony recovered, resuming smooth calibration");
            chronyAvailable = true;
        }

        long targetNanos = (long) (offsetSeconds * 1_000_000_000.0);

        long currentSmooth = smoothOffsetNanos.get();
        long diff = targetNanos - currentSmooth;
        long maxAdjust = (long) (maxAdjustRateNanosPerSecond * calibrationIntervalSeconds);

        long adjustment;
        if (Math.abs(diff) <= maxAdjust) {
            adjustment = diff;
        } else {
            adjustment = diff > 0 ? maxAdjust : -maxAdjust;
        }

        long newSmooth = currentSmooth + adjustment;
        smoothOffsetNanos.set(newSmooth);

        logger.log(System.Logger.Level.DEBUG,
                "Chrony calibration: targetOffset={0}ns, smoothOffset={1}ns, adjustment={2}ns",
                targetNanos, newSmooth, adjustment);
    }

    /**
     * 读取chrony tracking获取系统时钟偏差。
     * 先限时等待进程退出、再读取输出，避免进程挂起时读取流永久阻塞。
     *
     * @return 偏差秒数（正数=系统慢于NTP需加，负数=系统快于NTP需减）；
     *         若chrony不可用返回{@code Double.NaN}
     */
    private double readChronyOffset() {
        try {
            ProcessBuilder pb = new ProcessBuilder("chronyc", "tracking");
            pb.redirectErrorStream(true);
            Process process = pb.start();

            boolean exited = process.waitFor(5, TimeUnit.SECONDS);
            if (!exited) {
                process.destroyForcibly();
                process.waitFor(1, TimeUnit.SECONDS);
                return Double.NaN;
            }

            // 进程已退出，读取输出不会阻塞
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append('\n');
                }
            }

            if (process.exitValue() != 0) {
                return Double.NaN;
            }

            return parseSystemTimeOffset(output.toString());
        } catch (Exception e) {
            return Double.NaN;
        }
    }

    /**
     * 解析chronyc tracking输出中的System time行。
     *
     * @param output chronyc tracking的完整输出
     * @return 偏差秒数；解析失败返回{@code Double.NaN}
     */
    static double parseSystemTimeOffset(String output) {
        if (output == null) {
            return Double.NaN;
        }
        for (String line : output.split("\n")) {
            Matcher matcher = SYSTEM_TIME_PATTERN.matcher(line.trim());
            if (matcher.find()) {
                double magnitude = Double.parseDouble(matcher.group(1));
                String direction = matcher.group(2);
                return "slow".equals(direction) ? magnitude : -magnitude;
            }
        }
        return Double.NaN;
    }
}