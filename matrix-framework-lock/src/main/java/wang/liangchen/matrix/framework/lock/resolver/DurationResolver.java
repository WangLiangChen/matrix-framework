package wang.liangchen.matrix.framework.lock.resolver;

import wang.liangchen.matrix.framework.commons.exception.MatrixInfoException;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Liangchen.Wang 2022-08-26 15:12
 */
public enum DurationResolver {
    INSTANCE;
    private static final Pattern ISO8601 = Pattern.compile("^[\\+\\-]?P.*$");
    private static final Pattern SIMPLE = Pattern.compile("^([\\+\\-]?\\d+)([a-zA-Z]{0,2})$");

    private static final Map<String, ChronoUnit> UNITS = new HashMap<String, ChronoUnit>() {{
        put("d", ChronoUnit.DAYS);
        put("h", ChronoUnit.HOURS);
        put("m", ChronoUnit.MINUTES);
        put("s", ChronoUnit.SECONDS);
        put("ms", ChronoUnit.MILLIS);
        put("", ChronoUnit.MILLIS);
        put("us", ChronoUnit.MICROS);
        put("ns", ChronoUnit.NANOS);
    }};

    public Duration resolve(String durationString) {
        if (ISO8601.matcher(durationString).matches()) {
            return Duration.parse(durationString);
        }
        Matcher matcher = SIMPLE.matcher(durationString);
        if (matcher.matches()) {
            long amount = Long.parseLong(matcher.group(1));
            ChronoUnit unit = UNITS.get(matcher.group(2));
            return Duration.of(amount, unit);
        }
        throw new MatrixInfoException("'{}' is not a valid duration", durationString);
    }
}
