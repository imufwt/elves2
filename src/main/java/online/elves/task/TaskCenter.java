package online.elves.task;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import online.elves.config.Const;
import online.elves.service.CurrencyService;
import online.elves.task.service.TaskService;
import online.elves.third.fish.Fish;
import online.elves.utils.DateUtil;
import online.elves.utils.RedisUtil;
import online.elves.utils.StrUtils;
import online.elves.ws.WsClient;
import online.elves.ws.handler.UserChat;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.Strings;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.math.BigDecimal;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * 活动中心.
 */
@Slf4j
@Component
public class TaskCenter {
    @Resource
    TaskService taskService;

    /**
     * 五秒一次
     */
    @Scheduled(cron = "0/5 * * * * ?")
    public void check15Sec() {
        // 开红包
        taskService.buyCurrency();
        // 在线检查
        taskService.onlineCheck();
    }

    /**
     * 心跳检测 30秒
     */
    @Scheduled(cron = "0/30 * * * * ?")
    public void check30Sec() {
        // 如果对象全部空了
        if (CollUtil.isEmpty(WsClient.session)) {
            // 重建
            WsClient.start(null);
            try {
                // 放入频道
                String elves = RedisUtil.get(Const.ELVES_MAME);
                // 精灵自己的频道
                String uri = "wss://fishpi.cn/user-channel?apiKey=" + Fish.getKey();
                // 建立连接
                WebSocketContainer container = ContainerProvider.getWebSocketContainer();
                WsClient.session.put(elves, Pair.of(DateUtil.maxTime(), container.connectToServer(new UserChat(elves), URI.create(uri))));
            } catch (Exception e) {
                log.info("精灵 建立 ws 链接失败了...{}", e.getMessage());
            }
            return;
        }
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
                try {
                    // 没有过期 发送心跳
                    etValue.getValue().getBasicRemote().sendPing(StandardCharsets.UTF_8.encode("-hb-"));
                    // 发送后回写
                    temp.put(sKey, etValue);
                } catch (Exception e) {
                    log.info("{} session 心跳发送失败...{}", sKey, e.getMessage());
                }
            } else {
                // 关闭连接
                try {
                    etValue.getValue().close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "超时清理"));
                } catch (Exception e) {
                    log.info("{} session 关闭失败...{}", sKey, e.getMessage());
                }
            }
        }
        // 回写
        WsClient.session = temp;
    }

    /**
     * 三分钟一次
     */
    @Scheduled(cron = "0 0/3 * * * ?")
    public void check3min() {
        // 记录红包
        taskService.recordRpLog();
    }

    /**
     * 五分钟一次
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void check5min() {
        // 迎新
        taskService.welcomeV1();
        // 新人报道
        taskService.runCheckV1();
        // 精灵最后一次发言
        String s = RedisUtil.get("LAST:WORD");
        if (StringUtils.isBlank(s)) {
            RedisUtil.set("LAST:WORD", DateUtil.nowStr());
        } else {
            // 最后一次发言
            LocalDateTime last = DateUtil.parseLdt(s);
            // 断言
            assert last != null;
            // 三小时精灵没说话了, 说句话
            if (last.isBefore(LocalDateTime.now().minusHours(3))) {
                switch (new Random().nextInt(3)) {
                    case 0:
                        Fish.sendMsg("数据收集中...完成度 " + (new Random().nextInt(101)) + " %");
                        break;
                    case 1:
                        Fish.sendMsg("风起的天气, 而我在远方想你~");
                        break;
                    case 2:
                        Fish.sendMsg("智障进化中...进化失败~");
                        break;
                    default:
                        Fish.sendMsg("诶嘿嘿, 防AFK发言. 毫无意义~");
                        break;

                }
            }
        }
    }

    /**
     * 鱼鱼标记赛
     */
    @Scheduled(cron = "0 0 8,10,12,14,16 * * ?")
    public void clockBiuFish() {
        LocalDateTime now = LocalDateTime.now();
        // 开始预告
        Fish.sendMsg("### https://fishpi.cn/article/1685604556543 `" + DateUtil.formatDay(now.toLocalDate()) + "` 的 `" + now.getHour() + "点`赛 开始报名啦~ 渔民大人快冲呀!");
    }

    /**
     * 鱼鱼标记赛
     */
    @Scheduled(cron = "30 15 9,11,13,15,17 * * ?")
    public void biuFish() {
        LocalDateTime now = LocalDateTime.now();
        // 开始预告
        Fish.sendMsg("`鱼鱼标记赛`---`" + (now.getHour() - 1) + "点`赛 开始统计!");
        // 冰柜对象
        String potKey = "CR:GAME:BIU:JACKPOT";
        // 冰柜数量
        Integer jackpot = new BigDecimal(Optional.ofNullable(RedisUtil.get(potKey)).orElse("0")).multiply(new BigDecimal("0.8")).intValue();
        // 标记前缀
        String prefix = "CR:GAME:BIU:";
        // 标记对象
        StringBuilder record = new StringBuilder("`鱼鱼标记赛`---`" + (now.getHour() - 1) + "点`赛  参与详情:").append("\n\n");
        record.append("当前冰柜内鱼翅数量: ").append(jackpot).append("\n\n");
        // 今日玩家
        List<String> joinUsers = Lists.newArrayList();
        // 循环遍历
        for (int i = 1; i < 9; i++) {
            String tmp = "无";
            // 标记人
            String users = RedisUtil.get(prefix + i);
            if (StringUtils.isNotBlank(users)) {
                List<String> array = JSON.parseArray(users, String.class);
                // 放入今日玩家
                joinUsers.addAll(array);
                tmp = Strings.join(array, ',');
            }
            record.append("标记[").append(i).append("]号鱼鱼玩家: ").append(tmp).append("\n\n");
        }
        // 发送消息
        Fish.sendMsg(record.toString());
        // 随机获取鱼鱼
        Integer biu = Const.CHAT_ROOM_BIU_FISH.get(new SecureRandom().nextInt(Const.CHAT_ROOM_BIU_FISH.size()));
        // 开始预告
        Fish.sendMsg("biu~biu~biu~! 小精灵biu到了[ `" + biu + "` ]号鱼鱼~");
        // 没有人参与
        if (joinUsers.size() == 0) {
            Fish.sendMsg("`鱼鱼标记赛`---`" + (now.getHour() - 1) + "点`赛 结束, 很遗憾没有渔民参与~ 下次见啦!(冰柜鱼翅数量已累积, 当前:" + RedisUtil.get(potKey) + ")");
            return;
        }
        // 遍历私聊详情
        for (String u : joinUsers) {
            Fish.send2User(u, record.toString());
            Fish.send2User(u, "biu~biu~biu~! 小精灵biu到了[ `" + biu + "` ]号鱼鱼~");
        }
        // 标记人
        String biuUsers = RedisUtil.get(prefix + biu);
        if (StringUtils.isNotBlank(biuUsers)) {
            // 获胜玩家
            List<String> uList = JSON.parseArray(biuUsers, String.class);
            Fish.sendMsg("`鱼鱼标记赛`---`" + (now.getHour() - 1) + "点`赛 结束, 让我们恭喜玩家[" + Strings.join(uList, ',') + "](奖品稍后发放, 请注意查收私信!)~ 渔民们下次见啦!");
            // 礼物数量
            int gift = jackpot / uList.size();
            // 循环发奖
            for (String u : uList) {
                // 获取用户编号
                String uNo = Fish.getUserNo(u).toString();
                // 写入排行榜
                RedisUtil.incrScore(StrUtils.getKey(Const.RANKING_PREFIX, "8"), uNo, 1);
                RedisUtil.incrScore(StrUtils.getKey(Const.RANKING_PREFIX, "9"), uNo, gift);
                CurrencyService.sendCurrency(u, gift, "聊天室活动-鱼鱼标记赛-奖品(`" + (now.getHour() - 1) + "点`赛)");
            }
            // 清空奖池
            RedisUtil.set(potKey, "0");
        } else {
            // 下一次奖金
            int next = jackpot / 2;
            // 放入下一次 数量减半
            Fish.sendMsg("`鱼鱼标记赛`---`" + (now.getHour() - 1) + "点`赛 结束, 很遗憾没有渔民获胜~ 下次见啦!(冰柜鱼翅数量已累积, 当前:" + next + ")");
            // 清空奖池
            RedisUtil.set(potKey, String.valueOf(next));
        }
    }

    /**
     * 午间活动 片段雨
     */
    @Scheduled(cron = "0 30 11,17 * * ?")
    public void mcRain() {
        if (StringUtils.isBlank(RedisUtil.get(Const.CURRENCY_FREE_TIME))) {
            RedisUtil.set(Const.CURRENCY_FREE_TIME, "聊天室活动-天降鱼丸", 60);
            Fish.sendMsg("天降鱼丸, [0,10] 随机个数. 限时 1 min. 冲鸭~");
        } else {
            Fish.sendMsg("天降鱼丸开启中. 冲鸭~");
        }
    }

    /**
     * 每日活动 随机猜拳
     *
     * 暂停了. 没人玩儿
     */
    @Scheduled(cron = "0 30 9,10,15,16 * * ?")
    public void redPacket() {
        // Fish.sendRockPaperScissors(null, 64);
    }

    /**
     * 执法服务器状态 4 小时一次
     */
    @Scheduled(cron = "0 30 0/4 * * ?")
    public void zfServerState() {
        if (StringUtils.isBlank(RedisUtil.get(Const.PATROL_LIMIT_PREFIX + "FWQZT"))) {
            Fish.sendCMD("执法 服务器状态");
        }
    }

    /**
     * 执法维护 6 小时一次
     */
    @Scheduled(cron = "30 0 0/6 * * ?")
    public void zfWeiHu() {
        if (StringUtils.isBlank(RedisUtil.get(Const.PATROL_LIMIT_PREFIX + "WH"))) {
            Fish.sendCMD("执法 维护");
        }
    }

    /**
     * 执法维护 6 小时一次
     */
    @Scheduled(cron = "0 0 0/12 * * ?")
    public void zfRefresh() {
        if (StringUtils.isBlank(RedisUtil.get(Const.PATROL_LIMIT_PREFIX + "SXHC"))) {
            Fish.sendCMD("执法 刷新缓存");
        }
    }
}
