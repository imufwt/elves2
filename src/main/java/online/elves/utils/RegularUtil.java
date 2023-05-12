package online.elves.utils;

import cn.hutool.core.collection.CollUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 正则工具类
 */
public class RegularUtil {

    /**
     * 用户自定义命令
     */
    private static String USER_SET = "[a-zA-Z0-9\\u4e00-\\u9fa5]{1,3}";

    /**
     * md 语法图片
     */
    private static String MD_IMG = "\\S*?!\\[\\S*?\\]\\(\\S*?\\)";

    /**
     * 手机号正则
     */
    private static String MOBILE = "1[3456789]\\d{9}";
    /**
     * 数字串
     */
    private static String NUMBER = "^[0-9]\\d*$";

    /**
     * 数字串 1-max
     */
    private static String NUMBER_1_MAX = "^[1-9]\\d*$";

    /**
     * integer (-MAX, MAX)
     */
    public final static String REGEX_INTEGER = "^[-\\+]?\\d+$";
    /**
     * integer [1, MAX)
     */
    public final static String REGEX_POSITIVE_INTEGER = "^\\+?[1-9]\\d*$";
    /**
     * integer (-MAX, -1]
     */
    public final static String REGEX_NEGATIVE_INTEGER = "^-[1-9]\\d*$";
    /**
     * integer [0, MAX), only numeric
     */
    public final static String REGEX_NUMERIC = "^\\d+$";
    /**
     * decimal (-MAX, MAX)
     */
    public final static String REGEX_DECIMAL = "^[-\\+]?\\d+\\.\\d+$";
    /**
     * decimal (0.0, MAX)
     */
    public final static String REGEX_POSITIVE_DECIMAL = "^\\+?([1-9]+\\.\\d+|0\\.\\d*[1-9])$";
    /**
     * decimal (-MAX, -0.0)
     */
    public final static String REGEX_NEGATIVE_DECIMAL = "^-([1-9]+\\.\\d+|0\\.\\d*[1-9])$";
    /**
     * decimal + integer (-MAX, MAX)
     */
    public final static String REGEX_REAL_NUMBER = "^[-+]?(\\d+|\\d+\\.\\d+)$";
    /**
     * decimal + integer [0, MAX)
     */
    public final static String REGEX_NON_NEGATIVE_REAL_NUMBER = "^\\+?(\\d+|\\d+\\.\\d+)$";

    /**
     * YYYY-MM-DD HH:mm:ss / YYYY-MM-DD
     */
    private static final String REGEX_DATE_TIME = "^((\\d{2}(([02468][048])|([13579][26]))[\\-/\\s]?((((0?[13578])|" +
            "(1[02]))[\\-/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-/\\s]?((0?[1-9])|([1-2][0-9])|" +
            "(30)))|(0?2[\\-/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))" +
            "[\\-/\\s]?((((0?[13578])|(1[02]))[\\-/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))" +
            "[\\-/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))" +
            "(\\s(((0?[0-9])|([1][0-9])|([2][0-4])):([0-5]?[0-9])((\\s)|(:([0-5]?[0-9])))))?$";
    /**
     * YYYY-MM-DD
     */
    private static final String REGEX_DATE = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|" +
            "(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|" +
            "([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|" +
            "([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|" +
            "(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|" +
            "(1[0-9])|(2[0-8]))))))?$";
    /**
     * HH:mm:ss
     */
    private static final String REGEX_TIME = "((((0?[0-9])|([1][0-9])|([2][0-4]))\\:([0-5]?[0-9])" +
            "((\\s)|(\\:([0-5]?[0-9])))))?$";

    /**
     * 判断是否是数字
     *
     * @param num 字符串
     * @return 是否是数字
     */
    public static boolean isNum1Max(String num) {
        if (StringUtils.isBlank(num)) {
            return false;
        }
        return num.matches(NUMBER_1_MAX);
    }

    /**
     * 判断是否是数字
     *
     * @param num 字符串
     * @return 是否是数字
     */
    public static boolean isNum(String num) {
        if (StringUtils.isBlank(num)) {
            return false;
        }
        return num.matches(REGEX_REAL_NUMBER);
    }

    /**
     * 判断是否是都是数字
     *
     * @param num 字符串列表
     * @return 是否是数字
     */
    public static boolean isNum(List<String> num) {
        if (CollUtil.isEmpty(num)) {
            return false;
        }
        for (String str : num) {
            if (!isNum(str)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否是日期类型
     *
     * @param date 字符串
     * @return 是否是日期
     */
    public static boolean isDate(String date) {
        if (StringUtils.isBlank(date)) {
            return false;
        }
        return date.matches(REGEX_DATE_TIME);
    }

    /**
     * 判断是否是都是日期
     *
     * @param date 字符串列表
     * @return 是否是日期
     */
    public static boolean isDate(List<String> date) {
        if (CollUtil.isEmpty(date)) {
            return false;
        }
        for (String str : date) {
            if (!isDate(str)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断一串字符是否为手机号
     *
     * @param mobile 需要判断的手机号
     * @return
     */
    public static boolean isMobile(String mobile) {
        if (StringUtils.isBlank(mobile)) {
            return false;
        }
        return Pattern.compile(MOBILE).matcher(mobile).matches();
    }

    /**
     * 判断一串字符是否为数字串
     *
     * @param number 需要判断的手机号
     * @return
     */
    public static boolean isNumber(String number) {
        if (StringUtils.isBlank(number)) {
            return false;
        }
        return Pattern.compile(NUMBER).matcher(number).matches();
    }

    /**
     * 判断是不是md 语法的图片
     *
     * @param msg
     * @return
     */
    public static boolean isMdImg(String msg) {
        if (StringUtils.isBlank(msg)) {
            return false;
        }
        return Pattern.compile(MD_IMG).matcher(msg).matches();
    }

    /**
     * 判断是不是md 语法的图片
     *
     * @param cmd
     * @return
     */
    public static boolean isOrderCase(String cmd) {
        if (StringUtils.isBlank(cmd)) {
            return false;
        }
        return Pattern.compile(USER_SET).matcher(cmd).matches();
    }

    public static void main(String[] args) {
        for (int i = 0; i < 32; i++) {
            System.out.println((i + 1) * (i + 1) * 128);
        }
    }
}
