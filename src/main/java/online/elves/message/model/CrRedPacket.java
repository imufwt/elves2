package online.elves.message.model;

import lombok.Data;

import java.util.List;

/**
 * 聊天室红包
 */
@Data
public class CrRedPacket {
    
    private String msg;
    
    private String recivers;
    
    private String senderId;
    
    private String msgType;
    
    private int money;
    
    private int count;
    
    private String type;
    
    private int got;
    
    private List<String> who;
    
}
