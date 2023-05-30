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
     * 消息内容
     */
    @Getter
    private String md;
    /**
     * 消息id
     */
    @Getter
    private Long oid;

    
    /**
     * 构造函数
     * @param userName
     * @param userNo
     * @param md
     * @param oid
     */
    public CrMsgEvent(String userName, Integer userNo, String md, Long oid) {
        super(userName);
        this.userNo = userNo;
        this.md = md;
        this.oid = oid;
    }
    
}
