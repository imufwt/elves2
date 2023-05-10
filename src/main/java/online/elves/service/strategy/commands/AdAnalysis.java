package online.elves.service.strategy.commands;

import lombok.extern.slf4j.Slf4j;
import online.elves.config.Const;
import online.elves.enums.CrLevel;
import online.elves.service.strategy.CommandAnalysis;
import online.elves.third.fish.Fish;
import online.elves.utils.DateUtil;
import online.elves.utils.RedisUtil;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 广告命令分析
 */
@Slf4j
@Component
public class AdAnalysis extends CommandAnalysis {
    
    /**
     * 关键字
     */
    private static final List<String> keys = Arrays.asList("广告", "取消广告");
    
    @Override
    public boolean check(String commonKey) {
        return keys.contains(commonKey);
    }
    
    @Override
    public void process(String commandKey, String commandDesc, String userName) {
        // 只有网管才会处理
        if (Objects.equals(RedisUtil.get(Const.ADMIN), userName)) {
            if (commandKey.startsWith("取消")) {
                RedisUtil.del(Const.TEMPORARY_CONTENT);
                Fish.sendMsg("已取消广告");
            } else {
                // 当前时间
                LocalDateTime time = LocalDateTime.now();
                // 到今晚的时间差
                int intValue;
                // 替换换行符 并切割命令
                String[] split = commandDesc.replaceAll("☺️", " ").replaceAll("😄", "<br/>").split("\\^");
                if (split.length > 1) {
                    // 指定截止时间
                    intValue = Long.valueOf(Duration.between(time, DateUtil.parseLdt(split[1])).getSeconds()).intValue();
                } else {
                    // 默认当天
                    intValue = Long.valueOf(Duration.between(time, time.plusDays(1).toLocalDate().atStartOfDay()).getSeconds()).intValue();
                }
                RedisUtil.set(Const.TEMPORARY_CONTENT, split[0], intValue);
                Fish.sendMsg("已添加广告");
            }
        } else {
            Fish.sendMsg("@" + userName + " " + CrLevel.getCrLvName(userName) + " " + " : \n\n 1024 积分一天. 公益类型广告免费. 详询我老板 👉🏻 @" + RedisUtil.get(Const.ADMIN) + " ...");
        }
    }
    
}
