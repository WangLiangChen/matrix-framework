package wang.liangchen.matrix.framework.commons.encryption;

/**
 * @author wangliangchen
 */
public enum LuhnUtil {
    /**
     *
     */
    INSTANCE;

    public String generate(String string, int bit) {
        String result = string;
        for (int i = 0; i < bit; i++) {
            result = generate(result);
        }
        return result;
    }

    public String generate(String string) {
        int digit = 10 - doLuhn(string, true) % 10;
        digit = digit == 10 ? 0 : digit;
        return String.format("%s%d", string, digit);
    }

    public boolean validate(String string, int bit) {
        for (int i = 0; i < bit; i++) {
            String sub = string.substring(0, string.length() - i);
            boolean validate = validate(sub);
            if (!validate) {
                return false;
            }
        }
        return true;
    }

    public boolean validate(String string) {
        return doLuhn(string, false) % 10 == 0;
    }


    private int doLuhn(String string, boolean evenPosition) {
        int[] array = string2Array(string);
        int sum = 0;
        for (int i = array.length - 1; i >= 0; i--) {
            int n = array[i];
            if (evenPosition) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            evenPosition = !evenPosition;
        }
        return sum;
    }

    private int[] string2Array(String string) {
        char[] chars = string.toCharArray();
        int[] array = new int[chars.length];
        for (int i = 0; i < chars.length; i++) {
            array[i] = (byte) chars[i] - 48;
        }
        return array;
    }
}
