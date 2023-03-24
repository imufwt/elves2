package online.elves.service.strategy.commands;

import lombok.extern.slf4j.Slf4j;
import online.elves.config.Const;
import online.elves.enums.CrLevel;
import online.elves.service.strategy.CommandAnalysis;
import online.elves.third.fish.Fish;
import online.elves.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

/**
 * 活跃度命令分析
 */
@Slf4j
@Component
public class UserActivityAnalysis extends CommandAnalysis {
    
    /**
     * 关键字
     */
    private static final List<String> keys = Arrays.asList("当前活跃", "当前活跃度", "活跃度", "活跃");
    
    @Override
    public boolean check(String commonKey) {
        return keys.contains(commonKey);
    }
    
    @Override
    public void process(String commandKey, String commandDesc, String userName) {
        // 当前活跃度
        String uAct = RedisUtil.get(Const.USER_ACTIVITY + userName);
        if (StringUtils.isBlank(uAct)) {
            Fish.sendMsg("亲爱的 @" + userName + " " + CrLevel.getCrLvName(userName) + " " + " . 你当前活跃度可能为 `1.67%` ~ 保持 `30` 秒一次发言, 预计 `30` 分钟后满活跃~");
        } else {
            if ("100".equals(uAct)) {
                Fish.sendMsg("亲爱的 @" + userName + " " + CrLevel.getCrLvName(userName) + " " + " . 你当前活跃度可能为 `100%` ~ 水满咯. 做点自己想做的吧😋...比如~~召唤神秘代码~~");
            } else {
                Fish.sendMsg("亲爱的 @" + userName + " " + CrLevel.getCrLvName(userName) + " " + " . 你当前活跃度可能为 `" + uAct + "%` ~ 保持 `30` 秒一次发言, 预计 `" + calFull(uAct) + "` 分钟后满活跃~");
            }
        }
    }
    
    /**
     * 计算预计多少秒后满活跃
     * @param uAct
     * @return
     */
    private static String calFull(String uAct) {
        // 转义
        BigDecimal live = new BigDecimal(uAct);
        if (live.longValue() >= 100) {
            return "0";
        }
        // 减法 100 - uAct
        BigDecimal subtract = BigDecimal.valueOf(100).subtract(live);
        // 计算时间 * 30 / 1.67
        BigDecimal decimal = subtract.multiply(BigDecimal.valueOf(30)).divide(BigDecimal.valueOf(1.67 * 60), 2, RoundingMode.HALF_DOWN);
        // 返回结果
        return decimal.toString();
    }
    
}
