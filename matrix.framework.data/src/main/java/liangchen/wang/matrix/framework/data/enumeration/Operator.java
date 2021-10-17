package liangchen.wang.matrix.framework.data.enumeration;

/**
 * @author .LiangChen.Wang
 */
public enum Operator {
    /**
     *
     */
    EQUALS("="), NOTEQUALS("!="), LESSTHAN("<"), LESSTHAN_OR_EQUALS("<="), GREATERTHAN(">"), GREATERTHAN_OR_EQUALS(">="), ISNULL(" is null"), ISNOTNULL(" is not null"),
    CONTAINS(" like "), NOTCONTAINS(" not like "), STARTWITH(" like "), NOTSTARTWITH(" not like "), ENDWITH(" like "), NOTENDWITH(" not like "), BETWEEN(" between "), NOTBETWEEN(" not between "), IN(" in "), NOTIN(" not in ");
    private final String operator;

    Operator(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }
}
