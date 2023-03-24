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

/**
 * 神秘代码命令分析
 */
@Slf4j
@Component
public class MysteryAnalysis extends CommandAnalysis {
    
    /**
     * 关键字
     */
    private static final List<String> keys = Arrays.asList("神秘代码");
    
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
            default:
                // 什么都不用做
                break;
        }
        
    }
    
}
