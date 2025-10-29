package wang.liangchen.matrix.framework.commons.validation;

/**
 * @author Liangchen.Wang 2022-10-13 9:35
 */
class DynamicMessage {
    @DynamicMessageMaker
    private final String i18n;

    private DynamicMessage(String i18n) {
        this.i18n = i18n;
    }

    public static DynamicMessage newInstance(String i18n) {
        return new DynamicMessage(i18n);
    }

    public String getI18n() {
        return i18n;
    }
}
