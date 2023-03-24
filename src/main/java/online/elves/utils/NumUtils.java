package online.elves.utils;

/**
 * 字符串工具类
 */
public class NumUtils {
    
    /**
     * 是否在某个区间 左闭右开 [x,y)
     * @param value
     * @param start
     * @param end
     * @param <T>
     * @return
     */
    public static <T extends Comparable<T>> boolean isBetween(T value, T start, T end) {
        return value.compareTo(start) >= 0 && value.compareTo(end) < 0;
    }
    
}
