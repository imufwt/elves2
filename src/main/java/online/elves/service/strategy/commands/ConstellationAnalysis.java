package online.elves.service.strategy.commands;

import lombok.extern.slf4j.Slf4j;
import online.elves.config.Const;
import online.elves.service.strategy.CommandAnalysis;
import online.elves.third.apis.Destiny;
import online.elves.third.fish.Fish;
import online.elves.utils.CoderAlmanac;
import online.elves.utils.DateUtil;
import online.elves.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 命运命令分析
 */
@Slf4j
@Component
public class ConstellationAnalysis extends CommandAnalysis {

    /**
     * 关键字
     */
    private static final List<String> keys = Arrays.asList("星座", "今日运势", "运势");

    @Override
    public boolean check(String commonKey) {
        return keys.contains(commonKey);
    }

    @Override
    public void process(String commandKey, String commandDesc, String userName) {
        // 文章命令
        switch (commandKey) {
            case "星座":
                Fish.sendMsg(Destiny.getConstellation(commandDesc));
                break;
            case "运势":
            case "今日运势":
                // 当前时间
                LocalDate now = LocalDate.now();
                // 缓存key
                String key = Const.ALMANAC_CODER_PREFIX + DateUtil.formatDay(LocalDate.now());
                // 获取运势缓存
                String almanac = RedisUtil.get(key);
                // 存在就返回  不存在就缓存
                if (StringUtils.isNotBlank(almanac)) {
                    // 不为空就直接返回
                    Fish.sendMsg(almanac);
                } else {
                    // 生成的运势
                    String genAlmanac = CoderAlmanac.genAlmanac();
                    // 发送
                    Fish.sendMsg(genAlmanac);
                    // 写入缓存
                    int diff = Long.valueOf(Duration.between(LocalDateTime.now(), now.plusDays(1).atStartOfDay()).getSeconds()).intValue();
                    RedisUtil.set(key, genAlmanac, diff);
                }
                break;
            default:
                // 什么也不用做
                break;
        }
    }

}
