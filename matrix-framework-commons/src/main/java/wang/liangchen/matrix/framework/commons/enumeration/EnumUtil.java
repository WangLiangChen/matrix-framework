package wang.liangchen.matrix.framework.commons.enumeration;

/**
 * @author Liangchen.Wang 2022-10-10 17:22
 */
public enum EnumUtil {
    //
    INSTANCE;

    public <T extends Enum<T>> T valueOfNullable(Class<T> enumType, String name) {
        try {
            return Enum.valueOf(enumType, name);
        } catch (NullPointerException | IllegalArgumentException e) {
            return null;
        }
    }

    public <T extends Enum<T>> T valueOfNullable(Class<T> enumType, int ordinal) {
        try {
            return enumType.getEnumConstants()[ordinal];
        } catch (Exception e) {
            return null;
        }
    }
}
