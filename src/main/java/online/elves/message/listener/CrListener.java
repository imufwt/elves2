package online.elves.message.listener;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import online.elves.config.Const;
import online.elves.message.event.CrEvent;
import online.elves.message.model.CrMsg;
import online.elves.message.model.CrRedPacket;
import online.elves.service.FService;
import online.elves.third.fish.Fish;
import online.elves.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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
                            // 购买神秘代码
                            fService.recordMysteryCode(oId, userName, money);
                        }
                    }
                    // 记录消息
                    fService.recMsg(userName, oId, md, content, false);
                } else {
                    log.info("接收到聊天室消息...{}({}) 说: {}", userNickname, userName, md);
                    // 记录消息
                    fService.recMsg(userName, oId, md, content, true);
                }
                break;
            case "discussChanged":
                // 话题变更
                break;
            case "redPacketStatus":
                // 红包状态变更
                log.info("接收到聊天室消息...{} 抢 到了 {} 发送的红包", crMsg.getWhoGot(), crMsg.getWhoGive());
                //                service.getRedPacket(msg.getString("whoGot"), msg.getString("whoGive"), oId);
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
    
}