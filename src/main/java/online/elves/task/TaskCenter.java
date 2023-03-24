package online.elves.task;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import online.elves.config.Const;
import online.elves.task.service.TaskService;
import online.elves.third.fish.Fish;
import online.elves.utils.RedisUtil;
import online.elves.ws.WsClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.websocket.CloseReason;
import javax.websocket.Session;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 活动中心.
 */
@Slf4j
@Component
public class TaskCenter {
    
    @Resource
    TaskService taskService;
    
    /**
     * 一分钟一次
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void check1min() {
        // 开红包
        taskService.buyMysteryCode();
    }
    
    /**
     * 心跳检测 三分钟一次
     */
    @Scheduled(cron = "30 0/2 * * * ?")
    public void check3min() {
        // 临时对象
        Map<String, Pair<LocalDateTime, Session>> temp = Maps.newConcurrentMap();
        // 遍历
        for (Map.Entry<String, Pair<LocalDateTime, Session>> et : WsClient.session.entrySet()) {
            // 对象
            String sKey = et.getKey();
            // session
            Pair<LocalDateTime, Session> etValue = et.getValue();
            // 五分钟前
            LocalDateTime dt = LocalDateTime.now().minusMinutes(5);
            if (etValue.getKey().isAfter(dt)) {
                // 回写
                temp.put(sKey, etValue);
                try {
                    // 没有过期 发送心跳
                    etValue.getValue().getBasicRemote().sendPing(StandardCharsets.UTF_8.encode("-hb-"));
                } catch (IOException e) {
                    log.info("{} session 心跳发送失败...", sKey);
                }
            } else {
                // 关闭连接
                try {
                    etValue.getValue().close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "超时清理"));
                } catch (IOException e) {
                    log.info("{} session 关闭失败...", sKey);
                }
            }
        }
        // 回写
        WsClient.session = temp;
    }
    
    /**
     * 五分钟一次
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void check5min() {
        // 迎新
        taskService.welcome();
        // 新人报道
        taskService.runCheck();
    }
    
    /**
     * 午间活动 片段雨
     */
    @Scheduled(cron = "0 30 11,17 * * ?")
    public void mcRain() {
        if (StringUtils.isBlank(RedisUtil.get(Const.MYSTERY_CODE_ZZK_TIME))) {
            RedisUtil.set(Const.MYSTERY_CODE_ZZK_TIME, "聊天室活动-片段雨-沾沾卡", 60);
            Fish.sendMsg("天降神秘代码, [0,10] 随机个数. 限时 1 min. 冲鸭~");
        } else {
            Fish.sendMsg("天降神秘代码开启中. 冲鸭~");
        }
    }
}
