package online.elves.service.strategy.commands;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import online.elves.config.Const;
import online.elves.enums.CrLevel;
import online.elves.enums.Words;
import online.elves.mapper.entity.User;
import online.elves.service.FService;
import online.elves.service.strategy.CommandAnalysis;
import online.elves.third.apis.Joke;
import online.elves.third.fish.Fish;
import online.elves.utils.DateUtil;
import online.elves.utils.LotteryUtil;
import online.elves.utils.RedisUtil;
import online.elves.utils.StrUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 娱乐命令分析
 */
@Slf4j
@Component
public class FunnyAnalysis extends CommandAnalysis {

    @Resource
    FService fService;


    /**
     * 关键字
     */
    private static final List<String> keys = Arrays.asList("去打劫", "笑话", "沾沾卡", "等级", "发个红包", "V50", "v50", "VME50", "vivo50", "今日水分", "25", "欧皇们", "非酋们");

    /**
     * 打劫概率
     */
    private static TreeMap<Integer, Double> odds = new TreeMap<>();

    // 初始化概率
    static {
        // 32-64积分
        odds.put(0, 0.04);
        // 0-10 个片段
        odds.put(1, 0.22);
        // 优惠券
        odds.put(2, 0.34);
        // 0-2 个碎片
        odds.put(3, 0.10);
        // 什么也没有 随机扣1-3片段
        odds.put(4, 0.30);
    }

    @Override
    public boolean check(String commonKey) {
        return keys.contains(commonKey);
    }

    @Override
    public void process(String commandKey, String commandDesc, String userName) {
        // 娱乐命令
        switch (commandKey) {
            case "去打劫":
                // 财阀标记
                String cfCount = RedisUtil.get(Const.MYSTERY_CODE_TIMES_PREFIX + userName);
                if (StringUtils.isNotBlank(cfCount)) {
                    // 幸运编码
                    String lKey = "luck:try:" + userName;
                    // 是财阀. 每天第一次打劫 概率获得sth.
                    if (StringUtils.isBlank(RedisUtil.get(lKey))) {
                        // 当前时间
                        LocalDateTime now = LocalDateTime.now();
                        // 第二天0点过期
                        RedisUtil.set(lKey, userName, Long.valueOf(Duration.between(now, now.toLocalDate().plusDays(1).atStartOfDay()).getSeconds()).intValue());
                        // 计算概率 送东西
                        switch (LotteryUtil.getLv(odds)) {
                            case 0:
                                int money = new SecureRandom().nextInt(32) + 32;
                                Fish.sendMsg("@" + userName + " " + CrLevel.getCrLvName(userName) + " " + " 承你吉言.我打劫回来咯~ 我抢到了300积分, 可是半路摔了一跤, 就剩... " + money + "  积分...了, ┭┮﹏┭┮ 呜呜呜~");
                                Fish.sendSpecify(userName, money, userName + ", 喏~ 给你!");
                                break;
                            case 1:
                                int s = new SecureRandom().nextInt(11);
                                Fish.sendMsg("@" + userName + " " + CrLevel.getCrLvName(userName) + " " + " 哇.我打劫回来了~ 抢到了... " + s + "  个片段...等下你要分我点啊~ ^_^");
                                fService.sendMysteryCode(userName, s, "聊天室活动-打劫");
                                break;
                            case 2:
                            case 3:
                                Fish.sendMsg("@" + userName + " " + CrLevel.getCrLvName(userName) + " " + " 哎呦呦...我头晕~ 打劫的事情改日再说吧...");
                                break;
                            case 4:
                                int rz = new SecureRandom().nextInt(3) + 1;
                                Fish.sendMsg("@" + userName + " " + CrLevel.getCrLvName(userName) + " " + " 哼, 一天啥事儿没干净陪你打劫了. 还啥也抢不到... 撂挑子不干了");
                                fService.sendMysteryCode(userName, -rz, "聊天室活动-打劫-无功而返");
                                break;
                            default:
                                break;
                        }
                        return;
                    }
                    // 不为空 啥也不做....
                }
                Fish.sendMsg("@" + userName + " " + CrLevel.getCrLvName(userName) + " " + Words.random("r"));
                break;
            case "笑话":
                Fish.sendMsg("@" + userName + " " + CrLevel.getCrLvName(userName) + " " + "  \n\n" + Joke.getJoke());
                break;
            case "沾沾卡":
                String zzk = RedisUtil.get(Const.MYSTERY_CODE_ZZK_TIME);
                if (StringUtils.isBlank(zzk)) {
                    Fish.send2User(userName, "财阀大人~ 心急吃不了热豆腐啦~ 没开片段雨呐. 嘻嘻");
                } else {
                    // 当前兑换次数
                    String times = RedisUtil.get(Const.MYSTERY_CODE_TIMES_PREFIX + userName);
                    if (StringUtils.isBlank(times)) {
                        Fish.sendMsg("亲爱的 @" + userName + " " + CrLevel.getCrLvName(userName) + " " + " . 你还没有成为我的财阀大人呐~");
                        break;
                    }
                    // 缓存key 没人只有一次机会
                    String rKey = "zzk-" + userName;
                    // 获取对象
                    String zzkU = RedisUtil.get(rKey);
                    if (StringUtils.isBlank(zzkU)) {
                        // 可以沾
                        fService.sendMysteryCode(userName, new Random().nextInt(11), zzk);
                        // 设置缓存 180 肯定大于活动时间
                        RedisUtil.set(rKey, userName, 180);
                    } else {
                        Fish.send2User(userName, "你已经参与过啦~ 谢谢财阀");
                    }
                }
                break;
            case "25":
            case "今日水分":
                // 用户编码
                Integer userNo_ = Fish.getUserNo(userName);
                // 缓存key
                String key_ = StrUtils.getKey(Const.RANKING_DAY_PREFIX, "20", DateUtil.format(new Date(), "yyyyMMdd"));
                // 获取得分
                Double score_ = RedisUtil.getScore(key_, userNo_ + "");
                // 不存在就赋值 0
                if (Objects.isNull(score_)) {
                    score_ = Double.valueOf("0");
                }
                // 当前经验
                int exp_ = score_.intValue();
                Fish.sendMsg("亲爱的 @" + userName + " 你今天水了 [ " + exp_ + " ] 点经验啦~" + " \n\n > 一起工作的才叫同事, 一起摸鱼的叫同伙~ 加油, 同伙");
                break;
            case "等级":
                // 用户编码
                Integer userNo = Fish.getUserNo(userName);
                // 缓存key
                String key = StrUtils.getKey(Const.RANKING_PREFIX, "24");
                // 获取得分
                Double score = RedisUtil.getScore(key, userNo + "");
                // 不存在就赋值 0
                if (Objects.isNull(score)) {
                    score = Double.valueOf("0");
                }
                // 当前经验
                int exp = score.intValue();
                // 当前等级
                CrLevel crLv = CrLevel.get(exp);
                Fish.sendMsg("亲爱的 @" + userName + " 您的聊天室等级为 " + CrLevel.getCrLvName(userName) + " [当前经验值: " + exp + "/" + crLv.end + "] " + " \n\n > 等级分为 " + String.join(" => ", Const.CHAT_ROOM_LEVEL_NAME));
                break;
            case "发个红包":
                Fish.sendMsg("小冰 发个红包");
                break;
            case "V50":
            case "v50":
            case "VME50":
            case "vivo50":
                String cd = "KFC:V:50:CD";
                if (LocalDate.now().getDayOfWeek().getValue() == 4) {
                    // 幸运编码 每周四
                    String lKey = "KFC:V:50:" + userName;
                    // 每周四只能有一次
                    if (StringUtils.isBlank(RedisUtil.get(lKey))) {
                        if (StringUtils.isBlank(RedisUtil.get(cd))) {
                            // 当前时间
                            LocalDateTime now = LocalDateTime.now();
                            // 第二天0点过期
                            RedisUtil.set(lKey, userName, Long.valueOf(Duration.between(now, now.toLocalDate().plusDays(1).atStartOfDay()).getSeconds()).intValue());
                            // CD 1 min
                            RedisUtil.set(cd, userName, 60);
                            // 发红包
                            Fish.sendSpecify(userName, 50, userName + " 给, 彰显实力!");
                            // 记录排行榜
                            RedisUtil.incrScore(Const.RANKING_PREFIX + "KFC", String.valueOf(Fish.getUserNo(userName)), 1);
                        } else {
                            Fish.sendMsg("@" + userName + " 不要复读, 不要着急. 我一分钟只能发一个哦~");
                        }
                    } else {
                        Fish.sendMsg("@" + userName + " 怎么肥事儿~ 已经给你看过鱼排实力啦~");
                    }
                } else {
                    Fish.sendMsg("@" + userName + " 今儿可不是疯狂星期四. 嘻嘻~ 心急吃不了热豆腐哦.");
                }
                break;
            case "欧皇们":
                // 返回对象
                JSONObject resp = JSON.parseObject(HttpUtil.get(RedisUtil.get("ICE:GAME:RANK")));
                // 排行榜
                JSONArray data = resp.getJSONArray("data");
                // 构建返回对象
                StringBuilder res = new StringBuilder("来看看咱们的欧皇们!").append("\n\n");
                buildTable(data, res);
                // 发送消息
                Fish.sendMsg(res.toString());
                break;
            case "非酋们":
                // 返回对象
                JSONObject uresp = JSON.parseObject(HttpUtil.get(RedisUtil.get("ICE:GAME:RANK:NULL:LUCK")));
                // 排行榜
                JSONArray udata = uresp.getJSONArray("data");
                // 构建返回对象
                StringBuilder ures = new StringBuilder("来看看咱们的非酋们! 统统不许笑").append("\n\n");
                // 组合下bug
                buildTable(udata, ures);
                // 发送消息
                Fish.sendMsg(ures.toString());
                break;
            default:
                // 什么也不用做
                break;
        }
    }

    /**
     * 构建表格
     *
     * @param data
     * @param res
     */
    private void buildTable(JSONArray data, StringBuilder res) {
        res.append("|排行|用户|抽奖次数|特等奖|一等奖|二等奖|三等奖|四等奖|五等奖|六等奖|参与奖|").append("\n");
        res.append("|:----:|:----:|:----:|:----:|:----:|:----:|:----:|:----:|:----:|:----:|:----:|").append("\n");
        AtomicInteger p = new AtomicInteger(0);
        data.stream().forEach(x -> {
            // 转换对象
            JSONObject o = (JSONObject) x;
            res.append("|").append(p.addAndGet(1));
            // 用户
            User uname = fService.getUser(o.getString("uname"));
            res.append("|").append(uname.getUserNick()).append("(").append(uname.getUserName()).append(")");
            res.append("|").append(o.getInteger("pay_times"));
            res.append("|").append(o.getInteger("lv1_times"));
            res.append("|").append(o.getInteger("lv2_times"));
            res.append("|").append(o.getInteger("lv3_times"));
            res.append("|").append(o.getInteger("lv4_times"));
            res.append("|").append(o.getInteger("lv5_times"));
            res.append("|").append(o.getInteger("lv6_times"));
            res.append("|").append(o.getInteger("lv7_times"));
            res.append("|").append(o.getInteger("lv8_times"));
            res.append("|").append("\n");
        });
    }

}
