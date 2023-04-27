package online.elves.ws;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import online.elves.third.fish.Fish;
import online.elves.utils.DateUtil;
import online.elves.ws.handler.Chat;
import online.elves.ws.handler.ChatRoom;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * wss 客户端
 */
@Slf4j
@Component
public class WsClient {
    
    /**
     * 会话
     */
    public static Map<String, Pair<LocalDateTime, Session>> session = Maps.newHashMapWithExpectedSize(128);
    
    /**
     * 聊天室特殊key
     */
    public static String SpecKey = "session.elves.online";
    
    /**
     * 开启
     */
    public static void start(String user) {
        boolean def = false;
        if (StringUtils.isBlank(user)) {
            def = true;
            user = SpecKey;
        }
        if (CollUtil.isNotEmpty(session)) {
            Pair<LocalDateTime, Session> sessionPair = session.get(user);
            if (Objects.nonNull(sessionPair)) {
                try {
                    sessionPair.getValue().close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "初始化链接, 关闭"));
                    // 移除
                    session.remove(user);
                } catch (IOException e) {
                    log.info("链接初始化关闭失败...{}", user);
                }
            }
        }
        try {
            // 建立连接
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            // 最大消息为 10M 貌似鱼排天然限制了大小, 这个可有可无了
            container.setDefaultMaxBinaryMessageBufferSize(10 * 1024 * 1024);
            container.setDefaultMaxTextMessageBufferSize(10 * 1024 * 1024);
            //apiKey
            String key = Fish.getKey();
            // 2099 天荒地老
            LocalDateTime maxTime = DateUtil.maxTime();
            // 分情况建立连接
            if (def) {
                // 客户端开启
                String uri = "wss://fishpi.cn/chat-room-channel?apiKey=" + key;
                // 加入连接
                session.put(user, Pair.of(maxTime, container.connectToServer(ChatRoom.class, URI.create(uri))));
                // 开启监听
                Fish.sendMsg("诶嘿嘿, 小精灵上线啦~ (#^.^#) \n\n > 如果我多次重连, 请BAN了我, 然后艾特下我老板. 谢谢!");
            } else {
                // 客户端开启
                String uri = "wss://fishpi.cn/chat-channel?apiKey=" + key + "&toUser=" + user;
                // 建立的连接
                session.put(user, Pair.of(LocalDateTime.now(), container.connectToServer(new Chat(user), URI.create(uri))));
            }
        } catch (Exception e) {
            log.warn("客户端开启失败...{}", e.getMessage());
        }
    }
    
}
