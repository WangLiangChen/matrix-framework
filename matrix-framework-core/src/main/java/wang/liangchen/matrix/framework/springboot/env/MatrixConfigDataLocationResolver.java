package wang.liangchen.matrix.framework.springboot.env;

import org.springframework.boot.context.config.*;

import java.util.Collections;
import java.util.List;

/**
 * @author Liangchen.Wang 2022-06-21 11:56
 */
public class MatrixConfigDataLocationResolver implements ConfigDataLocationResolver<MatrixConfigDataSource> {
    private static final String PREFIX = "matrix://";

    @Override
    public boolean isResolvable(ConfigDataLocationResolverContext context, ConfigDataLocation location) {
        return location.hasPrefix(PREFIX);
    }

    @Override
    public List<MatrixConfigDataSource> resolve(ConfigDataLocationResolverContext context, ConfigDataLocation location) throws ConfigDataLocationNotFoundException, ConfigDataResourceNotFoundException {
        return Collections.emptyList();
    }

    @Override
    public List<MatrixConfigDataSource> resolveProfileSpecific(ConfigDataLocationResolverContext context, ConfigDataLocation location, Profiles profiles) throws ConfigDataLocationNotFoundException {
        List<String> activeProfiles = profiles.getActive();
        return Collections.singletonList(new MatrixConfigDataSource(location.getNonPrefixedValue(PREFIX), activeProfiles));
    }
}
