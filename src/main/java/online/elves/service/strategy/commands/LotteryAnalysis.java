package online.elves.service.strategy.commands;

import lombok.extern.slf4j.Slf4j;
import online.elves.service.strategy.CommandAnalysis;
import online.elves.third.apis.Lottery;
import online.elves.third.fish.Fish;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 彩票命令分析
 */
@Slf4j
@Component
public class LotteryAnalysis extends CommandAnalysis {
    
    /**
     * 中文数字
     */
    private final static String[] CN_NUM = {"零", "一", "二", "三", "四", "五"};
    
    /**
     * 关键字
     */
    private static final List<String> keys = Arrays.asList("抽奖", "试试手气","双色球", "大乐透", "超级大乐透", "福彩3D", "福彩3d", "3D", "3d", "七星彩", "排列三", "排列五");
    
    @Override
    public boolean check(String commonKey) {
        return keys.contains(commonKey);
    }
    
    @Override
    public void process(String commandKey, String commandDesc, String userName) {
        // 枚举命令
        switch (commandKey) {
            case "抽奖":
            case "试试手气":
                // 抽奖
                Fish.send2User(userName, "亲爱的玩家  ." + buildLotteryDesc(Lottery.get()));
                break;
            case "双色球":
                Fish.sendMsg(Lottery.getInfo(commandDesc, "ssq"));
                break;
            case "大乐透":
            case "超级大乐透":
                Fish.sendMsg(Lottery.getInfo(commandDesc, "cjdlt"));
                break;
            case "福彩3D":
            case "福彩3d":
            case "3D":
            case "3d":
                Fish.sendMsg(Lottery.getInfo(commandDesc, "fc3d"));
                break;
            case "七星彩":
                Fish.sendMsg(Lottery.getInfo(commandDesc, "qxc"));
                break;
            case "排列三":
                Fish.sendMsg(Lottery.getInfo(commandDesc, "pl3"));
                break;
            case "排列五":
                Fish.sendMsg(Lottery.getInfo(commandDesc, "pl5"));
                break;
        }
    }
    
    /**
     * 构建翻译结果
     * @param level
     * @return
     */
    private static String buildLotteryDesc(int level) {
        if (level == 0) {
            // 谢谢参与
            return "谢谢参与";
        }
        return "恭喜您, 抽中 " + CN_NUM[level] + " 等奖";
    }
    
}
