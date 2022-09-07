package wang.liangchen.matrix.framework.commons.string;

import wang.liangchen.matrix.framework.commons.collection.CollectionUtil;
import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.exception.Assert;

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
    private final static String FORMAT_REGEX = "\\{(.*?)}";
    private final static String FORMAT_REPLACEMENT = "\\%s";
    private final static String DOT_REPLACEMENT = "\\.";
    private final static Pattern nonNumberPattern = Pattern.compile("[^0-9]");
    private final static Pattern integerPattern = Pattern.compile("[+-]?[0-9]+");
    private final static Pattern floatPattern = Pattern.compile("[+-]?[0-9]+(\\\\.[0-9]+)?([Ee][+-]?[0-9]+)?");


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
        return !isBlank(string);
    }

    public String format(String format, Object... args) {
        // 按顺序替换{}或者{.*}
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

    public String blankString() {
        return Symbol.BLANK.getSymbol();
    }

    public String underline2camelCase(String string) {
        char[] chars = string.toCharArray();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            if ('_' == chars[i]) {
                builder.append((char) (chars[++i] - 32));
                continue;
            }
            builder.append(chars[i]);
        }

        return builder.toString();
    }

    public String camelCase2underline(String string) {
        char[] chars = string.toCharArray();
        StringBuilder builder = new StringBuilder();
        for (char c : chars) {
            if (c >= 'A' && c <= 'Z') {
                builder.append(Symbol.UNDERLINE.getSymbol()).append((char) (c + 32));
                continue;
            }
            builder.append(c);
        }
        return builder.toString();
    }

    public boolean isInteger(String str) {
        if (null == str) {
            return false;
        }
        Matcher matcher = integerPattern.matcher(str);
        if (matcher.find() && matcher.group().equals(str)) {
            return true;
        }
        return false;
    }

    public boolean isFloat(String str) {
        if (null == str) {
            return false;
        }
        Matcher matcher = floatPattern.matcher(str);
        if (matcher.find() && matcher.group().equals(str)) {
            return true;
        }
        return false;
    }

    public boolean isNumber(String str) {
        return isInteger(str) || isFloat(str);
    }

    public String path2Package(String path) {
        if (null == path) {
            return null;
        }
        return path.replace('/', '.').replace('\\', '.');
    }

    public String package2Path(String pack) {
        if (null == pack) {
            return null;
        }
        return pack.replaceAll(DOT_REPLACEMENT, Matcher.quoteReplacement(Symbol.FILE_SEPARATOR.getSymbol()));
    }

    public String getGetter(String fieldName) {
        Assert.INSTANCE.notBlank(fieldName, "fileldName can't be blank");
        return String.format("get%s", firstLetterUpperCase(fieldName));
    }

    public String getSetter(String fieldName) {
        Assert.INSTANCE.notBlank(fieldName, "fileldName can't be blank");
        return String.format("set%s", firstLetterUpperCase(fieldName));
    }
}
