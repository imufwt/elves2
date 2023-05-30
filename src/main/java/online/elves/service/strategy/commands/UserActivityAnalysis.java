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
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
        // 允许小冰查询别人
        if (userName.equals("xiaoIce") && StringUtils.isNotBlank(commandDesc)) {
            userName = commandDesc;
        }
        // 当前活跃度
        String uAct = RedisUtil.get(Const.USER_ACTIVITY + userName);
        // 时间间隔
        String limit = RedisUtil.get("CALL:FISH:LIMIT:" + userName);
        if (StringUtils.isBlank(uAct) || StringUtils.isBlank(limit)) {
            LocalDateTime now = LocalDateTime.now();
            // key 时间差
            Integer diff = Long.valueOf(Duration.between(now, now.toLocalDate().plusDays(1).atStartOfDay()).getSeconds()).intValue();
            // 用户当前活跃度
            uAct = Fish.getUserLiveness(userName);
            // 设置活跃对象
            RedisUtil.reSet(Const.USER_ACTIVITY + userName, uAct, diff);
        }

        if (StringUtils.isBlank(uAct)) {
            Fish.sendMsg("亲爱的 @" + userName + " " + CrLevel.getCrLvName(userName) + " " + " . \n\n> 你当前活跃度可能为 `0.6%` ~ 保持 `60` 秒一次发言, 预计 `166.5` 分钟后满活跃~");
        } else {
            if ("100".equals(uAct)) {
                Fish.sendMsg("亲爱的 @" + userName + " " + CrLevel.getCrLvName(userName) + " " + " . \n\n>  你当前活跃度可能为 `100%` ~ 水满咯. 做点自己想做的吧😋...比如~~兑换个鱼翅玩玩~~");
            } else {
                Fish.sendMsg("亲爱的 @" + userName + " " + CrLevel.getCrLvName(userName) + " " + " . \n\n>  你当前活跃度可能为 `" + uAct + "%` ~ 保持 `60` 秒一次发言, 预计 `" + calFull(uAct) + "` 分钟后满活跃~");
            }
        }
    }

    /**
     * 计算预计多少秒后满活跃
     *
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
        // 计算时间  = 剩余活跃度 /  0.6
        BigDecimal decimal = subtract.multiply(BigDecimal.valueOf(10)).divide(BigDecimal.valueOf(6), 2, RoundingMode.HALF_DOWN);
        // 返回结果
        return decimal.toString();
    }
}
