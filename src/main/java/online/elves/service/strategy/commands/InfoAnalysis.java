package online.elves.service.strategy.commands;

import lombok.extern.slf4j.Slf4j;
import online.elves.enums.CrLevel;
import online.elves.mapper.entity.DistrictCn;
import online.elves.service.FService;
import online.elves.service.strategy.CommandAnalysis;
import online.elves.third.apis.Weather;
import online.elves.third.fish.Fish;
import online.elves.third.fish.model.FUser;
import online.elves.utils.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 用户信息命令分析
 */
@Slf4j
@Component
public class InfoAnalysis extends CommandAnalysis {
    
    @Resource
    FService fService;
    
    /**
     * 关键字
     */
    private static final List<String> keys = Arrays.asList("信息", "天气");
    
    @Override
    public boolean check(String commonKey) {
        return keys.contains(commonKey);
    }
    
    @Override
    public void process(String commandKey, String commandDesc, String userName) {
        // 获取当前用户信息
        FUser user = Fish.getUser(userName);
        if (Objects.isNull(user)) {
            Fish.sendMsg("亲爱的 @" + userName + " " + CrLevel.getCrLvName(userName) + " " + " . 鱼排跟我有点事情沟通不畅, 我找不到你的身份玉牌呢~ 要不你等会儿再问我...");
            return;
        }
        // 用户所在城市
        String userCity = user.getUserCity();
        // 天气状况
        String weather = "嘶...气象雷达丢失...你容我想想~";
        // 经纬信息
        DistrictCn dc = null;
        if (StringUtils.isBlank(userCity)) {
            weather = "(ˉ▽￣～) 切~~我特喵都不知道你在哪...爱咋咋滴吧";
        } else {
            dc = fService.getDistrict(userCity);
            if (Objects.nonNull(dc)) {
                // 获取天气
                weather = Weather.get(dc, 0);
            }
        }
        // 遍历命令
        switch (commandKey) {
            case "信息":
                // 发送信息
                Fish.sendMsg("亲爱的 @" + userName + " " + CrLevel.getCrLvName(userName) + " " + " . " + "... 你加入鱼排 `" +
                        DateUtil.transferMinutes(Long.valueOf((System.currentTimeMillis() - user.getOId()) / 60 / 1000L).intValue()) +
                        "` 就已经累计在线 `" + DateUtil.transferMinutes(user.getOnlineMinute()) + "` 了, " +
                        "\n\n 摸鱼的时候你顺便已经赚了 `" + user.getUserPoint() + "` 积分..." +
                        "\n\n 😊现在你可以试试对我说：`凌 帮助` 来跟我互动吧\n\n" +
                        "> " + weather);
                break;
            case "天气":
                // 发送信息
                Fish.sendMsg("亲爱的 @" + userName + " " + CrLevel.getCrLvName(userName) + " " + " . \n\n > " + weather);
                break;
            default:
                // 啥也不用干
                break;
        }
    }
    
}
