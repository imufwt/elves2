package online.elves.message.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 聊天室命令事件
 *
 * 合议庭
 */
public class CollegiateBenchEvent extends ApplicationEvent {

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
    public CollegiateBenchEvent(String userName, String cmd) {
        super(userName);
        this.cmd = cmd;
    }
    
}
