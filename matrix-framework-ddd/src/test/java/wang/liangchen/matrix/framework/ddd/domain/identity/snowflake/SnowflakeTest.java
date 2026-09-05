package wang.liangchen.matrix.framework.ddd.domain.identity.snowflake;

import org.junit.jupiter.api.Test;
import wang.liangchen.matrix.framework.ddd.domain.identity.SnowflakeIdentity;
import wang.liangchen.matrix.framework.ddd.support.snowflake.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

class SnowflakeTest {

    @Test
    void nextId_generatesMonotonicallyIncreasing() {
        Snowflake snowflake = Snowflake.builder()
                .workerIdStrategy(new FixedWorkerIdStrategy(1))
                .clockSource(SystemClockSource.INSTANCE)
                .build();

        long prev = snowflake.nextId();
        for (int i = 0; i < 10000; i++) {
            long current = snowflake.nextId();
            assertTrue(current > prev, "ID must be monotonically increasing: prev=" + prev + ", current=" + current);
            prev = current;
        }
    }

    @Test
    void nextId_generatesUniqueIds_underConcurrency() throws InterruptedException {
        int threadCount = 16;
        int idsPerThread = 5000;
        Snowflake snowflake = Snowflake.builder()
                .workerIdStrategy(new FixedWorkerIdStrategy(1))
                .clockSource(SystemClockSource.INSTANCE)
                .build();

        Set<Long> allIds = Collections.newSetFromMap(new ConcurrentHashMap<>());
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int t = 0; t < threadCount; t++) {
            executor.submit(() -> {
                try {
                    for (int i = 0; i < idsPerThread; i++) {
                        allIds.add(snowflake.nextId());
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(30, TimeUnit.SECONDS));
        executor.shutdown();
        assertEquals((long) threadCount * idsPerThread, allIds.size(), "All IDs must be unique");
    }

    @Test
    void nextId_bitStructure() {
        Snowflake snowflake = Snowflake.builder()
                .workerIdStrategy(new FixedWorkerIdStrategy(123))
                .clockSource(SystemClockSource.INSTANCE)
                .build();

        long id = snowflake.nextId();

        long sequence = id & 0xFFF;
        long workerId = (id >> 12) & 0x3FF;
        long timestamp = id >> 22;

        assertEquals(123, workerId, "WorkerId bits should match");
        assertTrue(timestamp > 0, "Timestamp bits should be positive");
        assertTrue(sequence >= 0 && sequence <= 4095, "Sequence must be in [0, 4095]");
    }

    @Test
    void builder_epochDateTimeParsedAsUtc() {
        LocalDateTime customEpoch = LocalDateTime.of(2020, 1, 1, 0, 0, 0);
        Snowflake snowflake = Snowflake.builder()
                .epoch(customEpoch)
                .workerIdStrategy(new FixedWorkerIdStrategy(1))
                .clockSource(SystemClockSource.INSTANCE)
                .build();

        assertEquals(1577836800000L, snowflake.getEpoch(), "epoch must be parsed as UTC regardless of system timezone");
    }

    @Test
    void snowflakeId_initializeRejectsNull() {
        assertThrows(NullPointerException.class, () -> SnowflakeIdentity.initialize(null));
    }

    @Test
    void nextId_rejectsFutureEpoch() {
        Snowflake snowflake = Snowflake.builder()
                .epoch(System.currentTimeMillis() + 100_000L)
                .workerIdStrategy(new FixedWorkerIdStrategy(1))
                .clockSource(SystemClockSource.INSTANCE)
                .build();
        assertThrows(SnowflakeOverflowException.class, snowflake::nextId,
                "epoch晚于当前时间（elapsed为负）必须拒绝");
    }

    @Test
    void nextId_rejectsTimestampOverflow() {
        Snowflake snowflake = Snowflake.builder()
                .epoch(-500_000_000_000L)
                .workerIdStrategy(new FixedWorkerIdStrategy(1))
                .clockSource(SystemClockSource.INSTANCE)
                .build();
        assertThrows(SnowflakeOverflowException.class, snowflake::nextId,
                "elapsed超过41位上限必须拒绝");
    }

    @Test
    void nextId_sequenceIncrementsWithinSameMillis() {
        long fixedMillis = System.currentTimeMillis();
        Snowflake snowflake = Snowflake.builder()
                .workerIdStrategy(new FixedWorkerIdStrategy(1))
                .clockSource(() -> fixedMillis)
                .build();

        long first = snowflake.nextId();
        long second = snowflake.nextId();
        long third = snowflake.nextId();

        assertEquals(first >> 22, second >> 22, "同一时钟下ID的相对时间戳应相同");
        assertEquals(first >> 22, third >> 22, "同一时钟下ID的相对时间戳应相同");
        assertEquals(Snowflake.extractSequence(first) + 1, Snowflake.extractSequence(second),
                "同一毫秒内序列号应连续递增");
        assertEquals(Snowflake.extractSequence(second) + 1, Snowflake.extractSequence(third),
                "同一毫秒内序列号应连续递增");
    }

    @Test
    void nextId_sequenceOverflowSkipsToNextMillis() {
        AtomicLong reads = new AtomicLong();
        AtomicLong now = new AtomicLong(System.currentTimeMillis());
        ClockSource clock = () -> reads.incrementAndGet() <= 6000 ? now.get() : now.incrementAndGet();
        Snowflake snowflake = Snowflake.builder()
                .workerIdStrategy(new FixedWorkerIdStrategy(1))
                .clockSource(clock)
                .build();

        Set<Long> ids = new HashSet<>();
        Set<Long> relativeTimestamps = new HashSet<>();
        for (int i = 0; i < 5000; i++) {
            long id = snowflake.nextId();
            ids.add(id);
            relativeTimestamps.add(id >> 22);
        }

        assertEquals(5000, ids.size(), "All IDs must be unique after sequence overflow");
        assertTrue(relativeTimestamps.size() > 1, "Sequence overflow must skip to next millis");
    }

    @Test
    void nextId_handlesSmallClockBackward() {
        AtomicLong now = new AtomicLong(1_000_000L);
        Snowflake snowflake = Snowflake.builder()
                .epoch(0L)
                .workerIdStrategy(new FixedWorkerIdStrategy(1))
                .clockSource(now::incrementAndGet)
                .build();

        long id1 = snowflake.nextId();

        now.set(1_000_000L - 4L);
        long id2 = snowflake.nextId();

        assertTrue(id2 > id1, "容忍阈值内的回拨恢复后应继续生成更大ID");
    }

    @Test
    void nextId_throwsOnLargeClockBackward() {
        AtomicLong now = new AtomicLong(1_000_000L);
        Snowflake snowflake = Snowflake.builder()
                .epoch(0L)
                .workerIdStrategy(new FixedWorkerIdStrategy(1))
                .clockSource(now::incrementAndGet)
                .build();

        snowflake.nextId();

        now.set(1_000_000L - 100L);
        assertThrows(ClockBackwardException.class, snowflake::nextId);
    }

    @Test
    void extract_methodsDecodeIdParts() {
        Snowflake snowflake = Snowflake.builder()
                .workerIdStrategy(new FixedWorkerIdStrategy(123))
                .clockSource(SystemClockSource.INSTANCE)
                .build();

        long before = System.currentTimeMillis();
        long id = snowflake.nextId();
        long after = System.currentTimeMillis();

        assertEquals(123, Snowflake.extractWorkerId(id));
        long sequence = Snowflake.extractSequence(id);
        assertTrue(sequence >= 0 && sequence <= 4095);
        long timestamp = snowflake.extractTimestamp(id);
        assertTrue(timestamp >= before - 10 && timestamp <= after + 10,
                "extractTimestamp应还原生成时刻（epoch+相对时间）");
    }

    @Test
    void fixedWorkerIdStrategy_validatesRange() {
        assertThrows(IllegalArgumentException.class, () -> new FixedWorkerIdStrategy(-1));
        assertThrows(IllegalArgumentException.class, () -> new FixedWorkerIdStrategy(1024));
        assertDoesNotThrow(() -> new FixedWorkerIdStrategy(0));
        assertDoesNotThrow(() -> new FixedWorkerIdStrategy(1023));
    }

    @Test
    void ipWorkerIdStrategy_resolvesValidId() {
        IpWorkerIdStrategy strategy = new IpWorkerIdStrategy();
        long workerId = strategy.resolveWorkerId();
        assertTrue(workerId >= 0 && workerId <= 1023, "IP-based workerId must be in [0, 1023], got: " + workerId);
    }

    @Test
    void ipWorkerIdStrategy_rejectsNonIpv4() {
        assertThrows(IllegalArgumentException.class,
                () -> IpWorkerIdStrategy.ipV4Lower16BitsToLong("not-an-ip"));
        assertThrows(IllegalArgumentException.class,
                () -> IpWorkerIdStrategy.ipV4Lower16BitsToLong("192.168.1"));
        assertThrows(IllegalArgumentException.class,
                () -> IpWorkerIdStrategy.ipV4Lower16BitsToLong("192.168.1.2.3"));
        assertDoesNotThrow(() -> {
            long result = IpWorkerIdStrategy.ipV4Lower16BitsToLong("192.168.1.5");
            assertEquals(0x0105, result, "lower 16 bits: 1*256 + 5 = 261");
        });
    }

    @Test
    void waitClockBackwardStrategy_toleratesSmallBackward() {
        WaitClockBackwardStrategy strategy = new WaitClockBackwardStrategy();
        long corrected = strategy.onBackward(100, 97, SystemClockSource.INSTANCE);
        assertTrue(corrected >= 100, "校正后的时间戳不得早于lastTimestamp");
    }

    @Test
    void waitClockBackwardStrategy_exceedsThreshold() {
        WaitClockBackwardStrategy strategy = new WaitClockBackwardStrategy(5);
        assertThrows(ClockBackwardException.class,
                () -> strategy.onBackward(100, 90, SystemClockSource.INSTANCE));
    }

    @Test
    void waitClockBackwardStrategy_validatesConstructor() {
        assertThrows(IllegalArgumentException.class, () -> new WaitClockBackwardStrategy(0));
        assertThrows(IllegalArgumentException.class, () -> new WaitClockBackwardStrategy(-1));
    }

    @Test
    void chronyClockCalibrator_parsesSlowOffset() {
        String output = "Reference ID    : 7F7F0101\n"
                + "System time     : 0.000012345 seconds slow of NTP time\n"
                + "Last offset     : -0.000023456";
        double offset = ChronyClockCalibrator.parseSystemTimeOffset(output);
        assertEquals(0.000012345, offset, 1e-12);
    }

    @Test
    void chronyClockCalibrator_parsesFastOffset() {
        String output = "System time     : 0.000012345 seconds fast of NTP time";
        double offset = ChronyClockCalibrator.parseSystemTimeOffset(output);
        assertEquals(-0.000012345, offset, 1e-12);
    }

    @Test
    void chronyClockCalibrator_returnsNaNForUnparseable() {
        assertTrue(Double.isNaN(ChronyClockCalibrator.parseSystemTimeOffset("no matching line here")));
        assertTrue(Double.isNaN(ChronyClockCalibrator.parseSystemTimeOffset(null)));
    }

    @Test
    void chronyClockCalibrator_validatesConstructorArguments() {
        assertThrows(IllegalArgumentException.class, () -> new ChronyClockCalibrator(0, 1_000_000.0));
        assertThrows(IllegalArgumentException.class, () -> new ChronyClockCalibrator(-1, 1_000_000.0));
        assertThrows(IllegalArgumentException.class, () -> new ChronyClockCalibrator(30, -1.0));
    }

    @Test
    void snowflakeId_nextGeneratesId() {
        SnowflakeIdentity id1 = SnowflakeIdentity.next();
        SnowflakeIdentity id2 = SnowflakeIdentity.next();
        assertNotNull(id1);
        assertNotNull(id2);
        assertTrue(id2.value() > id1.value(), "Sequential SnowflakeIds must be monotonically increasing");
    }

    @Test
    void snowflakeId_ofCreatesFromValue() {
        SnowflakeIdentity id = SnowflakeIdentity.of(12345L);
        assertEquals(12345L, id.value());
        assertEquals("12345", id.toString());
    }

    @Test
    void snowflakeId_setGeneratorCustomizesGeneration() {
        Snowflake original = SnowflakeIdentity.getSnowflake();
        Snowflake custom = Snowflake.builder()
                .workerIdStrategy(new FixedWorkerIdStrategy(42))
                .clockSource(SystemClockSource.INSTANCE)
                .build();
        Snowflake replaced = SnowflakeIdentity.initialize(custom);
        assertSame(original, replaced, "initialize应返回被替换的旧生成器");
        try {
            SnowflakeIdentity id = SnowflakeIdentity.next();
            assertEquals(42, Snowflake.extractWorkerId(id.value()), "Custom generator's workerId should be used");
        } finally {
            SnowflakeIdentity.initialize(original);
        }
    }

    @Test
    void snowflake_closeDoesNotCloseInjectedClockSource() {
        RecordingClockSource clock = new RecordingClockSource();
        Snowflake snowflake = Snowflake.builder()
                .workerIdStrategy(new FixedWorkerIdStrategy(1))
                .clockSource(clock)
                .build();

        snowflake.close();

        assertFalse(clock.closed, "注入的时钟源由调用方管理，close()不应将其关闭");
    }

    @Test
    void snowflake_remainsUsableAfterClose() {
        Snowflake snowflake = Snowflake.builder()
                .workerIdStrategy(new FixedWorkerIdStrategy(1))
                .build(); // 默认chrony时钟源，归生成器所有

        long before = snowflake.nextId();
        snowflake.close();
        long after = snowflake.nextId();

        assertTrue(after > before, "close()后生成器仍应可用（校准冻结，时间跟随系统时钟）");
    }

    @Test
    void snowflakeId_initializeDoesNotCloseOldGenerator() {
        Snowflake original = SnowflakeIdentity.getSnowflake();
        try {
            RecordingClockSource clock = new RecordingClockSource();
            Snowflake first = Snowflake.builder()
                    .workerIdStrategy(new FixedWorkerIdStrategy(1))
                    .clockSource(clock)
                    .build();
            SnowflakeIdentity.initialize(first);

            Snowflake second = Snowflake.builder()
                    .workerIdStrategy(new FixedWorkerIdStrategy(2))
                    .clockSource(SystemClockSource.INSTANCE)
                    .build();
            Snowflake old = SnowflakeIdentity.initialize(second);
            try {
                assertSame(first, old, "initialize应返回被替换的旧生成器");
                assertFalse(clock.closed, "initialize不应自动关闭旧生成器（可能仍被使用或与新生成器共享时钟源）");
            } finally {
                SnowflakeIdentity.initialize(first);
            }
        } finally {
            SnowflakeIdentity.initialize(original);
        }
    }

    @Test
    void snowflakeId_shutdownReleasesAndRecreates() {
        assertNotNull(SnowflakeIdentity.getSnowflake());
        SnowflakeIdentity.shutdown();
        assertNotNull(SnowflakeIdentity.next(), "shutdown后首次使用应按默认配置重新创建生成器");
    }

    @Test
    void snowflakeId_generatorIsSingleton() {
        Snowflake first = SnowflakeIdentity.getSnowflake();
        assertSame(first, SnowflakeIdentity.getSnowflake(), "全局生成器应为同一实例（单例契约）");
        SnowflakeIdentity.next();
        assertSame(first, SnowflakeIdentity.getSnowflake(), "取号不应重建生成器（单例契约）");
    }

    @Test
    void snowflakeId_initializeReturnsNullAfterShutdown() {
        Snowflake original = SnowflakeIdentity.getSnowflake();
        try {
            SnowflakeIdentity.shutdown();
            Snowflake replacement = Snowflake.builder()
                    .workerIdStrategy(new FixedWorkerIdStrategy(9))
                    .clockSource(SystemClockSource.INSTANCE)
                    .build();
            assertNull(SnowflakeIdentity.initialize(replacement), "shutdown后生成器已移除，initialize应返回null");
            assertSame(replacement, SnowflakeIdentity.getSnowflake(), "注入的生成器应立即生效");
        } finally {
            SnowflakeIdentity.initialize(original);
        }
    }

    @Test
    void snowflake_closeIsIdempotent() {
        Snowflake snowflake = Snowflake.builder()
                .workerIdStrategy(new FixedWorkerIdStrategy(1))
                .build();

        snowflake.close();
        assertDoesNotThrow(snowflake::close, "二次close()应幂等、不抛异常");
        assertDoesNotThrow(snowflake::close, "三次close()应幂等、不抛异常");
    }

    @Test
    void snowflake_closeConcurrentWithNextId() throws InterruptedException {
        int threadCount = 8;
        int idsPerThread = 2000;
        Snowflake snowflake = Snowflake.builder()
                .workerIdStrategy(new FixedWorkerIdStrategy(1))
                .clockSource(SystemClockSource.INSTANCE)
                .build();

        Set<Long> allIds = Collections.newSetFromMap(new ConcurrentHashMap<>());
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount + 1);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount + 1);

        for (int t = 0; t < threadCount; t++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    for (int i = 0; i < idsPerThread; i++) {
                        allIds.add(snowflake.nextId());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        executor.submit(() -> {
            try {
                startLatch.await();
                snowflake.close();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                doneLatch.countDown();
            }
        });

        startLatch.countDown();
        assertTrue(doneLatch.await(30, TimeUnit.SECONDS));
        executor.shutdown();
        assertEquals((long) threadCount * idsPerThread, allIds.size(),
                "close()与nextId()并发时所有ID必须唯一");
    }

    @Test
    void snowflakeId_lazyInitIsThreadSafe() throws InterruptedException {
        Snowflake original = SnowflakeIdentity.getSnowflake();
        try {
            SnowflakeIdentity.shutdown();

            int threadCount = 16;
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch doneLatch = new CountDownLatch(threadCount);
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            Set<Snowflake> generators = Collections.newSetFromMap(new ConcurrentHashMap<>());

            for (int t = 0; t < threadCount; t++) {
                executor.submit(() -> {
                    try {
                        startLatch.await();
                        generators.add(SnowflakeIdentity.getSnowflake());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        doneLatch.countDown();
                    }
                });
            }

            startLatch.countDown();
            assertTrue(doneLatch.await(10, TimeUnit.SECONDS));
            executor.shutdown();
            assertEquals(1, generators.size(),
                    "多线程并发惰性初始化必须只创建一个生成器实例");
        } finally {
            SnowflakeIdentity.initialize(original);
        }
    }

    @Test
    void snowflakeId_initializeConcurrentWithLazyInit() throws InterruptedException {
        Snowflake original = SnowflakeIdentity.getSnowflake();
        try {
            SnowflakeIdentity.shutdown();

            Snowflake custom = Snowflake.builder()
                    .workerIdStrategy(new FixedWorkerIdStrategy(77))
                    .clockSource(SystemClockSource.INSTANCE)
                    .build();

            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch doneLatch = new CountDownLatch(2);
            ExecutorService executor = Executors.newFixedThreadPool(2);
            AtomicLong lazyInitWorkerId = new AtomicLong(-1);

            executor.submit(() -> {
                try {
                    startLatch.await();
                    lazyInitWorkerId.set(SnowflakeIdentity.getSnowflake().getWorkerId());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });

            executor.submit(() -> {
                try {
                    startLatch.await();
                    SnowflakeIdentity.initialize(custom);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });

            startLatch.countDown();
            assertTrue(doneLatch.await(10, TimeUnit.SECONDS));
            executor.shutdown();

            Snowflake winner = SnowflakeIdentity.getSnowflake();
            long winnerWorkerId = winner.getWorkerId();
            assertTrue(winnerWorkerId == 77 || winnerWorkerId == lazyInitWorkerId.get(),
                    "initialize与惰性初始化并发时，最终生成器必须是二者之一，"
                            + "got winner=" + winnerWorkerId + " lazyInit=" + lazyInitWorkerId.get());
        } finally {
            SnowflakeIdentity.initialize(original);
        }
    }

    @Test
    void snowflake_builderDefaultClockSourceIsOwned() {
        Snowflake snowflake = Snowflake.builder()
                .workerIdStrategy(new FixedWorkerIdStrategy(1))
                .build();

        long before = snowflake.nextId();
        snowflake.close();
        long after = snowflake.nextId();

        assertTrue(after > before,
                "默认时钟源归生成器所有，close()后生成器仍可用（时间跟随系统时钟）");
    }

    @Test
    void snowflakeId_nextGeneratesUniqueIds_underConcurrency() throws InterruptedException {
        int threadCount = 8;
        int idsPerThread = 10_000;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        Set<Long> allIds = Collections.newSetFromMap(new ConcurrentHashMap<>());

        for (int t = 0; t < threadCount; t++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    for (int i = 0; i < idsPerThread; i++) {
                        allIds.add(SnowflakeIdentity.next().value());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        assertTrue(doneLatch.await(30, TimeUnit.SECONDS));
        executor.shutdown();
        assertEquals(threadCount * idsPerThread, allIds.size(),
                "通过 SnowflakeIdentity.next() 调用的全局单例路径，并发下所有ID应唯一");
    }

    @Test
    void snowflake_constructorValidatesWorkerIdBoundary() {
        assertDoesNotThrow(() -> Snowflake.builder()
                .workerIdStrategy(new FixedWorkerIdStrategy(0))
                .clockSource(SystemClockSource.INSTANCE)
                .build(), "workerId=0 边界值应合法");

        assertDoesNotThrow(() -> Snowflake.builder()
                .workerIdStrategy(new FixedWorkerIdStrategy(1023))
                .clockSource(SystemClockSource.INSTANCE)
                .build(), "workerId=1023 边界值应合法");

        assertThrows(IllegalArgumentException.class, () -> Snowflake.builder()
                .workerIdStrategy(new FixedWorkerIdStrategy(-1))
                .clockSource(SystemClockSource.INSTANCE)
                .build(), "workerId=-1 应拒绝");

        assertThrows(IllegalArgumentException.class, () -> Snowflake.builder()
                .workerIdStrategy(new FixedWorkerIdStrategy(1024))
                .clockSource(SystemClockSource.INSTANCE)
                .build(), "workerId=1024 应拒绝");
    }

    private static final class RecordingClockSource implements ClockSource {

        private volatile boolean closed;

        @Override
        public long currentTimeMillis() {
            return System.currentTimeMillis();
        }

        @Override
        public void close() {
            closed = true;
        }
    }
}