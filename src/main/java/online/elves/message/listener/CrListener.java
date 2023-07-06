package online.elves.message.listener;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import online.elves.config.Const;
import online.elves.message.event.CrEvent;
import online.elves.message.model.CrMsg;
import online.elves.message.model.CrRedPacket;
import online.elves.service.FService;
import online.elves.third.fish.Fish;
import online.elves.utils.DateUtil;
import online.elves.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 聊天室消息监听
 */
@Slf4j
@Component
public class CrListener {

    @Resource
    FService fService;

    @EventListener(classes = {CrEvent.class})
    public void exec(CrEvent event) {
        // 事件消息
        CrMsg crMsg = event.getCrMsg();
        // 消息原始内容
        String content = crMsg.getContent();
        // 消息
        String md = crMsg.getMd();
        // 昵称
        String userNickname = crMsg.getUserNickname();
        // 用户名
        String userName = crMsg.getUserName();
        // 消息类型
        switch (event.getSource().toString()) {
            case "msg":
                // 消息 ID
                Long oId = Long.valueOf(crMsg.getOId());
                if (userNickname.contains("摸鱼派官方巡逻机器人") || userName.contains("摸鱼派官方巡逻机器人")) {
                    log.info("人工智障说: {}", md);
                    if (md.contains("用户会话详情")) {
                        List<String> x = Arrays.asList(md.split("<summary>用户会话详情</summary>")[1].split("<br></details>")[0].split("<br>"));
                        // 需要断开的用户
                        List<String> needUser = Lists.newArrayList();
                        for (String z : x) {
                            String[] split = z.split(" ");
                            String currUser = split[0];
                            log.info("用户[{}]当前连接数[{}]", currUser, split[1]);
                            // 当前连接数大于等于3
                            if (Integer.parseInt(split[1]) > Integer.parseInt(Objects.requireNonNull(RedisUtil.get("CMD:DEVICE:LIMIT")))) {
                                // 加入信息
                                needUser.add(currUser);
                                // 两位数加减法
                                int a = new SecureRandom().nextInt(10), b = new SecureRandom().nextInt(10);
                                // 符号
                                boolean symbol = a >= b;
                                // 结果
                                int res;
                                if (symbol) {
                                    res = a - b;
                                } else {
                                    res = a + b;
                                }
                                // 写入redis
                                RedisUtil.set("ONLINE:JUDGE:" + currUser, String.valueOf(res), 30);
                                // 需要发送的信息
                                StringBuilder str = new StringBuilder("## ⚠️设备数过载预警⚠️").append("\n\n");
                                str.append("> ### `").append(switchNum(a)).append(" ").append(symbol ? "-" : "+").append(" ").append(switchNum(b)).append(" = ?` ❤️").append("\n\n");
                                str.append("---- ").append("\n\n");
                                str.append(" 亲爱的 @").append(currUser).append(" 你当前连接数是[ **").append(split[1]).append("** ] ").append("\n\n");
                                str.append("📢 请在`30s`内在>[聊天室](https://fishpi.cn/cr)<直接回复上述问题答案, 否则将被 **断开会话**! ").append("\n\n");
                                // 发送结果
                                if (!Objects.requireNonNull(RedisUtil.get(Const.OP_LIST)).contains(currUser)) {
                                    // 发送命令
                                    Fish.sendCMD(str.toString());
                                }
                                Fish.send2User(currUser, str.toString());
                            }
                        }
                        // 不为空才写回去
                        if (CollUtil.isNotEmpty(needUser)) {
                            RedisUtil.set("ONLINE:JUDGE", JSON.toJSONString(needUser), 60);
                        }
                    }
                    break;
                }
                if (StringUtils.isBlank(md)) {
                    // 转义红包对象
                    CrRedPacket crRedPacket = JSON.parseObject(content, CrRedPacket.class);
                    // 获取消息类型
                    String msgType = crRedPacket.getMsgType();
                    if (StringUtils.isNotBlank(msgType) && Objects.equals("redPacket", msgType)) {
                        // 红包类型
                        String type = crRedPacket.getType();
                        // 打印消息
                        log.info("接收到一个红包消息, {}({})在聊天室发送了一个[{}]红包", userNickname, userName, type);
                        // 红包金额
                        Integer money = crRedPacket.getMoney();
                        // 是专属 且是精灵的 且红包金额大于 31. 防止负数积分 ... 嘿嘿
                        if ("specify".equals(type) && crRedPacket.getRecivers().contains(Objects.requireNonNull(RedisUtil.get(Const.ELVES_MAME))) && money > 31) {
                            // 购买鱼翅
                            fService.recordCurrency(oId, userName, money);
                        }
                        // 猜拳锁
                        if ("rockPaperScissors".equals(type) && DateUtil.isRpsLock()) {
                            RedisUtil.set("CR:RPS:LOCK", DateUtil.nowStr(), 30);
                        }
                        // 保存红包记录
                        fService.recordRp(oId, userName, money, crRedPacket.tfType());
                    }
                    // 记录消息
                    fService.recMsg(userName, oId, md, content, false);
                } else {
                    log.info("接收到聊天室消息...{}({}) 说: {}", userNickname, userName, md);
                    // online 判定
                    String judge = RedisUtil.get("ONLINE:JUDGE:" + userName);
                    // 判定通过
                    String judgePass = RedisUtil.get("ONLINE:JUDGE:PASS:" + userName);
                    // 存在 且尚未回答正确.
                    if (StringUtils.isNotBlank(judge) && StringUtils.isBlank(judgePass)) {
                        if (md.contains(judge)) {
                            Fish.sendMsg("@" + userName + " 验证成功，请注意检查连接情况~\n\n> 最好不要开启太多会话哦~(请尽量小于等于`" + Objects.requireNonNull(RedisUtil.get("CMD:DEVICE:LIMIT")) + "`个)");
                            //  回答正确延长60
                            RedisUtil.set("ONLINE:JUDGE:" + userName, judge, 60);
                            // 通过了
                            RedisUtil.set("ONLINE:JUDGE:PASS:" + userName, judge, 60);
                        } else {
                            Fish.sendMsg("@" + userName + " 很遗憾, 回答错误.抓紧时间哦~");
                        }
                    }
                    // 记录消息
                    fService.recMsg(userName, oId, md, content, true);
                }
                break;
            case "barrager":
                // 弹幕
                String barragerContent = crMsg.getBarragerContent();
                log.info("接收到聊天室弹幕消息...{}({}) 说: {}", userNickname, userName, barragerContent);
                // 记录消息
                fService.recMsg(userName, System.currentTimeMillis(), "弹幕-20230426163907", barragerContent, true);
                break;
            case "discussChanged":
                // 话题变更
                break;
            case "redPacketStatus":
                // 红包状态变更
                log.info("接收到聊天室消息...{} 抢 到了 {} 发送的红包", crMsg.getWhoGot(), crMsg.getWhoGive());
                // 入库
                fService.recordRpOpenLog(crMsg.getOId(), crMsg.getWhoGot(), crMsg.getWhoGive());
                break;
            case "online":
                // 在线状态
                log.info("聊天室主题...{}... 当前在线...{}", crMsg.getDiscussing(), crMsg.getOnlineChatCnt());
                // 领奖
                Fish.getAward();
                break;
            case "revoke":
                // 撤回
                log.info("消息...{}...被撤回", Long.valueOf(crMsg.getOId()));
                break;
            case "all":
            default:
                log.info("接收到聊天室消息...{}", JSON.toJSONString(event));
                break;
        }
    }

    private static String switchNum(int x) {
        switch (new SecureRandom().nextInt(3)) {
            case 0:
                return ten[x];
            case 1:
                return ten_1[x];
            case 2:
            default:
                return ten_2[x];
        }
    }

    private static String[] ten = new String[]{"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
    private static String[] ten_1 = new String[]{"〇", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
    private static String[] ten_2 = new String[]{"0️⃣", "1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣"};
}