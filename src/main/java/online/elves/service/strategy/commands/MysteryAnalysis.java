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
import java.util.Objects;

/**
 * 神秘代码命令分析
 */
@Slf4j
@Component
public class MysteryAnalysis extends CommandAnalysis {

    /**
     * 关键字
     */
    private static final List<String> keys = Arrays.asList("神秘代码", "决斗", "对线");

    @Override
    public boolean check(String commonKey) {
        return keys.contains(commonKey);
    }

    @Override
    public void process(String commandKey, String commandDesc, String userName) {
        // 涩涩的命令
        switch (commandKey) {
            case "神秘代码":
                // 当前兑换次数
                String times = RedisUtil.get(Const.MYSTERY_CODE_TIMES_PREFIX + userName);
                if (StringUtils.isBlank(times)) {
                    Fish.sendMsg("亲爱的 @" + userName + " " + CrLevel.getCrLvName(userName) + " " + " . 你还没有成为我的财阀大人呐~");
                } else {
                    Fish.sendMsg("尊敬的财阀大人 @" + userName + " " + CrLevel.getCrLvName(userName) + " " + " . 您的神秘代码还有 ..." + times + "... 个片段~");
                }
                break;
            case "决斗":
            case "对线":
                // 神秘代码次数 缓存 key
                String key = Const.MYSTERY_CODE_TIMES_PREFIX + userName;
                // 获取次数
                String fTimes = RedisUtil.get(key);
                // 判断次数
                if (StringUtils.isBlank(fTimes) || Integer.parseInt(fTimes) < 1) {
                    // 啥也不做
                    Fish.sendMsg("亲爱的 @" + userName + " . 您的神秘代码次数已耗尽咯(~~你拿什么跟我斗╭(╯^╰)╮~~)");
                } else {
                    // 加锁  增加 CD
                    if (StringUtils.isBlank(RedisUtil.get("MYSTERY_CODE_FIGHT_LIMIT"))) {
                        // 发送设置
                        Fish.sendMsg("亲爱的 @" + userName + " . 准备好了么? 决斗红包来喽(不能指定, 先到先得) `决斗全局锁, 下次召唤请...30...秒后(一分钟能发俩. 咱们都冷静下)` ~");
                        // 发送猜拳红包
                        Fish.sendRockPaperScissors(Objects.equals(commandKey, "决斗") ? userName : null, 32);
                        // 设置次数减一
                        RedisUtil.modify(key, -1);
                        // 加锁 一分钟一个
                        RedisUtil.set("MYSTERY_CODE_FIGHT_LIMIT", "limit", 31);
                    } else {
                        // 啥也不做
                        Fish.sendMsg("亲爱的 @" + userName + " . 美酒虽好, 可也不要贪杯哦~");
                    }
                }
                break;
            default:
                // 什么都不用做
                break;
        }

    }

}
