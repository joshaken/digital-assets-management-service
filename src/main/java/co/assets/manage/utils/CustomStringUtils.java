package co.assets.manage.utils;

public class CustomStringUtils {
    /**
     * 将输入字符串截取为最大长度限制
     *
     * @param input 待处理的字符串
     * @param maxLength 最大长度，例如 500
     * @return 截取后的字符串，如果 input 为 null，返回 null
     */
    public static String truncateToMaxLength(String input, int maxLength) {
        if (input == null) {
            return null;
        }
        if (input.length() <= maxLength) {
            return input;
        }
        return input.substring(0, maxLength);
    }

    public static String truncateToFailReasonMaxLen(String input) {
        return truncateToMaxLength(input, 500);
    }
}
