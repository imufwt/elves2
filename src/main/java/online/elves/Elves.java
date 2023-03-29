package online.elves;

import lombok.extern.slf4j.Slf4j;
import online.elves.config.Const;
import online.elves.third.fish.Fish;
import online.elves.utils.DateUtil;
import online.elves.utils.RedisUtil;
import online.elves.ws.WsClient;
import online.elves.ws.handler.UserChat;
import org.apache.commons.lang3.tuple.Pair;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;

/**
 * 启动类
 */
@Slf4j
@EnableScheduling
@AutoConfiguration
@SpringBootApplication
@MapperScan({"online.elves.mapper"})
public class Elves {
    
    /**
     * 主入口
     * @param args
     */
    public static void main(String[] args) throws DeploymentException, IOException {
        // 启动程序
        SpringApplication.run(Elves.class, args);
        // 激活链接
        WsClient.start(null);
        // 放入频道
        String elves = RedisUtil.get(Const.ELVES_MAME);
        // 精灵自己的频道
        String uri = "wss://fishpi.cn/user-channel?apiKey=" + Fish.getKey();
        // 建立连接
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        WsClient.session.put(elves, Pair.of(DateUtil.maxTime(), container.connectToServer(new UserChat(elves), URI.create(uri))));
    }
    
}