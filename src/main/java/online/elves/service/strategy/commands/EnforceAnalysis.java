package online.elves.service.strategy.commands;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import online.elves.config.Const;
import online.elves.enums.CrLevel;
import online.elves.mapper.entity.MsgRecord;
import online.elves.service.CurrencyService;
import online.elves.service.FService;
import online.elves.service.strategy.CommandAnalysis;
import online.elves.third.apis.IceNet;
import online.elves.third.fish.Fish;
import online.elves.utils.RedisUtil;
import online.elves.utils.RegularUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 执法命令分析
 */
@Slf4j
@Component
public class EnforceAnalysis extends CommandAnalysis {

    @Resource
    FService fService;

    /**
     * 关键字
     */
    private static final List<String> keys =
            Arrays.asList("敏感词", "删除敏感词", "敏感度", "敏感词查询", "敏感度查询", "撤回");

    @Override
    public boolean check(String commonKey) {
        return keys.contains(commonKey);
    }

    @Override
    public void process(String commandKey, String commandDesc, String userName) {
        // 只有网管才会处理
        if (Objects.equals(RedisUtil.get(Const.ADMIN), userName) || RedisUtil.get(Const.OP_LIST).contains(userName)) {
            // 缩小命令
            switch (commandKey) {
                case "敏感词查询":
                    String swQ = RedisUtil.get(Const.SENSITIVE_WORDS);
                    if (StringUtils.isBlank(swQ)) {
                        Fish.sendMsg("暂无敏感词汇");
                    } else {
                        List<String> parsed = JSON.parseArray(swQ, String.class);
                        if (CollUtil.isNotEmpty(parsed)) {
                            Fish.sendMsg("敏感词汇 => " + swQ);
                        } else {
                            Fish.sendMsg("暂无敏感词汇");
                        }
                    }
                    break;
                case "敏感度查询":
                    Fish.sendMsg("敏感限定改为[连续`15`分钟内触发`" + RedisUtil.get(Const.SENSITIVE_WORDS_LIMIT) + "`次敏感词汇后, 将被禁言`15`分钟(暂无积分处罚)]");
                    break;
                case "敏感词":
                    if (StringUtils.isBlank(commandDesc)) {
                        Fish.sendMsg("你连个标点都不给我...是想要通杀么?");
                    } else {
                        if ("小冰,凌,小智,鸽,ida".contains(commandDesc)) {
                            Fish.sendMsg("机器人触发口令豁免!嘻嘻, 你想干啥呢~");
                        } else if (commandDesc.contains("敏感")) {
                            Fish.sendMsg("你想俄罗斯套娃嘛~ (#^.^#)");
                        } else {
                            String sw = RedisUtil.get(Const.SENSITIVE_WORDS);
                            if (StringUtils.isBlank(sw)) {
                                RedisUtil.set(Const.SENSITIVE_WORDS, JSON.toJSONString(Lists.newArrayList(commandDesc)));
                            } else {
                                List<String> parsed = JSON.parseArray(sw, String.class);
                                parsed.add(commandDesc);
                                RedisUtil.set(Const.SENSITIVE_WORDS, JSON.toJSONString(parsed));
                            }
                            Fish.sendMsg("已添加敏感词[" + commandDesc + "]");
                        }
                    }
                    break;
                case "删除敏感词":
                    String swDel = RedisUtil.get(Const.SENSITIVE_WORDS);
                    if (StringUtils.isBlank(swDel)) {
                        Fish.sendMsg("当前无生效敏感词, 无需删除");
                    } else {
                        List<String> parsed = JSON.parseArray(swDel, String.class);
                        parsed.remove(commandDesc);
                        RedisUtil.set(Const.SENSITIVE_WORDS, JSON.toJSONString(parsed));
                        Fish.sendMsg("已删除敏感词[" + commandDesc + "]");
                    }
                    break;
                case "敏感度":
                    String count = "5";
                    if (RegularUtil.isNum1Max(commandDesc)) {
                        count = commandDesc;
                    }
                    // 设置敏感度
                    RedisUtil.set(Const.SENSITIVE_WORDS_LIMIT, count);
                    Fish.sendMsg("敏感限定改为[连续`15`分钟内触发`" + count + "`次敏感词汇后, 将被禁言`15`分钟(暂无积分处罚)]");
                    break;
                case "撤回":
                    // 撤回范围
                    String[] split = commandDesc.split("_");
                    // 默认100
                    int size = 100;
                    if (split.length > 1 && RegularUtil.isNum1Max(split[1])) {
                        size = Integer.parseInt(split[1]);
                    }
                    // 获取最近100条内包含敏感词的对象
                    List<MsgRecord> records = fService.getMsgRecordDescLimit(size);
                    // 关键词
                    String keyWord = split[0];
                    // 批量撤回
                    int sCount = 0;
                    for (MsgRecord mr : records) {
                        // 包含关键词
                        if (mr.getContent().contains(keyWord)) {
                            // 撤回消息
                            Fish.revoke(mr.getOid());
                            sCount++;
                        }
                    }
                    Fish.sendMsg("@" + userName + " 撤回关键词 [" + keyWord + "] 完成, 受影响记录为 " + sCount + " 条!");
                    break;
                default:
                    // 什么也不做
                    break;
            }
        } else {
            switch (commandKey) {
                case "敏感词查询":
                    String sw = RedisUtil.get(Const.SENSITIVE_WORDS);
                    if (StringUtils.isBlank(sw)) {
                        Fish.sendMsg("暂无敏感词汇");
                    } else {
                        Fish.sendMsg("敏感词汇 => " + sw);
                    }
                    break;
                case "敏感度查询":
                    Fish.sendMsg("敏感限定改为[连续`15`分钟内触发`" + RedisUtil.get(Const.SENSITIVE_WORDS_LIMIT) + "`次敏感词汇后, 将被禁言`15`分钟(暂无积分处罚)]");
                    break;
                default:
                    Fish.sendMsg("@" + userName + " " + CrLevel.getCrLvName(userName) + " " + " : \n\n 嘻嘻, 命令可不是你想用就能用的! ");
                    break;
            }
        }
    }

}
