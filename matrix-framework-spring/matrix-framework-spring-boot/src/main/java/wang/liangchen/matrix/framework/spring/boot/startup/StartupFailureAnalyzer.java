package wang.liangchen.matrix.framework.spring.boot.startup;

import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.boot.diagnostics.FailureAnalysisReporter;
import org.springframework.boot.diagnostics.FailureAnalyzer;

public final class StartupFailureAnalyzer implements FailureAnalyzer, FailureAnalysisReporter {
    @Override
    public FailureAnalysis analyze(Throwable failure) {
        return new FailureAnalysis("Matrix Framework startup failed", null, failure);
    }

    @Override
    public void report(FailureAnalysis analysis) {

    }
}
