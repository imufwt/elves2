package online.elves.utils;

import org.apache.logging.log4j.util.Strings;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 */
public class StrUtils {
    
    /**
     * An empty immutable <code>String</code> array.
     */
    public static final String[] EMPTY_STRING_ARRAY = new String[0];
    
    private static final Pattern PATTERN_CIDR = Pattern.compile("^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})/(\\d{1,2})$");
    
    private static final String QUOT = "&quot;";
    
    private static final String AMP = "&amp;";
    
    private static final String APOS = "&apos;";
    
    private static final String GT = "&gt;";
    
    private static final String LT = "&lt;";
    
    /**
     * 无参构造
     */
    private StrUtils() {
    }
    
    /**
     * 检查指定的字符串是否为空。
     * <ul>
     * <li>SysUtils.isEmpty(null) = true</li>
     * <li>SysUtils.isEmpty("") = true</li>
     * <li>SysUtils.isEmpty("   ") = true</li>
     * <li>SysUtils.isEmpty("abc") = false</li>
     * </ul>
     * @param value 待检查的字符串
     * @return true/false
     */
    public static boolean isEmpty(String value) {
        int strLen;
        if (value == null || (strLen = value.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(value.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 检查对象是否为数字型字符串,包含负数开头的。
     */
    public static boolean isNumeric(Object obj) {
        if (obj == null) {
            return false;
        }
        char[] chars = obj.toString().toCharArray();
        int length = chars.length;
        if (length < 1) {
            return false;
        }
        
        int i = 0;
        if (length > 1 && chars[0] == '-') {
            i = 1;
        }
        
        for (; i < length; i++) {
            if (!Character.isDigit(chars[i])) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 检查指定的字符串列表是否不为空。
     */
    public static boolean areNotEmpty(String... values) {
        boolean result = true;
        if (values == null || values.length == 0) {
            result = false;
        } else {
            for (String value : values) {
                result &= !isEmpty(value);
            }
        }
        return result;
    }
    
    /**
     * 把通用字符编码的字符串转化为汉字编码。
     */
    public static String unicodeToChinese(String unicode) {
        StringBuilder out = new StringBuilder();
        if (!isEmpty(unicode)) {
            for (int i = 0; i < unicode.length(); i++) {
                out.append(unicode.charAt(i));
            }
        }
        return out.toString();
    }
    
    /**
     * 把名称转换为小写加下划线的形式。
     */
    public static String toUnderlineStyle(String name) {
        StringBuilder newName = new StringBuilder();
        int len = name.length();
        for (int i = 0; i < len; i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    newName.append("_");
                }
                newName.append(Character.toLowerCase(c));
            } else {
                newName.append(c);
            }
        }
        return newName.toString();
    }
    
    /**
     * 把名称转换为首字母小写的驼峰形式。
     */
    public static String toCamelStyle(String name) {
        StringBuilder newName = new StringBuilder();
        int len = name.length();
        for (int i = 0; i < len; i++) {
            char c = name.charAt(i);
            if (i == 0) {
                newName.append(Character.toLowerCase(c));
            } else {
                newName.append(c);
            }
        }
        return newName.toString();
    }
    
    /**
     * XML字符转义包括(<,>,',&,")五个字符.
     * @param value 所需转义的字符串
     * @return 转义后的字符串 @
     */
    public static String escapeXml(String value) {
        StringBuilder writer = new StringBuilder();
        char[] chars = value.trim().toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            switch (c) {
                case '<':
                    writer.append(LT);
                    break;
                case '>':
                    writer.append(GT);
                    break;
                case '\'':
                    writer.append(APOS);
                    break;
                case '&':
                    writer.append(AMP);
                    break;
                case '\"':
                    writer.append(QUOT);
                    break;
                default:
                    if ((c == 0x9) || (c == 0xA) || (c == 0xD) || ((c >= 0x20) && (c <= 0xD7FF))
                            || ((c >= 0xE000) && (c <= 0xFFFD)) || ((c >= 0x10000) && (c <= 0x10FFFF))) {
                        writer.append(c);
                    }
            }
        }
        return writer.toString();
    }
    
    /**
     * 获取类的get/set属性名称集合。
     * @param clazz 类
     * @param isGet 是否获取读方法，true为读方法，false为写方法
     * @return 属性名称集合
     */
    public static Set<String> getClassProperties(Class<?> clazz, boolean isGet) {
        Set<String> propNames = new HashSet<String>();
        try {
            if (clazz == null) {
                return propNames;
            }
            BeanInfo info = Introspector.getBeanInfo(clazz);
            PropertyDescriptor[] props = info.getPropertyDescriptors();
            for (PropertyDescriptor prop : props) {
                String name = prop.getName();
                Method method;
                if (isGet) {
                    method = prop.getReadMethod();
                } else {
                    method = prop.getWriteMethod();
                }
                if (!"class".equals(name) && method != null) {
                    propNames.add(name);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return propNames;
    }
    
    //-----------------------------------------------------------------------
    
    /**
     * <p>Checks whether the <code>String</code> contains only
     * digit characters.</p>
     *
     * <p><code>Null</code> and empty String will return
     * <code>false</code>.</p>
     * @param str the <code>String</code> to check
     * @return <code>true</code> if str contains only unicode numeric
     */
    public static boolean isDigits(String str) {
        if (isEmpty(str)) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * <p>Splits the provided text into an array, separator specified.
     * This is an alternative to using StringTokenizer.</p>
     *
     * <p>The separator is not included in the returned String array.
     * Adjacent separators are treated as one separator.
     * For more control over the split use the StrTokenizer class.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.</p>
     *
     * <pre>
     * StringUtils.split(null, *)         = null
     * StringUtils.split("", *)           = []
     * StringUtils.split("a.b.c", '.')    = ["a", "b", "c"]
     * StringUtils.split("a..b.c", '.')   = ["a", "b", "c"]
     * StringUtils.split("a:b:c", '.')    = ["a:b:c"]
     * StringUtils.split("a b c", ' ')    = ["a", "b", "c"]
     * </pre>
     * @param str           the String to parse, may be null
     * @param separatorChar the character used as the delimiter
     * @return an array of parsed Strings, <code>null</code> if null String input
     * @since 2.0
     */
    public static String[] split(String str, char separatorChar) {
        return splitWorker(str, separatorChar, false);
    }
    
    /**
     * Performs the logic for the <code>split</code> and
     * <code>splitPreserveAllTokens</code> methods that do not return a
     * maximum array length.
     * @param str               the String to parse, may be <code>null</code>
     * @param separatorChar     the separate character
     * @param preserveAllTokens if <code>true</code>, adjacent separators are
     *                          treated as empty token separators; if <code>false</code>, adjacent
     *                          separators are treated as one separator.
     * @return an array of parsed Strings, <code>null</code> if null String input
     */
    private static String[] splitWorker(String str, char separatorChar, boolean preserveAllTokens) {
        // Performance tuned for 2.0 (JDK1.4)
        
        if (str == null) {
            return null;
        }
        int len = str.length();
        if (len == 0) {
            return EMPTY_STRING_ARRAY;
        }
        List<String> list = new ArrayList<String>();
        int i = 0, start = 0;
        boolean match = false;
        boolean lastMatch = false;
        while (i < len) {
            if (str.charAt(i) == separatorChar) {
                if (match || preserveAllTokens) {
                    list.add(str.substring(start, i));
                    match = false;
                    lastMatch = true;
                }
                start = ++i;
                continue;
            }
            lastMatch = false;
            match = true;
            i++;
        }
        if (match || (preserveAllTokens && lastMatch)) {
            list.add(str.substring(start, i));
        }
        return (String[]) list.toArray(new String[list.size()]);
    }
    
    /**
     * Performs the logic for the <code>split</code> and
     * <code>splitPreserveAllTokens</code> methods that return a maximum array
     * length.
     * @param str               the String to parse, may be <code>null</code>
     * @param separatorChars    the separate character
     * @param max               the maximum number of elements to include in the
     *                          array. A zero or negative value implies no limit.
     * @param preserveAllTokens if <code>true</code>, adjacent separators are
     *                          treated as empty token separators; if <code>false</code>, adjacent
     *                          separators are treated as one separator.
     * @return an array of parsed Strings, <code>null</code> if null String input
     */
    private static String[] splitWorker(String str, String separatorChars, int max, boolean preserveAllTokens) {
        // Performance tuned for 2.0 (JDK1.4)
        // Direct code is quicker than StringTokenizer.
        // Also, StringTokenizer uses isSpace() not isWhitespace()
        
        if (str == null) {
            return null;
        }
        int len = str.length();
        if (len == 0) {
            return EMPTY_STRING_ARRAY;
        }
        List<String> list = new ArrayList<String>();
        int sizePlus1 = 1;
        int i = 0, start = 0;
        boolean match = false;
        boolean lastMatch = false;
        if (separatorChars == null) {
            // Null separator means use whitespace
            while (i < len) {
                if (Character.isWhitespace(str.charAt(i))) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        } else if (separatorChars.length() == 1) {
            // Optimise 1 character case
            char sep = separatorChars.charAt(0);
            while (i < len) {
                if (str.charAt(i) == sep) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        } else {
            // standard case
            while (i < len) {
                if (separatorChars.indexOf(str.charAt(i)) >= 0) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        }
        if (match || (preserveAllTokens && lastMatch)) {
            list.add(str.substring(start, i));
        }
        return (String[]) list.toArray(new String[list.size()]);
    }
    
    /**
     * <p>Splits the provided text into an array, separators specified.
     * This is an alternative to using StringTokenizer.</p>
     *
     * <p>The separator is not included in the returned String array.
     * Adjacent separators are treated as one separator.
     * For more control over the split use the StrTokenizer class.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.
     * A <code>null</code> separatorChars splits on whitespace.</p>
     *
     * <pre>
     * StringUtils.split(null, *)         = null
     * StringUtils.split("", *)           = []
     * StringUtils.split("abc def", null) = ["abc", "def"]
     * StringUtils.split("abc def", " ")  = ["abc", "def"]
     * StringUtils.split("abc  def", " ") = ["abc", "def"]
     * StringUtils.split("ab:cd:ef", ":") = ["ab", "cd", "ef"]
     * </pre>
     * @param str            the String to parse, may be null
     * @param separatorChars the characters used as the delimiters,
     *                       <code>null</code> splits on whitespace
     * @return an array of parsed Strings, <code>null</code> if null String input
     */
    public static String[] split(String str, String separatorChars) {
        return splitWorker(str, separatorChars, -1, false);
    }
    
    /**
     * 判断指定的IP地址是否在IP段里面。
     * @param ipAddr   IP地址
     * @param cidrAddr 用CIDR表示法的IP段信息
     * @return true/false
     */
    public static boolean isIpInRange(String ipAddr, String cidrAddr) {
        Matcher matcher = PATTERN_CIDR.matcher(cidrAddr);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid CIDR address: " + cidrAddr);
        }
        
        int[] minIpParts = new int[4];
        int[] maxIpParts = new int[4];
        String[] ipParts = matcher.group(1).split("\\.");
        int intMask = Integer.parseInt(matcher.group(2));
        
        for (int i = 0; i < ipParts.length; i++) {
            int ipPart = Integer.parseInt(ipParts[i]);
            if (intMask >= 8) {
                minIpParts[i] = ipPart;
                maxIpParts[i] = ipPart;
                intMask -= 8;
            } else if (intMask > 0) {
                minIpParts[i] = ipPart >> intMask;
                maxIpParts[i] = ipPart | (0xFF >> intMask);
                intMask = 0;
            } else {
                minIpParts[i] = 1;
                maxIpParts[i] = 0xFF - 1;
            }
        }
        
        String[] realIpParts = ipAddr.split("\\.");
        for (int i = 0; i < realIpParts.length; i++) {
            int realIp = Integer.parseInt(realIpParts[i]);
            if (realIp < minIpParts[i] || realIp > maxIpParts[i]) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 将列表中的对象连接成字符串
     * @param objs
     * @param sep
     * @return
     */
    public static String join(Iterable<?> objs, String sep) {
        StringBuilder buf = new StringBuilder();
        
        join(buf, objs, sep);
        
        return buf.toString();
    }
    
    /**
     * 将列表中的对象连接起来。
     */
    public static void join(StringBuilder buf, Iterable<?> objs, String sep) {
        try {
            join((Appendable) buf, objs, sep);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 将列表中的对象连接起来。
     */
    public static void join(Appendable buf, Iterable<?> objs, String sep) throws IOException {
        if (objs == null) {
            return;
        }
        
        if (sep == null) {
            sep = "";
        }
        
        for (Iterator<?> i = objs.iterator(); i.hasNext(); ) {
            buf.append(String.valueOf(i.next()));
            
            if (i.hasNext()) {
                buf.append(sep);
            }
        }
    }
    
    /**
     * 生成滑动窗口
     * @param input
     * @param slideSize
     * @return
     */
    public static List<String> getSlideWindows(String input, int slideSize) {
        List<String> windows = new ArrayList<String>();
        int startIndex = 0;
        int endIndex = 0;
        int currentWindowSize = 0;
        String currentWindow = null;
        
        while (endIndex < input.length() || currentWindowSize > slideSize) {
            boolean startsWithLetterOrDigit;
            if (currentWindow == null) {
                startsWithLetterOrDigit = false;
            } else {
                startsWithLetterOrDigit = isLetterOrDigit(currentWindow.charAt(0));
            }
            
            if (endIndex == input.length() && !startsWithLetterOrDigit) {
                break;
            }
            
            if (currentWindowSize == slideSize && !startsWithLetterOrDigit && isLetterOrDigit(input.charAt(endIndex))) {
                endIndex++;
                currentWindow = input.substring(startIndex, endIndex);
                currentWindowSize = 5;
                
            } else {
                if (endIndex != 0) {
                    if (startsWithLetterOrDigit) {
                        currentWindowSize -= 1;
                    } else {
                        currentWindowSize -= 2;
                    }
                    startIndex++;
                }
                
                while (currentWindowSize < slideSize && endIndex < input.length()) {
                    char currentChar = input.charAt(endIndex);
                    if (isLetterOrDigit(currentChar)) {
                        currentWindowSize += 1;
                    } else {
                        currentWindowSize += 2;
                    }
                    endIndex++;
                }
                currentWindow = input.substring(startIndex, endIndex);
                
            }
            windows.add(currentWindow);
        }
        return windows;
    }
    
    /**
     * 判断字母还是数字
     * @param x
     * @return
     */
    private static boolean isLetterOrDigit(char x) {
        if (0 <= x && x <= 127) {
            return true;
        }
        return false;
    }
    
    /**
     * 获取缓存可以
     * @param keyPrefix
     * @param params
     * @return
     */
    public static String getKey(String keyPrefix, String... params) {
        return keyPrefix + Strings.join(Arrays.asList(params), ':');
    }
    
    /**
     * 字符串乱序
     * @param original
     * @return
     */
    public static String shuffle(String original){
        char[] arr=original.toCharArray();
        Random rnd=new Random();
        char tmp;
        int j;
        
        for(int i=arr.length;i>1;i--){
            j=rnd.nextInt(i);
            tmp=arr[i-1];
            arr[i-1]=arr[j];
            arr[j]=tmp;
        }
        
        return String.valueOf(arr);
    }
    public static void main(String[] args) {
        System.out.println(shuffle("你是我的小苹果, 逍遥小苹果"));
    }
}
