package wang.liangchen.matrix.framework.springboot.startup;

import wang.liangchen.matrix.framework.commons.utils.StopWatch;

import java.util.HashSet;
import java.util.Set;

public interface StartupStatic {
    String SPRING_CONFIG_IMPORT = "spring.config.import";
    String DEFAULT_SCAN_PACKAGES = "wang.liangchen.matrix";
    Set<String> excludeScanPackages = new HashSet<>();
    StopWatch stopWatch = new StopWatch();
}
