package online.elves.ws.handler;

import lombok.extern.slf4j.Slf4j;
import online.elves.ws.WsClient;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import java.time.LocalDateTime;

/**
 * 私聊-用户
 */
@Slf4j
@Component
@ClientEndpoint
public class Chat {
    
    /**
     * 用户对象
     */
    private String user;
    
    /**
     * 无参构造
     */
    public Chat() {
    
    }
    
    /**
     * 构造器
     */
    public Chat(String user) {
        this.user = user;
    }
    
    @OnOpen
    public void onOpen(Session session) {
        // 建立的连接
        WsClient.session.put(user, Pair.of(LocalDateTime.now(), session));
    }
    
    @OnMessage
    public void processMassage(String message) {
        // 接到消息... 配合精灵私信... 没啥用~
    }
    
    @OnError
    public void processError(Throwable t) {
        log.info("用户私聊异常中断...{}", t.getMessage());
    }
    
    @OnClose
    public void processClose(Session session, CloseReason closeReason) {
        log.warn("用户私聊链接({})被关闭({})...{}", session.getId(), closeReason.getCloseCode().getCode(), closeReason.getReasonPhrase());
    }
    
}
