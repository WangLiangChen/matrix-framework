package wang.liangchen.matrix.framework.commons.enumeration;

/**
 * @author LiangChen.Wang
 */
public enum Symbol {
    /**
     *
     */
    CARET("^","^"),
    CARET_REGEX("\\^","^正则"),
    EQUAL("=", "等号"),
    AND("&", "与"),
    BLANK("", "空串"),
    SPACE(" ", "空格"),
    TAB("    ", "TAB"),
    PLUS("+", "加号"),
    PLUS_REGEX("\\+", "加号正则"),
    DOT(".", "点号"),
    DOT_REGEX("\\.", "点号正则"),
    POUND("#", "井号"),
    COMMA(",", "逗号"),
    COLON(":", "冒号"),
    VERTICAL("|", "竖线"),
    VERTICAL_REGEX("\\|", "竖线正则"),
    SEMICOLON(";", "分号"),
    HYPHEN("-", "连字号"),
    QUESTION_MARK("?", "问号"),
    QUESTION_MARK_REGEX("\\?", "问号正则"),
    UNDERLINE("_", "下划线"),
    STAR("*", "星号"),
    STAR_REGEX("\\*", "星号正则"),
    AT("@", "@"),
    OPEN_PAREN("(", "左圆括号"),
    OPEN_PAREN_REGEX("\\(", "左圆括号正则"),
    CLOSE_PAREN(")", "右圆括号"),
    CLOSE_PAREN_REGEX("\\)", "右圆括号正则"),
    OPEN_BRACE("{", "左花括号"),
    CLOSE_BRACE("}", "右花括号"),
    OPEN_BRACE_REGEX("\\{", "左花括号正则"),
    CLOSE_BRACE_REGEX("\\}", "右花括号正则"),
    OPEN_BRACKET("[", "左方括号"),
    OPEN_BRACKET_REGEX("\\[", "左方括号正则"),
    CLOSE_BRACKET("]", "右方括号"),
    CLOSE_BRACKET_REGEX("\\]", "右方括号"),
    FILE_SEPARATOR(System.getProperty("file.separator"), "文件分割符"),
    LINE_SEPARATOR(System.getProperty("line.separator"), "换行符"),
    PATH_SEPARATOR(System.getProperty("path.separator"), "路径分割符"),
    USER_DIR(System.getProperty("user.dir"), "用户目录"),
    DOUBLE_SLASH("//", "双斜杠"),
    DOUBLE_BACKSLASH("\\\\", "双反斜杠"),
    BACKSLASH("\\", "反斜杠"),
    URI_SEPARATOR("/", "路径分割符"),

    DOLLAR("$","美元"),
    DOLLAR_REGEX("\\$","美元");

    private final String symbol;
    private final String text;

    Symbol(String symbol, String text) {
        this.symbol = symbol;
        this.text = text;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getText() {
        return text;
    }
}
