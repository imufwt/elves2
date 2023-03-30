package online.elves.config;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

/**
 * 常用配置信息
 */
@Component
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
    
    /* ..................账号密码系列.............开始了............... */
    @Value("${third-api.mxn.key")
    private String mxnKey;
    
    /**
     * 免费 api key
     * https://www.mxnzp.com/doc/list
     */
    public static String MXN_API_KEY;
    
    @PostConstruct
    public void getMxnAK() {
        MXN_API_KEY = mxnKey;
    }
    
    @Value("${third-api.mxn.secret")
    private String mxnSec;
    
    /**
     * 免费 api secret
     * https://www.mxnzp.com/doc/list
     */
    public static String MXN_API_SECRET;
    
    @PostConstruct
    public void getMxnAS() {
        MXN_API_SECRET = mxnSec;
    }
    
    @Value("${third-api.cai-yun.key")
    private String caiYunKey;
    
    /**
     * 彩云天气 API
     */
    public static String CAI_YUN_API;
    
    @PostConstruct
    public void getCaiYunK() {
        CAI_YUN_API = caiYunKey;
    }
    
    @Value("${third-api.ju-he.key")
    private String juHeKey;
    
    public static String JU_HE_API;
    
    @PostConstruct
    public void getJuHeK() {
        JU_HE_API = juHeKey;
    }
    
    /* ..................账号密码系列.............结束了............... */
    
    /**
     * 星座名称
     */
    public static String CONSTELLATION_NAMES = "水瓶座,双鱼座,白羊座,金牛座,双子座,巨蟹座,狮子座,处女座,天秤座,天蝎座,射手座,摩羯座";
    
    /**
     * 等级名称
     */
    public static List<String> CHAT_ROOM_LEVEL_NAME = Lists.newArrayList("炼气修士", "筑基修士", "金丹修士", "元婴修士", "化神修士", "合体修士", "大乘修士", "渡劫修士", "天仙", "罗天上仙", "大罗金仙", "九天玄仙", "仙君", "仙帝", "人神", "天神", "神王", "天尊", "鸿蒙");
    
    /**
     * 宵禁解除 早上八点
     */
    public static LocalTime start = LocalTime.of(8, 0, 0);
    
    /**
     * 宵禁开始 晚上七点半
     */
    public static LocalTime end = LocalTime.of(19, 30, 0);
    
    /**
     * 迎新帖子回复记录
     */
    public static final String WELCOME_CHECK_REPLY = "CHECK:REPLY";
    
    /**
     * 聊天室新人欢迎
     */
    public static final String CHAT_ROOM_WELCOME = "CHECK:CHAT:ROOM:WELCOME";
    
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
