package online.elves.service.strategy.commands;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import online.elves.config.Const;
import online.elves.enums.CrLevel;
import online.elves.service.FService;
import online.elves.service.strategy.CommandAnalysis;
import online.elves.third.fish.Fish;
import online.elves.utils.DateUtil;
import online.elves.utils.RedisUtil;
import online.elves.utils.RegularUtil;
import online.elves.utils.StrUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 排行榜命令分析
 */
@Slf4j
@Component
public class RankingAnalysis extends CommandAnalysis {

    @Resource
    FService fService;

    /**
     * 关键字
     */
    private static final List<String> keys = Arrays.asList(
            "0", "2", "3", "4", "5", "6","7",
            "红包计数器", "小冰召唤师", "点歌大王", "朗读小玩童", "图王", "互动指数","弹幕巨头",
            "10", "11", "12", "13", "14",
            "兑换日榜", "兑换周榜", "兑换月榜", "兑换年榜", "兑换总榜",
            "20", "21", "22", "23", "24",
            "话痨日榜", "话痨周榜", "话痨月榜", "话痨年榜", "话痨总榜");

    @Override
    public boolean check(String commonKey) {
        return keys.contains(commonKey);
    }

    @Override
    public void process(String commandKey, String commandDesc, String userName) {
        // 默认红包计数器
        int type = 0;
        // 日榜
        boolean isDay = true;
        // 遍历命令
        switch (commandKey) {
            case "0":
            case "红包计数器":
                type = 0;
                break;
            case "2":
            case "图王":
                type = 2;
                break;
            case "3":
            case "小冰召唤师":
                type = 3;
                break;
            case "4":
            case "点歌大王":
                type = 4;
                break;
            case "5":
            case "朗读小玩童":
                type = 5;
                break;
            case "6":
            case "互动指数":
                type = 6;
                break;
            case "7":
            case "弹幕巨头":
                type = 7;
                break;
            case "10":
            case "兑换日榜":
                type = 10;
                break;
            case "11":
            case "兑换周榜":
                type = 11;
                isDay = false;
                break;
            case "12":
            case "兑换月榜":
                type = 12;
                isDay = false;
                break;
            case "13":
            case "兑换年榜":
                type = 13;
                isDay = false;
                break;
            case "14":
            case "兑换总榜":
                type = 14;
                isDay = false;
                break;
            case "20":
            case "话痨日榜":
                type = 20;
                break;
            case "21":
            case "话痨周榜":
                type = 21;
                isDay = false;
                break;
            case "22":
            case "话痨月榜":
                type = 22;
                isDay = false;
                break;
            case "23":
            case "话痨年榜":
                type = 23;
                isDay = false;
                break;
            case "24":
            case "话痨总榜":
                type = 24;
                isDay = false;
                break;
            default:
                // 什么也不做
                break;
        }
        Fish.sendMsg("@" + userName + " " + CrLevel.getCrLvName(userName) + " " + count(type, RegularUtil.isDate(commandDesc) ? commandDesc : "", isDay));
    }

    /**
     * 处理统计
     *
     * @param i
     * @param date
     * @param isDay
     * @return
     */
    private String count(int i, String date, boolean isDay) {
        // 当前日期
        LocalDate now = LocalDate.now();
        // 简单字符串
        String simpleDay = null;
        // 当前日期
        LocalDate ld = LocalDate.now();
        // 时间不为空 则还原时间
        if (StringUtils.isNotBlank(date)) {
            // 还原
            now = DateUtil.parseLd(date);
            ld = now;
        }
        // 当前日期不为空 就处理一下
        if (Objects.nonNull(now)) {
            // 简单字符串 - 日期
            simpleDay = DateUtil.format(DateUtil.ld2UDate(now), DateUtil.DAY_SIMPLE);
        }
        try {
            // redisKey
            String redisKey = StrUtils.getKey(Const.RANKING_PREFIX, i + "", simpleDay);
            // 构建返回
            StringBuilder msg = new StringBuilder(" :\n>");
            // 不知道说啥
            switch (i) {
                case 0:
                    msg.append("赌狗...收手吧,外面都是痞老板\n");
                    break;
                case 2:
                    msg.append("图王...你要是总发新图,阿达已经盯上你了\n");
                    break;
                case 3:
                    msg.append("小冰召唤师...一天啥破事没有,净找小冰了\n");
                    break;
                case 4:
                    msg.append("点歌大王...寂寞的时候,是网抑云让我们更寂寞\n");
                    break;
                case 5:
                    msg.append("朗读小玩童...大概率他们是让小冰叫爸爸\n");
                    break;
                case 6:
                    msg.append("精灵互动指数~...谢谢你的喜欢呀~\n");
                    break;
                case 7:
                    msg.append("弹幕之王~...不就是点积分么? 主打一个炫酷~\n");
                    break;
                case 10:
                    msg.append("财阀日榜, 你的每一个神秘代码都闪耀着无上的光芒~\n");
                    break;
                case 11:
                    msg.append("财阀周榜, 你的每一个神秘代码都闪耀着无上的光芒~\n");
                    break;
                case 12:
                    msg.append("财阀月榜, 你的每一个神秘代码都闪耀着无上的光芒~\n");
                    break;
                case 13:
                    msg.append("财阀年榜, 至尊席位非你莫属~\n");
                    break;
                case 14:
                    msg.append("财阀总榜, 恐怖如斯~\n");
                    break;
                case 20:
                    msg.append("话痨日榜, 你的每一句话扔进鱼排, 激起了阵阵涟漪~\n");
                    break;
                case 21:
                    msg.append("话痨周榜, 你的每一句话扔进鱼排, 激起了阵阵涟漪~\n");
                    break;
                case 22:
                    msg.append("话痨月榜, 你的每一句话扔进鱼排, 激起了阵阵涟漪~\n");
                    break;
                case 23:
                    msg.append("话痨年榜, 你就是传说中的水帝~\n");
                    break;
                case 24:
                    msg.append("话痨总榜, 恐怖如斯...聊天室少了你, 就少了一片天地~\n");
                    break;
                default:
                    msg.append("词穷了...\n");
                    break;
            }
            // 排序
            int no = 1;
            // 过滤对象
            switch (i) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 20:
                    return getRank10(StrUtils.getKey(Const.RANKING_DAY_PREFIX, String.valueOf(i), simpleDay), msg, no);
                case 11:
                case 21:
                    assert ld != null;
                    return getRank10(StrUtils.getKey(Const.RANKING_WEEK_PREFIX, String.valueOf(i), String.valueOf(ld.getYear()), String.valueOf(ld.get(WeekFields.ISO.weekOfWeekBasedYear()))), msg, no);
                case 12:
                case 22:
                    assert ld != null;
                    return getRank10(StrUtils.getKey(Const.RANKING_MONTH_PREFIX, String.valueOf(i), String.valueOf(ld.getYear()), String.valueOf(ld.getMonth().getValue())), msg, no);
                case 13:
                case 23:
                    assert ld != null;
                    return getRank10(StrUtils.getKey(Const.RANKING_YEAR_PREFIX, String.valueOf(i), String.valueOf(ld.getYear())), msg, no);
                case 14:
                case 24:
                    // 话痨总榜
                    // 财阀总榜
                    return getRank10(StrUtils.getKey(Const.RANKING_PREFIX, String.valueOf(i)), msg, no);
                default:
                    return getRank10(redisKey, msg, no);
            }
        } catch (Exception e) {
            log.error("查询失败...", e);
            return "算了算了... \n>我查不出来(~~气鼓鼓~~) ...";
        }

    }

    /**
     * 获取redis排行榜前十名
     *
     * @param redisKey
     * @param msg
     * @param no
     * @return
     */
    private String getRank10(String redisKey, StringBuilder msg, int no) {
        // 否则就是 redis 前十名
        Set<ZSetOperations.TypedTuple> defRank = RedisUtil.rank(redisKey, 0, 9);
        if (CollUtil.isEmpty(defRank)) {
            return "就...有没有一种可能...你们并没有产生数据让我来统计...￣□￣｜｜";
        }
        // 用户列表
        List<Integer> userNos = Lists.newArrayList();
        // 遍历
        for (ZSetOperations.TypedTuple t : defRank) {
            userNos.add(Integer.valueOf(Objects.requireNonNull(t.getValue()).toString()));
        }
        // 获取用户
        Map<Integer, String> users = fService.getUserMap(userNos);
        // 组装
        for (ZSetOperations.TypedTuple t : defRank) {
            Integer uNo = Integer.valueOf(Objects.requireNonNull(t.getValue()).toString());
            msg.append(no).append(". ").append(users.getOrDefault(uNo, String.valueOf(uNo))).append(" ... ").append(Objects.requireNonNull(t.getScore()).intValue());
            if (no == 1) {
                buildTitle(msg, redisKey);
            }
            msg.append("\n");
            no++;
        }
        return msg.toString();
    }

    /**
     * 加个称号
     *
     * @param msg
     * @param redisKey
     */
    private void buildTitle(StringBuilder msg, String redisKey) {
        if (redisKey.contains(Const.RANKING_DAY_PREFIX)) {
            // 今日
            msg.append(" ");
            genTitle(msg, "今日鱼王","https://img1.voc.com.cn/UpLoadFile/2017/08/03/201708031117037846.jpg");
            msg.append(" ");
        }
        if (redisKey.contains(Const.RANKING_WEEK_PREFIX)) {
            // 本周
            msg.append(" ");
            genTitle(msg, "周度鱼王","https://img1.voc.com.cn/UpLoadFile/2017/08/03/201708031117037846.jpg");
            msg.append(" ");
        }
        if (redisKey.contains(Const.RANKING_MONTH_PREFIX)) {
            // 月度
            msg.append(" ");
            genTitle(msg, "月度鱼王","https://img1.voc.com.cn/UpLoadFile/2017/08/03/201708031117037846.jpg");
            msg.append(" ");
        }
        if (redisKey.contains(Const.RANKING_YEAR_PREFIX)) {
            // 年度
            msg.append(" ");
            genTitle(msg, "年度鱼王","https://img1.voc.com.cn/UpLoadFile/2017/08/03/201708031117037846.jpg");
            msg.append(" ");
        }
    }

    /**
     * 生成对象
     *
     * @param msg
     * @param title
     * @param img
     */
    private void genTitle(StringBuilder msg, String title, String img) {
        msg.append("![](https://unv-shield.librian.net/api/unv_shield?scale=0.79&txt=");
        // 称号
        msg.append(title);
        msg.append("&url=");
        // 图片
        msg.append(img);
        //底色
        msg.append("&backcolor=000000");
        // 背景色
        msg.append("&fontcolor=ffffff)");
    }
}
