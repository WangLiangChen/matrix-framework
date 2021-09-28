package liangchen.wang.matrix.framework.commons.utils;

import liangchen.wang.matrix.framework.commons.enumeration.Symbol;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Liangchen.Wang 2021-08-19 19:53
 */
public enum StringUtil {
    /**
     * instance
     */
    INSTANCE;
    private final String FORMAT_REGEX = "\\{(.*?)\\}";
    private final String FORMAT_REPLACEMENT = "\\%s";
    private final Pattern nonNumberPattern = Pattern.compile("[^0-9]");

    public boolean isNull(String string) {
        return null == string;
    }

    public boolean isNotNull(String string) {
        return null != string;
    }

    public boolean isBlank(String string) {
        if (null == string) {
            return true;
        }
        return string.isEmpty();
    }

    public boolean isNotBlank(String string) {
        if (null == string) {
            return false;
        }
        return !string.isEmpty();
    }

    /**
     * 按顺序替换{}或者{.*}
     *
     * @param format
     * @param args
     * @return 替换后的字符串
     */
    public String format(String format, Object... args) {
        if (isBlank(format)) {
            return format;
        }
        if (CollectionUtil.INSTANCE.isEmpty(args)) {
            return format;
        }
        format = format.replaceAll(FORMAT_REGEX, FORMAT_REPLACEMENT);
        return String.format(format, args);
    }

    public String firstLetterLowerCase(String string) {
        if (isBlank(string)) {
            return string;
        }
        char[] chars = string.toCharArray();
        char first = chars[0];
        if (first >= 'A' && first <= 'Z') {
            first = (char) (first + 32);
        }
        chars[0] = first;
        return new String(chars);
    }

    public String firstLetterUpperCase(String string) {
        if (isBlank(string)) {
            return string;
        }
        char[] chars = string.toCharArray();
        char first = chars[0];
        if (first >= 'a' && first <= 'z') {
            first = (char) (first - 32);
        }
        chars[0] = first;
        return new String(chars);
    }

    public String firstLetterConvertCase(String string) {
        if (isBlank(string)) {
            return string;
        }
        char[] chars = string.toCharArray();
        char first = chars[0];
        if (first >= 'a' && first <= 'z') {
            first = (char) (first - 32);
        }
        if (first >= 'A' && first <= 'Z') {
            first = (char) (first + 32);
        }
        chars[0] = first;
        return new String(chars);
    }

    public String clearBlank(String string) {
        if (isBlank(string)) {
            return string;
        }
        return string.replaceAll("\\s", "");
    }

    public String extractNumbers(String string) {
        if (isBlank(string)) {
            return string;
        }
        Matcher matcher = nonNumberPattern.matcher(string);
        return matcher.replaceAll("");
    }
}
