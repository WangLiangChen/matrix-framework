package wang.liangchen.matrix.framework.spring.boot.startup;

import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.boot.diagnostics.FailureAnalyzer;

public final class StartupFailureAnalyzer implements FailureAnalyzer {
    @Override
    public FailureAnalysis analyze(Throwable failure) {
        return null;
    }
}
