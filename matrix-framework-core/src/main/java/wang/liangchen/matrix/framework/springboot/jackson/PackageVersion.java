package wang.liangchen.matrix.framework.springboot.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;
import com.fasterxml.jackson.core.util.VersionUtil;

/**
 * @author Liangchen.Wang 2022-12-12 14:40
 */
public final class PackageVersion implements Versioned {
    public static final Version VERSION = VersionUtil.parseVersion("2.0.0", "wang.liangchen.matrix", "matrix-framework-core");

    public PackageVersion() {
    }

    public Version version() {
        return VERSION;
    }
}
