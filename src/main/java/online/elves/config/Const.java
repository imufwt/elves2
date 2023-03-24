package online.elves.config;

import com.google.common.collect.Lists;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

/**
 * 常用配置信息
 */
public class Const {
    
    /**
     * 老子是天道
     */
    public static String ADMIN = "ADMIN";
    
    /**
     * 机器人列表  小智 小冰 精灵
     */
    public static List<Integer> ROBOT_LIST = Arrays.asList(4076, 8888, 9036);
    
    /**
     * 小精灵
     */
    public static String ELVES_MAME = "ELVES_MAME";
    
    /**
     * 小精灵的密码
     */
    public static String ELVES_SECRET = "ELVES_SECRET";
    
    /**
     * 免费 api key
     * https://www.mxnzp.com/doc/list
     */
    public static String MXN_API_KEY = "";
    
    /**
     * 免费 api secret
     * https://www.mxnzp.com/doc/list
     */
    public static String MXN_API_SECRET = "";
    
    /**
     * 彩云天气 API
     */
    public static String CAI_YUN_API = "";
    
    /**
     * 星座名称
     */
    public static String CONSTELLATION_NAMES = "水瓶座,双鱼座,白羊座,金牛座,双子座,巨蟹座,狮子座,处女座,天秤座,天蝎座,射手座,摩羯座";
    
    /**
     * 等级名称
     */
    public static List<String> CHAT_ROOM_LEVEL_NAME = Lists.newArrayList("炼气(人)", "筑基(人)", "金丹(人)", "元婴(人)", "化神(人)", "合体(人)", "大乘(人)", "渡劫(人)", "天仙(仙)", "罗天上仙(仙)", "大罗金仙(仙)", "九天玄仙(仙)", "仙君(仙)", "仙帝(仙)", "人神(神)", "天神(神)", "神王(神)", "天尊(神)", "鸿蒙(神)");
    
    /**
     * 宵禁解除 早上八点
     */
    public static LocalTime start = LocalTime.of(8, 0, 0);
    
    /**
     * 宵禁开始 晚上七点半
     */
    public static LocalTime end = LocalTime.of(19, 30, 0);
    
    /**
     * 礼物领取状态
     */
    public static String AWARD_STATUS = "fish:award:status";
    
    /**
     * 上次礼物领取时间
     */
    public static String LAST_CHECK = "fish:last:check:time";
    
    /**
     * 最后一次发言
     */
    public static String LAST_HALO_PREFIX = "fish:last:halo:";
    
    /**
     * 最后一个会员报道文章
     */
    public static String LAST_NEW_MEM_ARTICLE = "fish:last:article:mem:new";
    
    /**
     * 临时广告内容
     */
    public static String TEMPORARY_CONTENT = "fish:temporary:content";
    
    
    /**
     * 欢迎的最后一个用户
     */
    public static String WELCOME_LAST = "fish:user:welcom:last";
    
    /**
     * 私聊欢迎的最后一个用户
     */
    public static String CHAT_WELCOME_LAST = "fish:user:chat:welcom:last";
    
    /**
     * 活跃度
     */
    public static String USER_ACTIVITY = "fish:activity:";
    
    /**
     * 活跃度时间间隔
     */
    public static String USER_ACTIVITY_LIMIT = "fish:activity:limit:";
    
    /**
     * 某地天气缓存前缀
     */
    public static String WEATHER_PREFIX = "fish:weather:city:";
    
    /**
     * 星座前缀
     */
    public static String CONSTELLATION_PREFIX = "fish:constellation:";
    
    /**
     * 程序员老黄历前缀
     */
    public static String ALMANAC_CODER_PREFIX = "fish:almanac:coder:";
    
    /**
     * 排行前缀
     */
    public static String RANKING_PREFIX = "fish:rank:";
    
    /**
     * 年榜
     */
    public static String RANKING_YEAR_PREFIX = "fish:rank:year:";
    
    /**
     * 月榜
     */
    public static String RANKING_MONTH_PREFIX = "fish:rank:month:";
    
    /**
     * 日榜
     */
    public static String RANKING_DAY_PREFIX = "fish:rank:day:";
    
    /**
     * 周榜
     */
    public static String RANKING_WEEK_PREFIX = "fish:rank:week:";
    
    /**
     * 笑话列表, 缓存
     */
    public static String JOKE_LIST = "fish:joke:list";
    
    /**
     * 神秘代码换了时光
     */
    public static String MYSTERY_CODE_HAPPY_TIME = "fish:mystery:happy:time";
    
    /**
     * 沾沾卡
     */
    public static String MYSTERY_CODE_ZZK_TIME = "fish:mystery:zzk:time";
    
    /**
     * 神秘代码个数前缀
     */
    public static String MYSTERY_CODE_TIMES_PREFIX = "fish:mystery:times:";
    
    /**
     * 神秘代码免费前缀
     */
    public static String MYSTERY_CODE_FREE_PREFIX = "fish:mystery:free:";
    
}
