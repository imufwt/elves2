package online.elves.service.strategy.commands;

import lombok.extern.slf4j.Slf4j;
import online.elves.config.Const;
import online.elves.enums.CrLevel;
import online.elves.mapper.entity.User;
import online.elves.service.CurrencyService;
import online.elves.service.FService;
import online.elves.service.strategy.CommandAnalysis;
import online.elves.third.fish.Fish;
import online.elves.third.fish.model.FUser;
import online.elves.utils.RedisUtil;
import online.elves.utils.RegularUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
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
    private static final List<String> keys = Arrays.asList("背包", "决斗", "对线", "幸运卡", "兑换", "拆兑", "赠送鱼翅", "赠送鱼丸");

    @Resource
    FService fService;

    @Override
    public boolean check(String commonKey) {
        return keys.contains(commonKey);
    }

    @Override
    public void process(String commandKey, String commandDesc, String userName) {
        // 货币命令
        switch (commandKey) {
            case "背包":
                // 鱼翅
                int times = CurrencyService.getCurrency(userName);
                if (times < 0) {
                    Fish.sendMsg("亲爱的 @" + userName + " " + CrLevel.getCrLvName(userName) + " " + " . 你还没有成为渔民呐~");
                } else {
                    // 鱼丸
                    int lTimes = CurrencyService.getCurrencyFree(userName);
                    // 没有就是0
                    if (lTimes < 0) {
                        lTimes = 0;
                    }
                    Fish.sendMsg("尊敬的渔民大人 @" + userName + " " + CrLevel.getCrLvName(userName) + " " + " . " +
                            "\n\n您的`鱼翅`还有 ...`" + times + "`个~  `鱼丸`还有 ...`" + lTimes + "`个~" +
                            "\n\n > 1 `鱼翅` == 10 `鱼丸`");
                }
                break;
            case "决斗":
            case "对线":
                // 获取鱼翅数量
                int soloTimes = CurrencyService.getCurrency(userName);
                // 判断次数
                if (soloTimes < 0) {
                    // 啥也不做
                    Fish.sendMsg("亲爱的 @" + userName + " " + CrLevel.getCrLvName(userName) + " " + " . 你还没有成为渔民呐~");
                } else if (soloTimes < 1) {
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
                        CurrencyService.sendCurrency(userName, -1, "聊天室活动-猜拳决斗");
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
                    int dTimes = CurrencyService.getCurrency(userName);
                    // 判断次数
                    if (dTimes < 0) {
                        // 啥也不做
                        Fish.sendMsg("亲爱的 @" + userName + " . 你还没有成为渔民呢(~~╭(╯^╰)╮~~)");
                    } else {
                        // 鱼丸数量
                        int dfTimes = CurrencyService.getCurrencyFree(userName);
                        if (dfTimes < 0) {
                            Fish.send2User(userName, "亲爱的渔民大人 . 你的背包里貌似没有`鱼丸`哦(~~╭(╯^╰)╮~~)");
                        } else {
                            // 默认兑换一个鱼翅
                            int count = 1;
                            // 不为空且是数字, 就转换
                            if (StringUtils.isNotBlank(commandDesc) || RegularUtil.isNum(commandDesc)) {
                                count = Math.abs(Integer.parseInt(commandDesc));
                                // 过滤0
                                if (count < 1) {
                                    count = 1;
                                }
                            }
                            // 不够扣
                            if (count * 10 > dfTimes) {
                                Fish.send2User(userName, "亲爱的渔民大人 . 兑换 [" + count + "] `鱼翅`需要 " + count * 10 + " 个`鱼丸`~ 你背包里不够啦~");
                            } else {
                                // 扣减鱼丸
                                CurrencyService.sendCurrencyFree(userName, -count * 10, "`鱼丸`兑换`鱼翅`");
                                // 增加鱼翅
                                CurrencyService.sendCurrency(userName, count, "`鱼丸`兑换`鱼翅`");
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
                    // 鱼翅个数
                    int cTimes = CurrencyService.getCurrency(userName);
                    // 判断次数
                    if (cTimes < 0) {
                        // 啥也不做
                        Fish.sendMsg("亲爱的 @" + userName + " . 你还没有成为渔民呢(~~╭(╯^╰)╮~~)");
                    } else {
                        // 默认拆一个鱼翅
                        int count = 1;
                        // 不为空且是数字, 就转换
                        if (StringUtils.isNotBlank(commandDesc) || RegularUtil.isNum(commandDesc)) {
                            count = Math.abs(Integer.parseInt(commandDesc));
                            // 过滤0
                            if (count < 1) {
                                count = 1;
                            }
                        }
                        // 不够扣
                        if (count > cTimes) {
                            Fish.send2User(userName, "亲爱的渔民大人 . 拆兑 [" + count + "] `鱼翅`是可以的~ 但是你背包里没有那么多啦~");
                        } else {
                            // 增加鱼丸
                            CurrencyService.sendCurrencyFree(userName, count * 10, "`鱼翅`拆兑`鱼丸`");
                            // 增加鱼翅
                            CurrencyService.sendCurrency(userName, -count, "`鱼翅`拆兑`鱼丸`");
                        }
                    }
                } else {
                    Fish.send2User(userName, "亲爱的渔民大人. 业务繁忙, 请稍后重试(~~╭(╯^╰)╮~~), 全局锁`15s`");
                }
                break;
            case "赠送鱼翅":
                // 加锁  增加 CD
                if (StringUtils.isBlank(RedisUtil.get("CURRENCY_CHANGE"))) {
                    // 兑换CD 15秒
                    RedisUtil.set("CURRENCY_CHANGE", "limit", 15);
                    // 鱼翅个数
                    int sendTimes = CurrencyService.getCurrency(userName);
                    // 判断次数
                    if (sendTimes < 0) {
                        // 啥也不做
                        Fish.sendMsg("亲爱的 @" + userName + " . 你还没有成为渔民呢(~~╭(╯^╰)╮~~)");
                    } else {
                        // 默认赠送一个鱼翅
                        int count = 1;
                        // 拆分命令
                        String[] split = commandDesc.split("_");
                        // 不为空且是数字, 就转换
                        if (split.length > 1 && StringUtils.isNotBlank(split[1]) || RegularUtil.isNum(split[1])) {
                            count = Math.abs(new BigDecimal(split[1]).intValue());
                            // 过滤0
                            if (count < 1) {
                                count = 1;
                            }
                        }
                        // 不够
                        if (sendTimes < count) {
                            Fish.sendMsg("亲爱的 @" + userName + " . 你可没有那么多`鱼翅`(~~╭(╯^╰)╮~~)\n\n> 你只有`" + sendTimes + "个`, 已全部赠送~");
                            count = sendTimes;
                        }
                        // 目标用户
                        String target = split[0];
                        // 获取目标用户
                        User user = fService.getUser(target);
                        if (Objects.isNull(user)) {
                            Fish.send2User(userName, " 找不到用户[" + split[0] + "], 感谢老铁送给我老板的`鱼翅`...");
                            target = RedisUtil.get(Const.ADMIN);
                        }
                        // 自己送自己都改成送给admin
                        if (userName.equals(target)) {
                            Fish.sendMsg("@" + userName + " 感谢老铁送给我老板的`鱼翅`");
                            target = RedisUtil.get(Const.ADMIN);
                        }
                        // 扣减
                        CurrencyService.sendCurrency(userName, -count, "赠送`鱼翅`给 " + user.getUserNick() + "(" + target + ")");
                        // 增加
                        if (target.equals("xiaoIce")) {
                            CurrencyService.sendCurrency(target, count, userName + "|" + Objects.requireNonNull(Fish.getUser(userName)).getOId() + " 赠送`鱼翅`给你");
                        } else {
                            CurrencyService.sendCurrency(target, count, fService.getUser(userName).getUserNick() + "(" + userName + ")" + " 赠送`鱼翅`给你");
                        }
                    }
                } else {
                    Fish.send2User(userName, "亲爱的渔民大人. 礼物系统繁忙, 请稍后重试(~~╭(╯^╰)╮~~), 全局锁`15s`");
                }
                break;
            case "赠送鱼丸":
                // 加锁  增加 CD
                if (StringUtils.isBlank(RedisUtil.get("CURRENCY_CHANGE"))) {
                    // 兑换CD 15秒
                    RedisUtil.set("CURRENCY_CHANGE", "limit", 15);
                    // 鱼翅个数
                    int sendFreeTimes = CurrencyService.getCurrency(userName);
                    // 判断次数
                    if (sendFreeTimes < 0) {
                        // 啥也不做
                        Fish.sendMsg("亲爱的 @" + userName + " . 你还没有成为渔民呢(~~╭(╯^╰)╮~~)");
                    } else {
                        // 鱼丸数量
                        int sendFreeTimes_ = CurrencyService.getCurrencyFree(userName);
                        if (sendFreeTimes_ < 0) {
                            Fish.send2User(userName, "亲爱的渔民大人 . 你的背包里貌似没有`鱼丸`哦(~~╭(╯^╰)╮~~)");
                        } else {
                            // 默认兑换一个鱼翅
                            int count = 1;
                            // 拆分命令
                            String[] split = commandDesc.split("_");
                            // 不为空且是数字, 就转换
                            if (split.length > 1 && StringUtils.isNotBlank(split[1]) || RegularUtil.isNum(split[1])) {
                                count = Math.abs(new BigDecimal(split[1]).intValue());
                                // 过滤0
                                if (count < 1) {
                                    count = 1;
                                }
                            }
                            // 不够扣
                            if (count > sendFreeTimes_) {
                                Fish.sendMsg("亲爱的 @" + userName + " . 你可没有那么多`鱼丸`(~~╭(╯^╰)╮~~)\n\n> 你只有`" + sendFreeTimes_ + "个`, 已全部赠送~");
                                count = sendFreeTimes_;
                            }
                            // 目标用户
                            String target = split[0];
                            // 获取目标用户
                            User user = fService.getUser(target);
                            if (Objects.isNull(user)) {
                                Fish.send2User(userName, " 找不到用户[" + split[0] + "], 感谢老铁送给我老板的`鱼翅`...");
                                target = RedisUtil.get(Const.ADMIN);
                            }
                            // 自己送自己都改成送给admin
                            if (userName.equals(target)) {
                                Fish.sendMsg("@" + userName + " 感谢老铁送给我老板的`鱼丸`");
                                target = RedisUtil.get(Const.ADMIN);
                            }
                            // 扣减
                            CurrencyService.sendCurrencyFree(userName, -count, "赠送`鱼丸`给 " + target);
                            // 增加
                            if (target.equals("xiaoIce")) {
                                CurrencyService.sendCurrency(target, count, userName + "|" + Objects.requireNonNull(Fish.getUser(userName)).getOId() + " 赠送`鱼丸`给你");
                            } else {
                                CurrencyService.sendCurrency(target, count, fService.getUser(userName).getUserNick() + "(" + userName + ")" + " 赠送`鱼丸`给你");
                            }
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
