package wang.liangchen.matrix.framework.spring.boot.startup;

import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.boot.diagnostics.FailureAnalyzer;

import static wang.liangchen.matrix.framework.spring.boot.startup.BootStartupStopWatch.watchTask;

public final class StartupFailureAnalyzer implements FailureAnalyzer {
    @Override
    public FailureAnalysis analyze(Throwable failure) {
        return new FailureAnalysis("Matrix Framework startup failed", null, failure);
    }
}
