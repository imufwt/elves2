package online.elves.service.strategy.commands;

import lombok.extern.slf4j.Slf4j;
import online.elves.config.Const;
import online.elves.enums.CrLevel;
import online.elves.service.FService;
import online.elves.service.strategy.CommandAnalysis;
import online.elves.third.fish.Fish;
import online.elves.utils.RedisUtil;
import online.elves.utils.RegularUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 货币命令分析
 */
@Slf4j
@Component
public class CurrencyAnalysis extends CommandAnalysis {

    /**
     * 关键字
     */
    private static final List<String> keys = Arrays.asList("背包", "决斗", "对线", "幸运卡", "兑换", "拆兑");

    @Resource
    FService fService;

    @Override
    public boolean check(String commonKey) {
        return keys.contains(commonKey);
    }

    @Override
    public void process(String commandKey, String commandDesc, String userName) {
        // 涩涩的命令
        switch (commandKey) {
            case "背包":
                // 当前兑换次数
                String times = RedisUtil.get(Const.CURRENCY_TIMES_PREFIX + userName);
                if (StringUtils.isBlank(times)) {
                    Fish.sendMsg("亲爱的 @" + userName + " " + CrLevel.getCrLvName(userName) + " " + " . 你还没有成为渔民呐~");
                } else {
                    String freeTimes = RedisUtil.get(Const.CURRENCY_TIMES_FREE_PREFIX + userName);
                    if (StringUtils.isBlank(freeTimes)) {
                        freeTimes = "0";
                    }
                    Fish.sendMsg("尊敬的渔民大人 @" + userName + " " + CrLevel.getCrLvName(userName) + " " + " . " +
                            "\n\n您的`鱼翅`还有 ...`" + times + "`个~  `鱼丸`还有 ...`" + freeTimes + "`个~" +
                            "\n\n > 1 `鱼翅` == 10 `鱼丸`");
                }
                break;
            case "决斗":
            case "对线":
                // 鱼翅个数 缓存 key
                String key = Const.CURRENCY_TIMES_PREFIX + userName;
                // 获取次数
                String fTimes = RedisUtil.get(key);
                // 判断次数
                if (StringUtils.isBlank(fTimes) || Integer.parseInt(fTimes) < 1) {
                    // 啥也不做
                    Fish.sendMsg("亲爱的 @" + userName + " . 您的鱼翅已耗尽咯(~~你拿什么跟我斗╭(╯^╰)╮~~)");
                } else {
                    // 加锁  增加 CD
                    if (StringUtils.isBlank(RedisUtil.get("CURRENCY_FIGHT_LIMIT"))) {
                        // 发送设置
                        Fish.sendMsg("亲爱的 @" + userName + " . 准备好了么? 决斗红包来喽(不能指定, 先到先得) `决斗全局锁, 下次召唤请...30...秒后(一分钟能发俩. 咱们都冷静下)` ~");
                        // 发送猜拳红包
                        Fish.sendRockPaperScissors(Objects.equals(commandKey, "决斗") ? userName : null, 32);
                        // 设置次数减一
                        RedisUtil.modify(key, -1);
                        // 加锁 一分钟一个
                        RedisUtil.set("CURRENCY_FIGHT_LIMIT", "limit", 31);
                    } else {
                        // 啥也不做
                        Fish.sendMsg("亲爱的 @" + userName + " . 美酒虽好, 可也不要贪杯哦~");
                    }
                }
                break;
            case "幸运卡":
                Fish.sendMsg("嘻嘻, 小冰抽奖活动要下线啦. 期待下次梦幻联动咯~");
                break;
            case "兑换":
                // 加锁  增加 CD
                if (StringUtils.isBlank(RedisUtil.get("CURRENCY_CHANGE"))) {
                    // 兑换CD 15秒
                    RedisUtil.set("CURRENCY_CHANGE", "limit", 15);
                    // 鱼翅个数 缓存 key
                    String lKey = Const.CURRENCY_TIMES_PREFIX + userName;
                    // 获取次数
                    String lTimes = RedisUtil.get(lKey);
                    // 判断次数
                    if (StringUtils.isBlank(lTimes)) {
                        // 啥也不做
                        Fish.sendMsg("亲爱的 @" + userName + " . 你还没有成为渔民呢(~~╭(╯^╰)╮~~)");
                    } else {
                        // 鱼丸数量
                        String lfKey = Const.CURRENCY_TIMES_FREE_PREFIX + userName;
                        // 鱼丸个数
                        String lfTimes = RedisUtil.get(lfKey);
                        if (StringUtils.isBlank(lfTimes)) {
                            Fish.send2User(userName, "亲爱的渔民大人 . 你的背包里貌似没有`鱼丸`哦(~~╭(╯^╰)╮~~)");
                        } else {
                            // 默认兑换一个鱼翅
                            int count = 1;
                            // 不为空且是数字, 就转换
                            if (StringUtils.isNotBlank(commandDesc) || RegularUtil.isNum(commandDesc)) {
                                count = Math.abs(Integer.parseInt(commandDesc));
                            }
                            // 不够扣
                            if (count * 10 > Integer.parseInt(lfTimes)) {
                                Fish.send2User(userName, "亲爱的渔民大人 . 兑换 [" + count + "] `鱼翅`需要 " + count * 10 + " 个`鱼丸`~ 你背包里不够啦~");
                            } else {
                                // 扣减鱼丸
                                fService.sendCurrencyFree(userName, -count * 10, "`鱼丸`兑换`鱼翅`");
                                // 增加鱼翅
                                fService.sendCurrency(userName, count, "`鱼丸`兑换`鱼翅`");
                            }
                        }
                    }
                } else {
                    Fish.send2User(userName, "亲爱的渔民大人. 业务繁忙, 请稍后重试(~~╭(╯^╰)╮~~), 全局锁`15s`");
                }
                break;
            case "拆兑":
                // 加锁  增加 CD
                if (StringUtils.isBlank(RedisUtil.get("CURRENCY_CHANGE"))) {
                    // 兑换CD 15秒
                    RedisUtil.set("CURRENCY_CHANGE", "limit", 15);
                    // 鱼翅个数 缓存 key
                    String lKey_ = Const.CURRENCY_TIMES_PREFIX + userName;
                    // 获取次数
                    String lTimes_ = RedisUtil.get(lKey_);
                    // 判断次数
                    if (StringUtils.isBlank(lTimes_)) {
                        // 啥也不做
                        Fish.sendMsg("亲爱的 @" + userName + " . 你还没有成为渔民呢(~~╭(╯^╰)╮~~)");
                    } else {
                        // 默认拆一个鱼翅
                        int count = 1;
                        // 不为空且是数字, 就转换
                        if (StringUtils.isNotBlank(commandDesc) || RegularUtil.isNum(commandDesc)) {
                            count = Math.abs(Integer.parseInt(commandDesc));
                        }
                        // 不够扣
                        if (count > Integer.parseInt(lTimes_)) {
                            Fish.send2User(userName, "亲爱的渔民大人 . 拆兑 [" + count + "] `鱼翅`是可以的~ 但是你背包里没有那么多啦~");
                        } else {
                            // 增加鱼丸
                            fService.sendCurrencyFree(userName, count * 10, "`鱼翅`拆兑`鱼丸`");
                            // 增加鱼翅
                            fService.sendCurrency(userName, -count, "`鱼翅`拆兑`鱼丸`");
                        }
                    }
                } else {
                    Fish.send2User(userName, "亲爱的渔民大人. 业务繁忙, 请稍后重试(~~╭(╯^╰)╮~~), 全局锁`15s`");
                }
                break;
            default:
                // 什么都不用做
                break;
        }

    }

}
