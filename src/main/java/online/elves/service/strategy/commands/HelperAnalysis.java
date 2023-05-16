package online.elves.service.strategy.commands;

import lombok.extern.slf4j.Slf4j;
import online.elves.config.Const;
import online.elves.enums.CrLevel;
import online.elves.service.strategy.CommandAnalysis;
import online.elves.third.fish.Fish;
import online.elves.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 命运命令分析
 */
@Slf4j
@Component
public class HelperAnalysis extends CommandAnalysis {

    /**
     * 关键字
     */
    private static final List<String> keys = Arrays.asList("帮助", "菜单");

    @Override
    public boolean check(String commonKey) {
        return keys.contains(commonKey);
    }

    @Override
    public void process(String commandKey, String commandDesc, String userName) {
        // 自定义命令
        String cmdSet = RedisUtil.get(Const.CMD_USER_SET + userName).replaceAll(",", " 或 ");
        if (StringUtils.isBlank(cmdSet)){
            cmdSet = "凌";
        }
        Fish.sendMsg("亲爱的 @" + userName + " " + CrLevel.getCrLvName(userName) + " " + " : 你好呀~\n" +
                "<details>" +
                "<summary> 帮助(菜单) 👇🏻 点开查看</summary>\n\n" +
                "> 指令后可跟 `空格` + 日期(yyyy-MM-dd) 查询指定日期排行, 否则默认当日\n\n" +
                "> Since ~~2022-09-06~~ 精灵重构, 总榜不变. 日榜 Since 2022-10-22 \n\n" +
                "> Since 2023-03-25 精灵2.0 一切待从头, 重整旧河山\n\n" +
                "\n" +

                "* `" + cmdSet + "  2` 或 `" + cmdSet + "  小冰召唤师` 统计 **召唤小冰** 前 10名\n" +
                "* `" + cmdSet + "  3` 或 `" + cmdSet + "  点歌大王` 统计 **点歌** 前 10名\n" +
                "* `" + cmdSet + "  4` 或 `" + cmdSet + "  朗读小玩童` 统计 **朗诵** 前 10名\n" +
                "* `" + cmdSet + "  5` 或 `" + cmdSet + "  图王` 统计 **发图** 前 10名\n" +
                "* `" + cmdSet + "  6` 或 `" + cmdSet + "  互动指数` 统计 **精灵互动** 前 10名\n" +
                "* `" + cmdSet + "  7` 或 `" + cmdSet + "  弹幕巨头` 统计 **发送弹幕** 前 10名\n" +

                "* `" + cmdSet + "  10` 或 `" + cmdSet + "  兑换日榜` 统计 **兑换鱼翅积分消耗日榜** 前 10名\n" +
                "* `" + cmdSet + "  11` 或 `" + cmdSet + "  兑换周榜` 统计 **兑换鱼翅积分消耗周榜** 前 10名\n" +
                "* `" + cmdSet + "  12` 或 `" + cmdSet + "  兑换月榜` 统计 **兑换鱼翅积分消耗月榜** 前 10名\n" +
                "* `" + cmdSet + "  13` 或 `" + cmdSet + "  兑换年榜` 统计 **兑换鱼翅积分消耗年榜** 前 10名\n" +
                "* `" + cmdSet + "  14` 或 `" + cmdSet + "  兑换总榜` 统计 **兑换鱼翅积分消耗总榜** 前 10名\n" +

                "* `" + cmdSet + "  20` 或 `" + cmdSet + "  话痨日榜` 统计 **发言日榜** 前 10名\n" +
                "* `" + cmdSet + "  21` 或 `" + cmdSet + "  话痨周榜` 统计 **发言周榜** 前 10名\n" +
                "* `" + cmdSet + "  22` 或 `" + cmdSet + "  话痨月榜` 统计 **发言月榜** 前 10名\n" +
                "* `" + cmdSet + "  23` 或 `" + cmdSet + "  话痨年榜` 统计 **发言年榜** 前 10名\n" +
                "* `" + cmdSet + "  24` 或 `" + cmdSet + "  话痨总榜` 统计 **发言总榜** 前 10名\n" +

                "* `" + cmdSet + "  25` 或 `" + cmdSet + "  散财日榜` 统计 **发红包金额日榜** 前 10名\n" +
                "* `" + cmdSet + "  26` 或 `" + cmdSet + "  散财周榜` 统计 **发红包金额周榜** 前 10名\n" +
                "* `" + cmdSet + "  27` 或 `" + cmdSet + "  散财月榜` 统计 **发红包金额月榜** 前 10名\n" +
                "* `" + cmdSet + "  28` 或 `" + cmdSet + "  散财年榜` 统计 **发红包金额年榜** 前 10名\n" +
                "* `" + cmdSet + "  29` 或 `" + cmdSet + "  散财总榜` 统计 **发红包金额总榜** 前 10名\n" +


                "* `" + cmdSet + "  30` 或 `" + cmdSet + "  赌圣日榜` 统计 **猜拳红包赢积分日榜** 前 10名\n" +
                "* `" + cmdSet + "  31` 或 `" + cmdSet + "  赌圣周榜` 统计 **猜拳红包赢积分周榜** 前 10名\n" +
                "* `" + cmdSet + "  32` 或 `" + cmdSet + "  赌圣月榜` 统计 **猜拳红包赢积分月榜** 前 10名\n" +
                "* `" + cmdSet + "  33` 或 `" + cmdSet + "  赌圣年榜` 统计 **猜拳红包赢积分年榜** 前 10名\n" +
                "* `" + cmdSet + "  34` 或 `" + cmdSet + "  赌圣总榜` 统计 **猜拳红包赢积分总榜** 前 10名\n" +


                "* `" + cmdSet + "  35` 或 `" + cmdSet + "  赌狗日榜` 统计 **猜拳红包输积分日榜** 前 10名\n" +
                "* `" + cmdSet + "  36` 或 `" + cmdSet + "  赌狗周榜` 统计 **猜拳红包输积分周榜** 前 10名\n" +
                "* `" + cmdSet + "  37` 或 `" + cmdSet + "  赌狗月榜` 统计 **猜拳红包输积分月榜** 前 10名\n" +
                "* `" + cmdSet + "  38` 或 `" + cmdSet + "  赌狗年榜` 统计 **猜拳红包输积分年榜** 前 10名\n" +
                "* `" + cmdSet + "  39` 或 `" + cmdSet + "  赌狗总榜` 统计 **猜拳红包输积分总榜** 前 10名\n" +
                "\n\n" +
                "* `" + cmdSet + "  触发词 xxx` 支持自定义触发词咯, xxx为英文数字或中文字符[1,3]个, 每次修改消耗66个`鱼翅`, 触发词专属你自己哦~\n" +
                "* `" + cmdSet + "  报时` 看看节假日, 不用返回主页啦. 抄的阿达的代码. 嘻嘻~\n" +
                "* `" + cmdSet + "  看帖` 不知道干什么的话,就来看帖子吧. 随机给你一个鱼排帖子瞅瞅~\n" +
                "* `" + cmdSet + "  双子座` 查询星座今日运势, 试试看吧~(仅支持中文全称)\n" +
                "* `" + cmdSet + "  信息` 查询当前个人信息(加入鱼排时间/在线时长/积分数/所在位置及天气)?\n" +
                "* `" + cmdSet + "  天气` 查询当前个人所在位置天气(基于你的公开位置, 你隐藏的话...哼😒)\n" +
                "* `" + cmdSet + "  去打劫` 试试看你的脸白么?\n" +
                "* `" + cmdSet + "  笑话` 讲个笑话? 虽然我不知道好不好笑\n" +
                "* `" + cmdSet + "  背包` 查询您的`鱼丸`与`鱼翅`数量. \n" +
                "* `" + cmdSet + "  当前活跃度/活跃度/活跃` 查询您的当前活跃度(活跃度计算基于您在聊天室的发言,无法感知其余操作, 一定有误差切莫较真) \n" +
                "* `" + cmdSet + "  运势/今日运势` 查询今日运势. 程序员只相信程序生成的黄历.来看看吧~ \n" +
                "* `" + cmdSet + "  摸鱼历/摸鱼/日历/鱼历` 摸鱼日历, 看看图一乐~ \n" +
                "* `" + cmdSet + "  捞鱼丸` 每天中午11:30, 下午17:30 鱼排上空准时出现鱼丸雨.所有渔民可以使用`捞鱼丸`命令获得随机[0,10]个`鱼丸`, 限时一分钟.~ \n" +
                "* `" + cmdSet + "  赠送鱼翅 username_xxx` 可以相互赠送咯.`username`是对方用户名.`xxx`是数量乱写/不写都是默认1. 预计增加小冰好感度比例 1:10~ \n" +
                "* `" + cmdSet + "  赠送鱼丸 username_xxx` 可以相互赠送咯.`username`是对方用户名.`xxx`是数量乱写/不写都是默认1. 预计增加小冰好感度比例 1:1~ \n" +
                "* `" + cmdSet + "  V50` 疯狂星期四, 冲鸭~ (感谢老王(巫昂)发起活动, 感谢王总(wuang)赞助!).~ \n" +
                "* `" + cmdSet + "  决斗/对线` 精灵发送猜拳红包, 试试你的手气(决斗会写你的名字, 对线不会~~听说小智是赌王?~~).~ \n" +
                "* `" + cmdSet + "  15` 或 `" + cmdSet + " 今日水分` 看看你今天水了多少条.~ \n" +
                "* `" + cmdSet + "  探路者` 看看[迷宫游戏](https://maze.hancel.org/)排行榜. 暴多积分等你来拿~ \n" +
                "* `" + cmdSet + "  天降鱼丸` 和小冰亲密度大于`2048`的渔民大人, 每天可以自主开启一次天降鱼丸哦~ \n" +
                "*  每天 9,10,15,16 点的半点, 精灵发送猜拳红包(64积分) 全员可抢, 试试手气吧~ \n" +
                "</details>" +
                "这不是 AI, 这是个匹配规则. 切勿较真哦~\n");
    }
}
