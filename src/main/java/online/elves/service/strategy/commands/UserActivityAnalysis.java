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
            Fish.sendMsg("亲爱的 @" + userName + " " + CrLevel.getCrLvName(userName) + " " + " . 你当前活跃度可能为 `2%` ~ 保持 `10分钟10条(每十分钟只有前十条有效)` 的发言频率, 预计 `50` 分钟后满活跃~");
        } else {
            if ("100".equals(uAct)) {
                Fish.sendMsg("亲爱的 @" + userName + " " + CrLevel.getCrLvName(userName) + " " + " . 你当前活跃度可能为 `100%` ~ 水满咯. 做点自己想做的吧😋...比如~~兑换个鱼翅玩玩~~");
            } else {
                Fish.sendMsg("亲爱的 @" + userName + " " + CrLevel.getCrLvName(userName) + " " + " . 你当前活跃度可能为 `" + uAct + "%` ~ 保持 `10分钟10条(每十分钟只有前十条有效)` 的发言频率, 预计 `" + calFull(uAct, userName) + "` 分钟后满活跃~");
            }
        }
    }

    /**
     * 计算预计多少秒后满活跃
     *
     * @param uAct
     * @param userName
     * @return
     */
    private static String calFull(String uAct, String userName) {
        // 转义
        BigDecimal live = new BigDecimal(uAct);
        if (live.longValue() >= 100) {
            return "0";
        }
        // 减法 100 - uAct
        BigDecimal subtract = BigDecimal.valueOf(100).subtract(live);
        // cd还有多久
        Long expire = RedisUtil.getExpire(Const.USER_ACTIVITY_LIMIT + userName);
        // 计算剩余时间 计算时间 剩余活跃/20 * 10
        BigDecimal decimal;
        if (expire > 0) {
            // 当前cd内还有
            BigDecimal divide = subtract.divide(BigDecimal.valueOf(20), 2, RoundingMode.HALF_DOWN);
            // 剩余时间
            BigDecimal divided = new BigDecimal(expire).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_DOWN);
            // 不足一个时间周期了
            if (divide.intValue() <= 1) {
                return divided.toString();
            } else {
                // 直接减去一个周期 / 10 + expire
                decimal = divide.subtract(BigDecimal.ONE).multiply(BigDecimal.valueOf(10)).add(divided);
            }
        } else {
            decimal = subtract.multiply(BigDecimal.valueOf(10)).divide(BigDecimal.valueOf(20), 2, RoundingMode.HALF_DOWN);
        }

        // 返回结果
        return decimal.toString();
    }

}
