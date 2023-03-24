package online.elves.utils;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.Strings;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 程序员黄历.
 */
public class CoderAlmanac {
    /**
     * 特殊日期
     */
    private static final String[] SEPCIALS = {
            "{\"date\":\"20140214\", \"type\":\"bad\", \"name\":\"待在男（女）友身边\", \"description\":\"脱团火葬场，入团保平安。\"}"
    };
    /**
     * 星期
     */
    private static final String[] WEEK_CN = {"补", "一", "二", "三", "四", "五", "六", "日"};
    /**
     * 写作工具
     */
    private static final String[] TOOLS = {"Eclipse写程序", "MSOffice写文档", "记事本写程序", "Windows8", "Linux", "MacOS", "IE", "Android设备", "iOS设备"};
    /**
     * 变量名
     */
    private static final String[] VAR_NAMES = {"jieguo", "huodong", "pay", "expire", "zhangdan", "every", "free", "i1", "a", "virtual", "ad", "spider", "mima", "pass", "ui"};
    /**
     * 喝什么
     */
    private static final String[] DRINKS = {"水", "茶", "红茶", "绿茶", "咖啡", "奶茶", "可乐", "鲜奶", "豆奶", "果汁", "果味汽水", "苏打水", "运动饮料", "酸奶", "酒"};
    /**
     * 方位
     */
    private static final String[] DIRCTS = {"北方", "东北方", "东方", "东南方", "南方", "西南方", "西方", "西北方"};
    /**
     * 活动
     */
    private static final String[] ACTIVITIES = {
            "{\"name\":\"写单元测试\",\"good\":\"写单元测试将减少出错\",\"bad\":\"写单元测试会降低你的开发效率\",\"weekend\":false}",
            "{\"name\":\"洗澡\",\"good\":\"你几天没洗澡了？\",\"bad\":\"会把设计方面的灵感洗掉\",\"weekend\":true}",
            "{\"name\":\"锻炼一下身体\",\"good\":\"\",\"bad\":\"能量没消耗多少，吃得却更多\",\"weekend\":true}",
            "{\"name\":\"抽烟\",\"good\":\"抽烟有利于提神，增加思维敏捷\",\"bad\":\"除非你活够了，死得早点没关系\",\"weekend\":true}",
            "{\"name\":\"白天上线\",\"good\":\"今天白天上线是安全的\",\"bad\":\"可能导致灾难性后果\",\"weekend\":false}",
            "{\"name\":\"重构\",\"good\":\"代码质量得到提高\",\"bad\":\"你很有可能会陷入泥潭\",\"weekend\":false}",
            "{\"name\":\"使用%t\",\"good\":\"你看起来更有品位\",\"bad\":\"别人会觉得你在装逼\",\"weekend\":false}",
            "{\"name\":\"跳槽\",\"good\":\"该放手时就放手\",\"bad\":\"鉴于当前的经济形势，你的下一份工作未必比现在强\",\"weekend\":false}",
            "{\"name\":\"招人\",\"good\":\"你面前这位有成为牛人的潜质\",\"bad\":\"这人会写程序吗？\",\"weekend\":false}",
            "{\"name\":\"面试\",\"good\":\"面试官今天心情很好\",\"bad\":\"面试官不爽，会拿你出气\",\"weekend\":false}",
            "{\"name\":\"提交辞职申请\",\"good\":\"公司找到了一个比你更能干更便宜的家伙，巴不得你赶快滚蛋\",\"bad\":\"鉴于当前的经济形势，你的下一份工作未必比现在强\",\"weekend\":false}",
            "{\"name\":\"申请加薪\",\"good\":\"老板今天心情很好\",\"bad\":\"公司正在考虑裁员\",\"weekend\":false}",
            "{\"name\":\"晚上加班\",\"good\":\"晚上是程序员精神最好的时候\",\"bad\":\"\",\"weekend\":true}",
            "{\"name\":\"在妹子面前吹牛\",\"good\":\"改善你矮穷挫的形象\",\"bad\":\"会被识破\",\"weekend\":true}",
            "{\"name\":\"撸管\",\"good\":\"避免缓冲区溢出\",\"bad\":\"强撸灰飞烟灭\",\"weekend\":true}",
            "{\"name\":\"浏览成人网站\",\"good\":\"重拾对生活的信心\",\"bad\":\"你会心神不宁\",\"weekend\":true}",
            "{\"name\":\"命名变量%v\",\"good\":\"\",\"bad\":\"\",\"weekend\":false}",
            "{\"name\":\"写超过%l行的方法\",\"good\":\"你的代码组织的很好，长一点没关系\",\"bad\":\"你的代码将混乱不堪，你自己都看不懂\",\"weekend\":false}",
            "{\"name\":\"提交代码\",\"good\":\"遇到冲突的几率是最低的\",\"bad\":\"你遇到的一大堆冲突会让你觉得自己是不是时间穿越了\",\"weekend\":false}",
            "{\"name\":\"代码复审\",\"good\":\"发现重要问题的几率大大增加\",\"bad\":\"你什么问题都发现不了，白白浪费时间\",\"weekend\":false}",
            "{\"name\":\"开会\",\"good\":\"写代码之余放松一下打个盹，有益健康\",\"bad\":\"小心被扣屎盆子背黑锅\",\"weekend\":false}",
            "{\"name\":\"打DOTA\",\"good\":\"你将有如神助\",\"bad\":\"你会被虐的很惨\",\"weekend\":true}",
            "{\"name\":\"晚上上线\",\"good\":\"晚上是程序员精神最好的时候\",\"bad\":\"你白天已经筋疲力尽了\",\"weekend\":false}",
            "{\"name\":\"修复BUG\",\"good\":\"你今天对BUG的嗅觉大大提高\",\"bad\":\"新产生的BUG将比修复的更多\",\"weekend\":false}",
            "{\"name\":\"设计评审\",\"good\":\"设计评审会议将变成头脑风暴\",\"bad\":\"人人筋疲力尽，评审就这么过了\",\"weekend\":false}",
            "{\"name\":\"需求评审\",\"good\":\"\",\"bad\":\"\",\"weekend\":false}",
            "{\"name\":\"上微博\",\"good\":\"今天发生的事不能错过\",\"bad\":\"今天的微博充满负能量\",\"weekend\":true}",
            "{\"name\":\"上AB站\",\"good\":\"还需要理由吗？\",\"bad\":\"满屏兄贵亮瞎你的眼\",\"weekend\":true}",
            "{\"name\":\"玩FlappyBird\",\"good\":\"今天破纪录的几率很高\",\"bad\":\"除非你想玩到把手机砸了\",\"weekend\":true}"
    };

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class Act {
        /**
         * 名称
         */
        private String name;
        /**
         * 适宜
         */
        private String good;
        /**
         * 不宜
         */
        private String bad;
        /**
         * 周末事件
         */
        private boolean weekend;
    }

    /**
     * 获取星星
     *
     * @param count
     * @return
     */
    private static String star(int count) {
        // 星星
        String str = "";
        for (int i = 0; i < 5; i++) {
            if (i < count) {
                str += "★";
            } else {
                str += "☆";
            }
        }
        return str;
    }

    /**
     * 当前时间字符串
     *
     * @param now
     * @return
     */
    private static String today(LocalDate now) {
        return "今天是 " + now.getYear() + "年" + now.getMonth().getValue() + "月" + now.getDayOfMonth() + "日 星期" + WEEK_CN[now.getDayOfWeek().getValue()];
    }

    /**
     * 伪随机算法
     *
     * @param daySeed
     * @param indexSeed
     * @return
     */
    private static int random(int daySeed, int indexSeed) {
        int n = daySeed % 11117;
        for (int i = 0; i < 100 + indexSeed; i++) {
            n = n * n;
            n = n % 11117;
        }
        return n;
    }

    /**
     * 从数组中随机挑选N个
     *
     * @param source
     * @param size
     * @param seed
     * @return
     */
    private static List<String> pickRandom(List<String> source, int size, int seed) {
        // 插入数组
        List<String> res = Lists.newArrayList();
        // 拷贝数组
        for (String s : source) {
            res.add(s);
        }
        // 遍历处理
        for (int i = 0; i < source.size() - size; i++) {
            // 需要删除的元素
            res.remove(res.get(random(seed, i) % res.size()));
        }
        return res;
    }

    /**
     * 获取今日运势
     *
     * @param seed
     * @return
     */
    private static Pair<List<Act>, List<Act>> getActs(int seed, boolean isWeekend) {
        // 获取所有对象
        List<Act> acts = Arrays.asList(ACTIVITIES).stream().map(x -> JSON.parseObject(x, Act.class)).collect(Collectors.toList());
        // 过滤对象
        acts = filter(acts, isWeekend);
        // 适宜的个数
        int good = random(seed, 98) % 3 + 2;
        // 不宜的个数
        int bad = random(seed, 87) % 3 + 2;
        // 获取随机值
        List<Act> randoms = actRandom(acts, good + bad, seed);
        // 获取返回值
        List<Act> ga = Lists.newArrayList(), ba = Lists.newArrayList();
        // 循环对象
        for (int i = 0; i < good + bad; i++) {
            // 获取对象
            Act act = replace(randoms.get(i), seed);
            // 写入
            if (i < good) {
                ga.add(act);
            } else {
                ba.add(act);
            }
        }
        // 返回对象
        return Pair.of(ga, ba);
    }

    /**
     * 替换执行字符串
     *
     * @param act
     * @return
     */
    private static Act replace(Act act, int seed) {
        String name = act.getName();
        if (name.contains("%v")) {
            name = name.replace("%v", VAR_NAMES[random(seed, 12) % VAR_NAMES.length]);
        }
        if (name.contains("%t")) {
            name = name.replace("%t", TOOLS[random(seed, 11) % TOOLS.length]);
        }
        if (name.contains("%l")) {
            name = name.replace("%l", (random(seed, 12) % 247 + 30) + "");
        }
        // 返回name
        act.setName(name);
        return act;
    }

    /**
     * 随机运势
     *
     * @param acts
     * @return
     */
    private static List<Act> actRandom(List<Act> acts, int size, int seed) {
        // 获取字符串值
        List<String> strActs = acts.stream().map(JSON::toJSONString).collect(Collectors.toList());
        // 随机对象 并 返回
        return pickRandom(strActs, size, seed).stream().map(x -> JSON.parseObject(x, Act.class)).collect(Collectors.toList());
    }

    /**
     * 过滤对象
     *
     * @param acts
     * @return
     */
    private static List<Act> filter(List<Act> acts, boolean isWeekend) {
        // 返回对象
        List<Act> res = Lists.newArrayList();
        // 如果是周末的话 只留下周末的事件
        if (isWeekend) {
            // 循环对象
            for (Act a : acts) {
                if (a.isWeekend()) {
                    res.add(a);
                }
            }
            return res;
        }
        return acts;
    }

    /**
     * 生成黄历
     *
     * @return
     */
    public static String genAlmanac() {
        // 当前日期
        LocalDate now = LocalDate.now();
        // 今天
        int day = now.getDayOfWeek().getValue();
        // 是否是周末
        boolean isWeekedn = day >= 6;
        // 当前日
        int iday = now.getYear() * 10000 + now.getMonth().getValue() * 100 + now.getDayOfMonth();
        // 结果
        StringBuilder str = new StringBuilder(today(now));
        str.append("\n\n");
        // 座位
        str.append("**座位朝向 :** 面向 `").append(DIRCTS[random(iday, 2) % DIRCTS.length]).append("` 写程序, BUG最少").append("\n\n");
        str.append("**今日宜饮 :** ").append(Strings.join(pickRandom(Arrays.asList(DRINKS), 2, iday), ',')).append("\n\n");
        str.append("**女神亲近指数 :** ").append(star(random(iday, 6) % 5 + 1)).append("\n\n");
        // 获取随机对象
        Pair<List<Act>, List<Act>> acts = getActs(iday, isWeekedn);
        str.append("\n#### 宜").append("\n\n");
        List<Act> gas = acts.getKey();
        for (Act a : gas) {
            String good = a.getGood();
            if (StringUtils.isNotBlank(good)) {
                good = " **:** " + good;
            }
            str.append("- ").append(" **").append(a.getName()).append("** ").append(good).append("\n\n");
        }
        str.append("\n#### 不宜").append("\n\n");
        List<Act> bas = acts.getValue();
        for (Act a : bas) {
            String bad = a.getBad();
            if (StringUtils.isNotBlank(bad)) {
                bad = " **:** " + bad;
            }
            str.append("- ").append(" **").append(a.getName()).append("** ").append(bad).append("\n\n");
        }
        return str.toString();
    }

    public static void main(String[] args) {
        System.out.println(genAlmanac());
    }
}
