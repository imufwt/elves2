package online.elves.service.strategy.commands;

import lombok.extern.slf4j.Slf4j;
import online.elves.config.Const;
import online.elves.enums.CrLevel;
import online.elves.service.FService;
import online.elves.service.strategy.CommandAnalysis;
import online.elves.third.fish.Fish;
import online.elves.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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
            Arrays.asList("补偿", "欢乐时光", "片段雨", "退费");
    
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
                case "补偿":
                    // 补偿神秘代码
                    fService.sendMysteryCode(commandDesc.split("_")[0], Integer.valueOf(commandDesc.split("_")[1]), "GM操作");
                    break;
                case "退费":
                    String un = commandDesc.split("_")[0];
                    // 发红包
                    Fish.sendSpecify(un, Integer.valueOf(commandDesc.split("_")[1]), un + " : GM操作退费");
                    break;
                case "欢乐时光":
                    if (StringUtils.isBlank(RedisUtil.get(Const.MYSTERY_CODE_HAPPY_TIME))) {
                        RedisUtil.set(Const.MYSTERY_CODE_HAPPY_TIME, "happyTime", 60);
                        Fish.sendMsg("神秘代码欢乐时光, 兑换价格 1-64 随机数. 限时 1 min. 冲鸭~");
                    } else {
                        Fish.sendMsg("神秘代码欢乐时光开启中. 冲鸭~");
                    }
                    break;
                case "片段雨":
                    if (StringUtils.isBlank(RedisUtil.get(Const.MYSTERY_CODE_ZZK_TIME))) {
                        RedisUtil.set(Const.MYSTERY_CODE_ZZK_TIME, "聊天室活动-片段雨-沾沾卡", 60);
                        Fish.sendMsg("天降神秘代码, [0,10] 随机个数. 限时 1 min. 冲鸭~");
                    } else {
                        Fish.sendMsg("天降神秘代码开启中. 冲鸭~");
                    }
                    break;
                default:
                    // 什么也不做
                    break;
            }
        } else {
            switch (commandKey) {
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
