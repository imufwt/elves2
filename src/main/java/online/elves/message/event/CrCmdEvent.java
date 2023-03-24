package online.elves.message.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 聊天室命令事件
 */
public class CrCmdEvent extends ApplicationEvent {
    
    /**
     * 用户命令
     */
    @Getter
    private String cmd;
    
    
    /**
     * 构造函数
     * @param userName
     * @param cmd
     */
    public CrCmdEvent(String userName, String cmd) {
        super(userName);
        this.cmd = cmd;
    }
    
}
