package online.elves.message.model;

import lombok.Data;

import java.util.List;

/**
 * 聊天室消息对象
 */
@Data
public class CrMsg {
    /*  基础字段 */
    /** 消息ID */
    private String oId;
    /** 昵称 */
    private String userNickname;
    /** 用户名 */
    private String userName;
    /** 消息类型 */
    private String type;
    /** 原始内容 */
    private String content;
    /** md文本 */
    private String md;
    /** 用户勋章 */
    private String sysMetal;
    /** 用户头像 */
    private String userAvatarURL;
    /** 用户头像 */
    private String userAvatarURL20;
    /** 用户头像 */
    private String userAvatarURL48;
    /** 用户头像 */
    private String userAvatarURL210;
    /** 消息记录时间 显示用 */
    private String time;
    
    /* 红包状态变更 start */

    /** 谁抢到 */
    private String whoGot;
    /** 谁发的 */
    private String whoGive;
    /** 红包个数 */
    private int count;
    /** 是否获得 */
    private int got;
    
    /* 撤回 start 没啥特殊的 */
    
    /* 话题变更 start */
    /** 修改人 */
    private String whoChanged;
    /** 新话题 */
    private String newDiscuss;
    
    /* 在线状态 start */

    /** 当前话题 */
    private String discussing;
    /** 在线人数 */
    private int onlineChatCnt;
    /** 在线用户列表 */
    private List<User> users;
    
    /**
     * 在线用户对象
     */
    @Data
    static class User {
        private String userName;
        private String homePage;
        private String userAvatarURL;
        private String userAvatarURL20;
        private String userAvatarURL48;
        private String userAvatarURL210;
        
    }
}
