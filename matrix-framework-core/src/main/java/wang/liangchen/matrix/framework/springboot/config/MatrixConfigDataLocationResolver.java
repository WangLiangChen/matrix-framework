package wang.liangchen.matrix.framework.springboot.config;

import org.springframework.boot.context.config.*;
import wang.liangchen.matrix.framework.commons.enumeration.Symbol;

import java.util.Collections;
import java.util.List;

/**
 * @author Liangchen.Wang 2022-06-21 11:56
 */
public class MatrixConfigDataLocationResolver implements ConfigDataLocationResolver<MatrixConfigDataSource> {
    private static final String PREFIX = "matrix://";
    public static final String MARTIX_CONFIG = "/matrix-framework";


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
        String profile = activeProfiles.isEmpty() ? Symbol.BLANK.getSymbol() : Symbol.HYPHEN.getSymbol().concat(activeProfiles.get(0));
        String configRoot = String.format("%s%s%s", location.getNonPrefixedValue(PREFIX), MARTIX_CONFIG, profile);
        return Collections.singletonList(new MatrixConfigDataSource(configRoot));
    }
}
