package online.elves.message.listener;

import lombok.extern.slf4j.Slf4j;
import online.elves.config.Const;
import online.elves.enums.CrLevel;
import online.elves.enums.Words;
import online.elves.message.event.CrCmdEvent;
import online.elves.service.strategy.CommandAnalysis;
import online.elves.third.fish.Fish;
import online.elves.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 聊天室消息监听
 */
@Slf4j
@Component
public class CrCmdListener {

    @Resource
    private List<CommandAnalysis> commandAnalyses;

    @EventListener(classes = {CrCmdEvent.class})
    public void exec(CrCmdEvent event) {
        // 事件消息 命令
        String cmd = event.getCmd();
        // 用户
        String userName = event.getSource().toString();
        // 替换连续空格并拆分换行
        cmd = cmd.trim().replaceAll(" + ", " ").replaceAll("<span[^>]*?>(</span>)*$", " ").split("\\n")[0];
        // 按照空格切分命令
        String[] commandKeys = cmd.split(" ");
        // 用户命令
        String userCmd = RedisUtil.get(Const.CMD_USER_SET + userName);
        if (StringUtils.isBlank(userCmd)) {
            userCmd = "凌";
        }
        // 至少有一个空格, 才可能是命令 且第一个是 凌 开头 或是用户自定义
        if (userCmd.contains(commandKeys[0])) {
            if (commandKeys.length > 1) {
                // 关键词
                String commandKey = commandKeys[1].trim();
                // 补充信息
                String commandDesc = commandKeys.length < 3 ? "" : commandKeys[2];
                // 查询星座
                if (Arrays.asList(Const.CONSTELLATION_NAMES.split(",")).contains(commandKey)) {
                    // 替换对象
                    commandDesc = commandKey;
                    commandKey = "星座";
                }
                // 是否有匹配结果
                boolean match = false;
                // 遍历策略
                for (CommandAnalysis analysis : commandAnalyses) {
                    // 如果合适就处理
                    if (analysis.check(commandKey)) {
                        match = true;
                        // 执行方法
                        analysis.process(commandKey, commandDesc, userName);
                    }
                }
                // 没有匹配上
                if (!match) {
                    Fish.sendMsg("@" + userName + " " + CrLevel.getCrLvName(userName) + " " + Words.random("def"));
                }
            } else {
                // 自定义命令
                String cmdSet = RedisUtil.get(Const.CMD_USER_SET + userName).replaceAll(",", " 或 ");
                if (StringUtils.isBlank(cmdSet)){
                    cmdSet = "凌";
                }
                Fish.sendMsg("@" + userName + " " + CrLevel.getCrLvName(userName) + " " + " . 嗯哼~ 我在! 有什么可以帮你的么? \n\n你可以使用命令 `" + cmd + " 帮助` 来查看一些和我的交互");
            }

        }
    }

}