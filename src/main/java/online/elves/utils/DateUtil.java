package online.elves.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import online.elves.config.Const;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * 时间日期 工具类
 */
@Slf4j
public class DateUtil {
    /**
     * 中国时区 东八区
     */
    public static final String CHINA_ZONE = "GMT+8";
    /**
     * 年月日
     */
    public static final String DAY = "yyyy-MM-dd";
    /**
     * 简单年月日
     */
    public static final String DAY_SIMPLE = "yyyyMMdd";
    /**
     * 年月
     */
    public static final String YEAR_MONTH = "yyyy-MM";
    /**
     * 年月日
     */
    public static final String DAY_TIME = "yyyy-MM-dd HH:mm:ss";
    /**
     * 年月日 时分秒 毫秒
     */
    public static final String DAY_TIME_FULL = "yyyy-MM-dd HH:mm:ss.SSS";
    /**
     * CST 年月日
     */
    public static final String CST_DAY_TIME = "EEE MMM dd HH:mm:ss zzz yyyy";
    /**
     * 天进制
     */
    public static final Double DAY_HOURS = 24d;
    /**
     * 分钟小时进制
     */
    public static final Double AN_HOUR_WINDERS = 60d;
    /**
     * 星期数组 中文
     */
    private static final String[] WEEK_NAME_CN = {"一", "二", "三", "四", "五", "六", "日"};
    /**
     * 星期数组 英文
     */
    private static final String[] WEEK_NAME_EN = {"Mon.", "Tues.", "Wed.", "Thurs.", "Fri.", "Sat.", "Sun."};

    /**
     * 本地线程池
     */
    private static final ThreadLocal<SimpleDateFormat> SDF = ThreadLocal.withInitial(() -> new SimpleDateFormat(DAY_TIME));

    /**
     * 私有构造
     */
    private DateUtil() {

    }

    /**
     * 获取日期的年月日
     * 抹掉 时分秒
     *
     * @param date 需要转换的日期
     * @return 转换后的日期
     */
    public static Date getDay(Date date) {
        return ld2UDate(ud2LDate(date));
    }

    /**
     * date 转 localDate
     *
     * @param date 需要转换的日期
     * @return 转换后的日期
     */
    public static LocalDate ud2LDate(Date date) {
        if (Objects.isNull(date)) {
            return null;
        }
        return ud2LdTime(date).toLocalDate();
    }

    /**
     * date 转 localDateTime
     *
     * @param date 需要转换的时间日期
     * @return 转换后的时间日期
     */
    public static LocalDateTime ud2LdTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    /**
     * uDate转换为localDateTime
     * 抹掉秒
     *
     * @param date 需要转换的时间日期
     * @return 转换后的时间日期
     */
    public static LocalDateTime ud2LdTimeExactHour(Date date) {
        return ud2LdTime(date).toLocalDate().atStartOfDay();
    }

    /**
     * localDate 转 date
     *
     * @param localDate 需要转换的日期
     * @return 转换后的日期
     */
    public static Date ld2UDate(LocalDate localDate, String zoneId) {
        if (Objects.isNull(localDate)) {
            return null;
        }
        // 时区
        ZoneId zone;
        if (StringUtils.isBlank(zoneId)) {
            zone = ZoneId.systemDefault();
        } else {
            zone = ZoneId.of(zoneId);
        }
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        return Date.from(instant);
    }

    /**
     * localDate 转 date
     *
     * @param localDate 需要转换的日期
     * @return 转换后的日期
     */
    public static Date ld2UDate(LocalDate localDate) {
        return ld2UDate(localDate, CHINA_ZONE);
    }

    /**
     * localDateTime 转 date
     *
     * @param localDateTime 需要转换的时间日期
     * @return 转换后的时间日期
     */
    public static Date ldtToUDate(LocalDateTime localDateTime, String zoneId) {
        if (Objects.isNull(localDateTime)) {
            return null;
        }
        // 时区
        ZoneId zone;
        if (StringUtils.isBlank(zoneId)) {
            zone = ZoneId.systemDefault();
        } else {
            zone = ZoneId.of(zoneId);
        }
        Instant instant = localDateTime.atZone(zone).toInstant();
        return Date.from(instant);
    }

    /**
     * localDateTime 转 date
     *
     * @param localDateTime 需要转换的时间日期
     * @return 转换后的时间日期
     */
    public static Date ldtToUDate(LocalDateTime localDateTime) {
        return ldtToUDate(localDateTime, CHINA_ZONE);
    }

    /**
     * 时间格式化为String， 传入Date & Pattern
     *
     * @param time    Date型
     * @param pattern 时间格式 如：yyyy-MM-dd HH:mm:ss
     * @return 转换后的类型
     */
    public static String format(Date time, String pattern) {
        SDF.get().applyPattern(pattern);
        return SDF.get().format(time);
    }

    /**
     * 时间格式化为String， 传入字符串类型Date & Pattern
     *
     * @param time   String型
     * @param format 时间格式 如：yyyy-MM-dd HH:mm:ss
     * @return 转换后的类型
     */
    public static String format(String time, String format) {
        SDF.get().applyPattern(format);
        try {
            Date date = SDF.get().parse(time);
            return SDF.get().format(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException("日期格式化错误：传入日期参数【" + time + "】，格式化参数【" + format + "】... " + e.getMessage());
        }
    }

    /**
     * 时间格式化为String， 传入毫秒
     *
     * @param time   long型
     * @param format 时间格式 如：yyyy-MM-dd HH:mm:ss
     * @return 格式化后的日期
     */
    public static String format(long time, String format) {
        if (Objects.isNull(time)) {
            return null;
        }
        SDF.get().applyPattern(format);
        return SDF.get().format(time);
    }

    /**
     * 时间格式化为date， 传入字符串类型Date & Pattern
     *
     * @param time   String型
     * @param format 时间格式 如：yyyy-MM-dd HH:mm:ss
     * @return 转换后的类型
     */
    public static Date parse(String time, String format) {
        SDF.get().applyPattern(format);
        try {
            return SDF.get().parse(time);
        } catch (ParseException e) {
            throw new IllegalArgumentException("日期格式化错误：传入日期参数【" + time + "】，格式化参数【" + format + "】... " + e.getMessage());
        }
    }

    /**
     * 时间格式化为String， 传入毫秒
     *
     * @param time long型
     * @return 格式化后的日期
     */
    public static String unixFormat(long time) {
        return format(time, DAY_TIME);
    }

    /**
     * 时间格式化为Date， 传入毫秒
     *
     * @param time long型
     * @return 格式化后的日期
     */
    public static Date unixParse(long time) {
        return parse(unixFormat(time), DAY_TIME);
    }

    /**
     * 格式化为 yyyy-MM-dd 格式 String
     *
     * @param time 需要格式化的时间
     * @return 格式化后的日期
     */
    public static String formatDay(Date time) {
        if (Objects.isNull(time)) {
            return null;
        }
        return format(time, DAY);
    }

    /**
     * 格式化为 yyyy-MM-dd HH:mm:ss格式 String
     *
     * @param time 需要格式化的时间
     * @return 格式化后的日期
     */
    public static String formatTime(Date time) {
        if (Objects.isNull(time)) {
            return null;
        }
        return format(time, DAY_TIME);
    }

    /**
     * 格式化为 yyyy-MM-dd 格式 date
     *
     * @param date 需要格式化的日期
     * @return 格式化后的日期
     */
    public static Date parseDate(String date) {
        if (StringUtils.isBlank(date)) {
            return null;
        }
        return parse(date, DAY);
    }

    /**
     * 获取date之后day天的日期 string
     *
     * @param date 计算开始日期
     * @param day  间隔天数
     * @return 计算后的日期
     */
    public static String getNextDate(String date, long day) {
        Date d = parseDate(date);
        LocalDate localDate = ud2LDate(d);
        if (Objects.isNull(localDate)) {
            return formatDay(d);
        }
        return formatDay(ld2UDate(localDate.plusDays(day)));
    }

    /**
     * 获取date之后day天的日期 date
     *
     * @param date 计算开始日期
     * @param day  间隔天数
     * @return 计算后的日期
     */
    public static Date getNextDate(Date date, long day) {
        LocalDate localDate = ud2LDate(date);
        if (Objects.isNull(localDate)) {
            return date;
        }
        return ld2UDate(localDate.plusDays(day));
    }

    /**
     * 收集起始时间到结束时间之间所有的时间并以字符串集合方式返回
     *
     * @param timeStart 开始时间 String
     * @param timeEnd   结束时间 String
     * @return 时间集合
     */
    public static List<String> collectLocalDates(String timeStart, String timeEnd) {
        return collectLocalDates(LocalDate.parse(timeStart), LocalDate.parse(timeEnd));
    }

    /**
     * 收集起始时间到结束时间之间所有的时间并以字符串集合方式返回
     *
     * @param timeStart 开始时间 date
     * @param timeEnd   结束时间 date
     * @return 时间集合
     */
    public static List<String> collectLocalDates(Date timeStart, Date timeEnd) {
        return collectLocalDates(ud2LDate(timeStart), ud2LDate(timeEnd));
    }

    /**
     * 收集起始时间到结束时间之间所有的时间并以字符串集合方式返回
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 时间结合
     */
    public static List<String> collectLocalDates(LocalDate start, LocalDate end) {
        // 用起始时间作为流的源头，按照每次加一天的方式创建一个无限流
        return Stream.iterate(start, localDate -> localDate.plusDays(1))
                // 截断无限流，长度为起始时间和结束时间的差+1个
                .limit(ChronoUnit.DAYS.between(start, end) + 1)
                // 由于最后要的是字符串，所以map转换一下
                .map(x -> formatDay(ld2UDate(x)))
                // 把流收集为List
                .collect(toList());
    }

    /**
     * 收集起始时间到结束时间之间所有的整点间隔
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 时间集合
     */
    public static List<LocalDateTime> collectHours(LocalDateTime start, LocalDateTime end) {
        // 用起始时间作为流的源头，按照每次加一个小时的方式创建一个无限流
        return Stream.iterate(start, ldt -> ldt.plusHours(1))
                // 截断无限流，长度为起始时间和结束时间的差+1个
                .limit(ChronoUnit.HOURS.between(start, end) + 1)
                // 把流收集为List
                .collect(toList());
    }

    public static void main(String[] args) {
        //log.info(Arrays.asList("null|8888|adlered|admin".split("\\|")).toString());
        String md = "凌 广告 鱼翅支持[1](123123123)发麻花纹(http://foshp.cn)🈶详询\n   \n 测试换行\n###引用\n\n";
        String s = md.replaceAll(" + ", " ").split("\\n")[0];
        log.info(JSON.toJSONString(s.split(" ")));
        log.info(JSON.toJSONString(s));
    }

    /**
     * 获取本周一的日期
     *
     * @return 周一 的字符串日期
     */
    public static String getCurrentMonday() {
        return LocalDate.now().with(DayOfWeek.MONDAY).toString();
    }

    /**
     * 获取本周日的日期
     *
     * @return 周日 的字符串日期
     */
    public static String getCurrentSunday() {
        return LocalDate.now().with(DayOfWeek.SUNDAY).toString();
    }

    /**
     * 获取本月第一天
     *
     * @return 本月第一天 字符串
     */
    public static String getFirstDayOfMonth() {
        return LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).toString();
    }

    /**
     * 获取本月第一天
     *
     * @return 本月第一天
     */
    public static LocalDate getStartOfMonth() {
        return LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
    }

    /**
     * 获取本月最后一天
     *
     * @return 本月最后一天
     */
    public static LocalDate getEndOfMonth() {
        return LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
    }

    /**
     * 获取本月最后一天
     *
     * @return 本月最后一天 字符串
     */
    public static String getLastDayOfMonth() {
        return LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).toString();
    }

    /**
     * 明天
     *
     * @return 明天的localDate
     */
    public static LocalDate tomorrow() {
        return LocalDate.now().plusDays(1);
    }

    /**
     * 获取日期星期几
     *
     * @param date 日期
     * @return 周几
     */
    public static int getDayForWeek(String date) {
        return getDayForWeek(parseDate(date));
    }

    /**
     * 获取日期星期几
     *
     * @param date 日期
     * @return 周几
     */
    public static int getDayForWeek(Date date) {
        return getDayForWeek(formatDay(date));
    }

    /**
     * 获取日期周几的显示
     *
     * @param date 需要获取的日期
     * @return 显示
     */
    public static String getWeekCNName(String date) {
        return getWeekName(date, Boolean.TRUE);
    }

    /**
     * 获取日期周几的显示
     *
     * @param date  需要获取的日期
     * @param useCN 是否使用中文
     * @return 显示
     */
    public static String getWeekName(String date, boolean useCN) {
        if (useCN) {
            return WEEK_NAME_CN[getDayForWeek(date) - 1];
        } else {
            return WEEK_NAME_EN[getDayForWeek(date) - 1];
        }
    }

    /**
     * long 2 LocalDate
     *
     * @param ld 需要转化的字段
     * @return 转换结果
     */
    public static LocalDate parseLd(long ld) {
        if (Objects.isNull(ld)) {
            return null;
        }
        Date parse = unixParse(ld);
        if (Objects.isNull(parse)) {
            return null;
        }
        return ud2LDate(parse);
    }

    /**
     * long 2 LocalDateTime
     *
     * @param ldt 需要转化的时间
     * @return 转化结果
     */
    public static LocalDateTime parseLdt(long ldt) {
        if (Objects.isNull(ldt)) {
            return null;
        }
        Date parse = unixParse(ldt);
        if (Objects.isNull(parse)) {
            return null;
        }
        return ud2LdTime(parse);
    }

    /**
     * String 2 LocalDate
     *
     * @param ld 需要转化的字段
     * @return 转换结果
     */
    public static LocalDate parseLd(String ld) {
        if (StringUtils.isBlank(ld)) {
            return null;
        }
        Date parse = parse(ld, DAY);
        if (Objects.isNull(parse)) {
            return null;
        }
        return ud2LDate(parse);
    }

    /**
     * String 2 LocalDateTime
     *
     * @param ldt 需要转化的时间
     * @return 转化结果
     */
    public static LocalDateTime parseLdt(String ldt) {
        if (StringUtils.isBlank(ldt)) {
            return null;
        }
        Date parse = parse(ldt, DAY_TIME);
        if (Objects.isNull(parse)) {
            return null;
        }
        return ud2LdTime(parse);
    }

    /**
     * 日期是否介于两个日期
     *
     * @param mid 计算的日期
     * @param min 最小日期
     * @param max 最大日期
     * @return 是否介于
     */
    public static boolean isBetween(String mid, String min, String max) {
        if (StringUtils.isBlank(mid) || StringUtils.isBlank(min) || StringUtils.isBlank(max)) {
            return false;
        }
        LocalDateTime minLdt = adaptStr2LdTime(min), midLdt = adaptStr2LdTime(mid), maxLdt = adaptStr2LdTime(max);
        return !midLdt.isBefore(minLdt) && !midLdt.isAfter(maxLdt);
    }

    /**
     * 时间是否介于两者中间
     *
     * @param mid 比较的时间
     * @param min 最小值
     * @param max 最大值
     * @return
     */
    public static boolean isBetween(LocalDateTime mid, LocalDateTime min, LocalDateTime max) {
        if (Objects.isNull(mid) || Objects.isNull(min) || Objects.isNull(max)) {
            return false;
        }
        return !mid.isBefore(min) && !mid.isAfter(max);
    }

    /**
     * 时间是否介于两者中间
     *
     * @param mid 比较的时间
     * @param min 最小值
     * @param max 最大值
     * @return
     */
    public static boolean isBetween(LocalDate mid, LocalDate min, LocalDate max) {
        if (Objects.isNull(mid) || Objects.isNull(min) || Objects.isNull(max)) {
            return false;
        }
        return !mid.isBefore(min) && !mid.isAfter(max);
    }

    /**
     * 适配 String 转换 ldt
     *
     * @param ldt 字符串日期
     * @return 转换日期
     */
    private static LocalDateTime adaptStr2LdTime(String ldt) {
        if (ldt.length() > 10) {
            return Optional.ofNullable(parseLdt(ldt)).orElse(LocalDateTime.now());
        } else {
            return Optional.ofNullable(parseLd(ldt)).orElse(LocalDate.now()).atStartOfDay();
        }
    }

    /**
     * LocalDate 转 String
     *
     * @param ld 需要转换的日期
     * @return 转换结果
     */
    public static String formatDay(LocalDate ld) {
        Date date = ld2UDate(ld);
        if (Objects.isNull(date)) {
            return null;
        }
        return formatDay(date);
    }

    /**
     * 格式化ldt
     *
     * @param ldt 需要格式化的日期
     * @return 格式化后的日期
     */
    public static String formatDay(LocalDateTime ldt) {
        Date date = ldtToUDate(ldt);
        if (Objects.isNull(date)) {
            return null;
        }
        return format(date, DAY_TIME);
    }

    /**
     * 获取当前时间字符串
     *
     * @return 当前时间字符串
     */
    public static String nowStr() {
        return formatDay(LocalDateTime.now());
    }

    /**
     * 最大日期
     *
     * @return 2099-12-31
     */
    public static LocalDate maxDate() {
        return LocalDate.of(2099, 12, 31);
    }

    /**
     * 最大时间
     *
     * @return 最大时间
     */
    public static LocalDateTime maxTime() {
        return LocalDateTime.of(2099, 12, 31, 23, 59, 59);
    }

    /**
     * 安全转换月日到指定年, 处理闰年 2月29
     *
     * @param year     获取指定年份
     * @param monthDay 获取月日
     * @return 返回结果
     */
    public static LocalDate safeComposeDate(LocalDate year, LocalDate monthDay) {
        if (Objects.isNull(year) || Objects.isNull(monthDay)) {
            return null;
        }
        // 年
        int yearYear = year.getYear();
        // 月
        int monthValue = monthDay.getMonthValue();
        // 日
        int dayOfMonth = monthDay.getDayOfMonth();
        // 不是闰年
        if (!year.isLeapYear()) {
            if (monthValue == 2 && dayOfMonth == 29) {
                return LocalDate.of(yearYear, monthValue, dayOfMonth - 1);
            }
        }
        return LocalDate.of(yearYear, monthValue, dayOfMonth);
    }

    /**
     * 计算时间间隔
     *
     * @param start
     * @param end
     * @param unit
     * @return
     */
    public static Long getInterval(LocalDateTime start, LocalDateTime end, ChronoUnit unit) {
        return unit.between(start, end);
    }

    /**
     * 获取年纪描述
     *
     * @param birthday
     * @return
     */
    public static String getAgeDesc(LocalDate birthday) {
        Period between = Period.between(birthday, LocalDate.now());
        return between.getYears() + "岁 " + between.getMonths() + "个月 " + between.getDays() + "天";
    }

    /**
     * 转化分钟为年月日
     *
     * @param onlineMinute
     * @return
     */
    public static String transferMinutes(Integer onlineMinute) {
        if (onlineMinute < 1) {
            return "0 分钟";
        }
        int day = onlineMinute / (24 * 60);
        int hour = (onlineMinute % (24 * 60)) / 60;
        int minute = (onlineMinute % (24 * 60)) % 60;
        return day + " 天 " + hour + " 小时 " + minute + " 分钟";
    }

    /**
     * 获取鱼历
     *
     * @return
     */
    public static String getFishDay() {
        // 当前时间
        LocalTime now = LocalTime.now();
        // 当前年月日
        LocalDate localDate = LocalDate.now();
        // 组合后的鱼历
        localDate = LocalDate.of(localDate.getYear() - 2020, localDate.getMonth(), localDate.getDayOfMonth());
        return formatDay(LocalDateTime.of(localDate, now));
    }

    /**
     * 猜拳限制
     *
     * @return
     */
    public static boolean isRpsLock() {
        // 当前时间
        LocalDateTime now = LocalDateTime.now();
        // 今天
        LocalDate ld = now.toLocalDate();
        // 8:30 - 11:30
        if (isBetween(now, LocalDateTime.of(ld, Const.eight30), LocalDateTime.of(ld, Const.eleven30))) {
            return true;
        }
        // 13:30 - 18:00
        if (isBetween(now, LocalDateTime.of(ld, Const.thirteen30), LocalDateTime.of(ld, Const.eighteen))) {
            return true;
        }
        return false;
    }
}
