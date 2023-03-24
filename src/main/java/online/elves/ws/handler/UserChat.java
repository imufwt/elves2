package online.elves.ws.handler;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import online.elves.config.Const;
import online.elves.third.fish.Fish;
import online.elves.utils.DateUtil;
import online.elves.utils.RedisUtil;
import online.elves.ws.WsClient;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import java.util.Objects;

/**
 * 私聊-精灵
 */
@Slf4j
@Component
@ClientEndpoint
public class UserChat {
    
    /**
     * 用户对象
     */
    private String user;
    
    /**
     * 无参构造
     */
    public UserChat() {
    
    }
    
    /**
     * 构造器
     */
    public UserChat(String user) {
        this.user = user;
    }
    
    @OnOpen
    public void onOpen(Session session) {
        // 建立的连接
        WsClient.session.put(RedisUtil.get(Const.ELVES_MAME), Pair.of(DateUtil.maxTime(), session));
    }
    
    @OnMessage
    public void processMassage(String message) {
        log.info("精灵收到私信...{}", message);
        // 精灵私信
        ElvesMsg elvesMsg = JSON.parseObject(message, ElvesMsg.class);
        if (elvesMsg.isMsg()) {
            Fish.send2User(elvesMsg.getSenderUserName(), "十分抱歉~ \n\n 我是智障助理 **小精灵** , 无法识别您的问题. \n\n 请移步 [->聊天室<-](https://fishpi.cn/cr) 来跟我进行互动吧~ \n\n > Tips: 本通知及其余通知皆为自动发送, 请勿回复~");
        }
    }
    
    @OnError
    public void processError(Throwable t) {
        log.info("私聊异常中断...", t.getMessage());
    }
    
    @OnClose
    public void processClose(Session session, CloseReason closeReason) {
        log.warn("私聊链接({})被关闭({})...{}", session.getId(), closeReason.getCloseCode().getCode(), closeReason.getReasonPhrase());
    }
    
    /**
     * 精灵私信
     */
    @Data
    static class ElvesMsg {
        
        /**
         * 未读私信数量
         */
        private Integer count;
        
        /**
         * 私信用户ID
         */
        private String userId;
        
        /**
         * 命令模式
         * chatUnreadCountRefresh  未读
         * newIdleChatMessage  新消息
         */
        private String command;
        
        /**
         * 发送人用户名
         */
        private String senderUserName;
        
        /**
         * 发送人的头像
         */
        private String senderAvatar;
        
        /**
         * 预览消息
         */
        private String preview;
        
        /**
         * 是否是消息
         * @return
         */
        public boolean isMsg() {
            return Objects.equals(command, "newIdleChatMessage");
        }
        
    }
    
}
