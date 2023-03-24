package online.elves.ws.handler;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import online.elves.message.Publisher;
import online.elves.message.event.CrEvent;
import online.elves.message.model.CrMsg;
import online.elves.utils.DateUtil;
import online.elves.ws.WsClient;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;

/**
 * 客户端
 */
@Slf4j
@Component
@ClientEndpoint
public class ChatRoom {
    
    /**
     * 服务接口
     */
    private static Publisher publisher;
    
    /**
     * 方法注入
     * @param publisher
     */
    @Autowired
    public ChatRoom(Publisher publisher) {
        this.publisher = publisher;
    }
    
    /**
     * 默认对象
     */
    public ChatRoom() {
    }
    
    @OnOpen
    public void onOpen(Session session) {
        WsClient.session.put(WsClient.SpecKey, Pair.of(DateUtil.maxTime(), session));
    }
    
    @OnMessage
    public void processMassage(String message) {
        try {
            // 接收到消息
            CrMsg crMsg = JSON.parseObject(message, CrMsg.class);
            // 发送出去
            publisher.send(new CrEvent(crMsg.getType(), crMsg));
        } catch (Exception e) {
            log.info("聊天室消息...处理异常...{}", e.getMessage());
            e.printStackTrace();
        }
        
    }
    
    @OnError
    public void processError(Throwable t) {
        log.info("聊天室异常中断...{}", t.getMessage());
    }
    
    @OnClose
    public void processClose(Session session, CloseReason closeReason) {
        log.warn("聊天室链接({})被关闭({})...{}", session.getId(), closeReason.getCloseCode().getCode(), closeReason.getReasonPhrase());
    }
    
}
