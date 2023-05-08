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
    // 红包类型 random(拼手气红包), average(平分红包)，specify(专属红包)，heartbeat(心跳红包)，rockPaperScissors(猜拳红包)
    private String type;

    private int got;

    private List<String> who;

    /**
     * 转义
     *
     * @return
     */
    public int tfType() {
        switch (type) {
            case "random":
                return 1;
            case "average":
                return 2;
            case "specify":
                return 3;
            case "heartbeat":
                return 4;
            case "rockPaperScissors":
                return 5;
            default:
                return -1;
        }
    }
}
