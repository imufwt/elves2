package online.elves.service.strategy.commands;

import lombok.extern.slf4j.Slf4j;
import online.elves.enums.CrLevel;
import online.elves.service.strategy.CommandAnalysis;
import online.elves.third.fish.Fish;
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
        Fish.sendMsg("亲爱的 @" + userName +" "+ CrLevel.getCrLvName(userName) +" "+ " : 你好呀~\n" +
                "<details>" +
                "<summary> 帮助(菜单) 👇🏻 点开查看</summary>\n\n" +
                "> 指令后可跟 `空格` + 日期(yyyy-MM-dd) 查询指定日期排行, 否则默认当日\n\n" +
                "> Since ~~2022-09-06~~ 精灵重构, 总榜不变. 日榜 Since 2022-10-22 \n\n" +
                "> Since 2023-03-25 精灵2.0 一切待从头, 重整旧河山\n\n" +
                "\n" +
                "* `凌  0` 或 `凌  红包计数器` 统计 **发言** 前 10名\n" +
                
                "* `凌  2` 或 `凌  小冰召唤师` 统计 **召唤小冰** 前 10名\n" +
                "* `凌  3` 或 `凌  点歌大王` 统计 **点歌** 前 10名\n" +
                "* `凌  4` 或 `凌  朗读小玩童` 统计 **朗诵** 前 10名\n" +
                "* `凌  5` 或 `凌  图王` 统计 **发图** 前 10名\n" +
                "* `凌  6` 或 `凌  互动指数` 统计 **精灵互动** 前 10名\n" +
                
                "* `凌  10` 或 `凌  兑换日榜` 统计 **兑换神秘代码积分消耗日榜** 前 10名\n" +
                "* `凌  11` 或 `凌  兑换周榜` 统计 **兑换神秘代码积分消耗周榜** 前 10名\n" +
                "* `凌  12` 或 `凌  兑换月榜` 统计 **兑换神秘代码积分消耗月榜** 前 10名\n" +
                "* `凌  13` 或 `凌  兑换年榜` 统计 **兑换神秘代码积分消耗年榜** 前 10名\n" +
                "* `凌  14` 或 `凌  兑换总榜` 统计 **兑换神秘代码积分消耗总榜** 前 10名\n" +
                
                "* `凌  20` 或 `凌  话痨日榜` 统计 **发言日榜** 前 10名\n" +
                "* `凌  21` 或 `凌  话痨周榜` 统计 **发言周榜** 前 10名\n" +
                "* `凌  22` 或 `凌  话痨月榜` 统计 **发言月榜** 前 10名\n" +
                "* `凌  23` 或 `凌  话痨年榜` 统计 **发言年榜** 前 10名\n" +
                "* `凌  24` 或 `凌  话痨总榜` 统计 **发言总榜** 前 10名\n" +
                
                "* `凌  看帖` 不知道干什么的话,就来看帖子吧. 随机给你一个鱼排帖子瞅瞅~\n" +
                "* `凌  双子座` 查询星座今日运势, 试试看吧~(仅支持中文全称)\n" +
                "* `凌  信息` 查询当前个人信息(加入鱼排时间/在线时长/积分数/所在位置及天气)?\n" +
                "* `凌  天气` 查询当前个人所在位置天气(基于你的公开位置, 你隐藏的话...哼😒)\n" +
                "* `凌  去打劫` 试试看你的脸白么?\n" +
                "* `凌  笑话` 讲个笑话? 虽然我不知道好不好笑\n" +
                "* `凌  神秘代码` 查询您的神秘代码片段个数 \n" +
                "* `凌  当前活跃度/活跃度/活跃` 查询您的当前活跃度(活跃度计算基于您在聊天室的发言,无法感知其余操作, 一定有误差切莫较真) \n" +
                "* `凌  运势/今日运势` 查询今日运势. 程序员只相信程序生成的黄历.来看看吧~ \n" +
                "* `凌  摸鱼历/摸鱼/日历/鱼历` 摸鱼日历, 看看图一乐~ \n" +
                "* `凌  沾沾卡` 每天中午11:30, 下午17:30 准时出现片段雨.所有财阀可以使用沾沾卡获得[0,10]个随机片段, 限时一分钟.~ \n" +
                "*  每天 9,10,15,16 点的半点, 精灵发送猜拳红包(64积分) 全员可抢, 试试手气吧~ \n" +
                "</details>" +
                "这不是 AI, 这是个匹配规则. 切勿较真哦~\n");
    }
}
