package wang.liangchen.matrix.framework.commons.encryption;

/**
 * @author wangliangchen
 */
public enum LuhnUtil {
    /**
     *
     */
    INSTANCE;

    public String generate(String numberString, int checkBit) {
        String result = numberString;
        for (int i = 0; i < checkBit; i++) {
            result = generate(result);
        }
        return result;
    }

    public String generate(String numberString) {
        int digit = 10 - doLuhn(numberString, true) % 10;
        digit = digit == 10 ? 0 : digit;
        return String.format("%s%d", numberString, digit);
    }

    public boolean validate(String numberString, int checkBit) {
        for (int i = 0; i < checkBit; i++) {
            String sub = numberString.substring(0, numberString.length() - i);
            boolean validate = validate(sub);
            if (!validate) {
                return false;
            }
        }
        return true;
    }

    public boolean validate(String numberString) {
        return doLuhn(numberString, false) % 10 == 0;
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
            // 48 -> 0,数值就是相对于48的偏移量
            array[i] = (byte) chars[i] - 48;
        }
        return array;
    }
}
