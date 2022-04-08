package wang.liangchen.matrix.framework.commons.enumeration;

/**
 * @author LiangChen.Wang
 */
public enum Symbol {
    /**
     *
     */
    EQUAL("=", "等号"),
    BLANK("", "空串"),
    SPACE(" ", "空格"),
    TAB("   ", "TAB"),
    PLUS("+", "加号"),
    DOT(".", "点号"),
    POUND("#", "井号"),
    COMMA(",", "逗号"),
    COLON(":", "冒号"),
    VERTICAL("|", "竖线"),
    SEMICOLON(";", "分号"),
    HYPHEN("-", "连字号"),
    UNDERLINE("_", "下划线"),
    STAR("*", "星号"),
    AT("@", "@"),
    FILE_SEPARATOR(System.getProperty("file.separator"), "文件分割符"),
    LINE_SEPARATOR(System.getProperty("line.separator"), "换行符"),
    PATH_SEPARATOR(System.getProperty("path.separator"), "路径分割符"),
    DOUBLE_SLASH("//", "双斜杠"),
    DOUBLE_BACKSLASH("\\\\", "双反斜杠"),
    BACKSLASH("\\", "反斜杠"),
    URI_SEPARATOR("/", "路径分割符");

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
