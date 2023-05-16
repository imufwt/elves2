package online.elves.service.strategy.commands;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import online.elves.config.Const;
import online.elves.enums.CrLevel;
import online.elves.service.CurrencyService;
import online.elves.service.FService;
import online.elves.service.strategy.CommandAnalysis;
import online.elves.third.apis.IceNet;
import online.elves.third.fish.Fish;
import online.elves.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 网管命令分析
 */
@Slf4j
@Component
public class AdminAnalysis extends CommandAnalysis {

    @Resource
    FService fService;

    /**
     * 关键字
     */
    private static final List<String> keys =
            Arrays.asList("送鱼翅", "送鱼丸", "欢乐时光", "天降鱼丸", "退费", "惹不起", "巡逻", "停止检查", "违禁词");

    @Override
    public boolean check(String commonKey) {
        return keys.contains(commonKey);
    }

    @Override
    public void process(String commandKey, String commandDesc, String userName) {
        // 只有网管才会处理
        if (Objects.equals(RedisUtil.get(Const.ADMIN), userName)) {
            // 缩小命令
            switch (commandKey) {
                case "违禁词":
                    RedisUtil.set("BLACK:WORD", RedisUtil.get("BLACK:WORD") + "," + commandDesc);
                    Fish.sendMsg("已添加违禁词[" + commandDesc + "]");
                    break;
                case "送鱼翅":
                    // 补偿鱼翅
                    CurrencyService.sendCurrency(commandDesc.split("_")[0], Integer.valueOf(commandDesc.split("_")[1]), "渔场老板操作");
                    break;
                case "送鱼丸":
                    // 补偿鱼翅
                    CurrencyService.sendCurrencyFree(commandDesc.split("_")[0], Integer.valueOf(commandDesc.split("_")[1]), "渔场老板操作");
                    break;
                case "退费":
                    String un = commandDesc.split("_")[0];
                    // 发红包
                    Fish.sendSpecify(un, Integer.valueOf(commandDesc.split("_")[1]), un + " : GM操作退费");
                    break;
                case "欢乐时光":
                    if (StringUtils.isBlank(RedisUtil.get(Const.CURRENCY_HAPPY_TIME))) {
                        RedisUtil.set(Const.CURRENCY_HAPPY_TIME, "happyTime", 60);
                        Fish.sendMsg("欢乐时光, 鱼翅兑换价格 1-64 随机数. 限时 1 min. 冲鸭~");
                    } else {
                        Fish.sendMsg("鱼翅欢乐时光开启中. 冲鸭~");
                    }
                    break;
                case "天降鱼丸":
                    if (StringUtils.isBlank(RedisUtil.get(Const.CURRENCY_FREE_TIME))) {
                        RedisUtil.set(Const.CURRENCY_FREE_TIME, "聊天室活动-天降鱼丸", 60);
                        Fish.sendMsg("天降鱼丸, [0,10] 随机个数. 限时 1 min. 冲鸭~");
                    } else {
                        Fish.sendMsg("天降鱼丸开启中. 冲鸭~");
                    }
                    break;
                case "惹不起":
                    String opList = RedisUtil.get(Const.OP_LIST);
                    if (StringUtils.isBlank(opList)) {
                        RedisUtil.set(Const.OP_LIST, JSON.toJSONString(Lists.newArrayList(commandDesc)));
                    } else {
                        List<String> ops = JSON.parseArray(opList, String.class);
                        ops.add(commandDesc);
                        RedisUtil.set(Const.OP_LIST, JSON.toJSONString(ops));
                    }
                    Fish.sendMsg("收到收到, 惹不起~");
                    break;
                case "巡逻":
                    if (StringUtils.isBlank(commandDesc)) {
                        Fish.send2User(RedisUtil.get(Const.ADMIN), "FWQZT 服务器状态, WH 维护, SXHC 刷新缓存.  巡逻/停止检查");
                    } else {
                        // 限制条件
                        String cmd = RedisUtil.get(Const.PATROL_LIMIT_PREFIX + commandDesc);
                        if (StringUtils.isBlank(cmd)) {
                            Fish.sendMsg("报告, 正常执勤中~");
                        } else {
                            RedisUtil.del(Const.PATROL_LIMIT_PREFIX + commandDesc);
                            Fish.sendMsg("收到, 开始执勤~");
                        }
                    }
                    break;
                case "停止检查":
                    if (StringUtils.isNotBlank(commandDesc)) {
                        // 限制条件
                        String stop = RedisUtil.get(Const.PATROL_LIMIT_PREFIX + commandDesc);
                        if (StringUtils.isBlank(stop)) {
                            RedisUtil.set(Const.PATROL_LIMIT_PREFIX + commandDesc, commandDesc);
                            Fish.sendMsg("收到, 已取消执勤~");
                        } else {
                            Fish.sendMsg("报告, 尚未开始执勤~");
                        }
                    } else {
                        Fish.send2User(RedisUtil.get(Const.ADMIN), "FWQZT 服务器状态, WH 维护, SXHC 刷新缓存.  巡逻/停止检查");
                    }
                    break;
                default:
                    // 什么也不做
                    break;
            }
        } else {
            switch (commandKey) {
                case "天降鱼丸":
                    // 财阀标记
                    String cfCount = RedisUtil.get(Const.CURRENCY_TIMES_PREFIX + userName);
                    if (StringUtils.isNotBlank(cfCount)) {
                        // 幸运编码
                        String lKey = "luck:try:free:" + userName;
                        // 是财阀. 每天第一次打劫 概率获得sth.
                        if (StringUtils.isBlank(RedisUtil.get(lKey))) {
                            // 当前时间
                            LocalDateTime now = LocalDateTime.now();
                            // 第二天0点过期
                            RedisUtil.set(lKey, userName, Long.valueOf(Duration.between(now, now.toLocalDate().plusDays(1).atStartOfDay()).getSeconds()).intValue());
                            // 小冰亲密度大于2048 每天可以召唤一次鱼丸
                            if (IceNet.getUserIntimacy(userName) > 2048) {
                                if (StringUtils.isBlank(RedisUtil.get(Const.CURRENCY_FREE_TIME))) {
                                    RedisUtil.set(Const.CURRENCY_FREE_TIME, "聊天室活动-天降鱼丸-OpUser:" + userName, 60);
                                    Fish.sendMsg("天降鱼丸, [0,10] 随机个数. 限时 1 min. 冲鸭~");
                                } else {
                                    Fish.sendMsg("天降鱼丸开启中. 冲鸭~");
                                }
                            } else {
                                Fish.sendMsg("@" + userName + " " + CrLevel.getCrLvName(userName) + " " + " : \n\n 嘻嘻, 渔民大人~ 和小冰的亲密度要大于`2048`哦, 加油呀! ");
                            }
                        } else {
                            Fish.sendMsg("@" + userName + " " + CrLevel.getCrLvName(userName) + " " + " : \n\n 嘻嘻, 渔民大人~ 你今天召唤过咯! ");
                        }
                    } else {
                        Fish.sendMsg("@" + userName + " " + CrLevel.getCrLvName(userName) + " " + " : \n\n 先成为渔民吧🙄不然你捞啥 ");
                    }
                    break;
                case "补偿":
                    Fish.sendMsg("@" + userName + " " + CrLevel.getCrLvName(userName) + " " + " : 我就知道(p≧w≦q) 你要给自己加片段对不对...  ");
                    break;
                default:
                    Fish.sendMsg("@" + userName + " " + CrLevel.getCrLvName(userName) + " " + " : \n\n 你在说什么, 我怎么听不明白呢🙄 ");
                    break;
            }
        }
    }

}
