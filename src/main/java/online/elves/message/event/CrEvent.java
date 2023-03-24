package online.elves.message.event;

import lombok.*;
import online.elves.message.model.CrMsg;
import org.springframework.context.ApplicationEvent;

/**
 * 聊天室消息事件
 */
public class CrEvent extends ApplicationEvent {
    
    /**
     * 状态对象
     */
    @Getter
    private CrMsg crMsg;
    
    
    /**
     * 构造函数
     * @param msgType
     * @param crMsg
     */
    public CrEvent(String msgType, CrMsg crMsg) {
        super(msgType);
        this.crMsg = crMsg;
    }
}
