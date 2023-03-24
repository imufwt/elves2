package online.elves.message.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 聊天室消息事件
 */
public class CrMsgEvent extends ApplicationEvent {
    
    /**
     * 用户编号
     */
    @Getter
    private Integer userNo;
    
    
    /**
     * 构造函数
     * @param userName
     * @param userNo
     */
    public CrMsgEvent(String userName, Integer userNo) {
        super(userName);
        this.userNo = userNo;
    }
    
}
