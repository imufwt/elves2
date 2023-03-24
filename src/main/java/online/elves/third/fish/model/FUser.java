package online.elves.third.fish.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 鱼排用户信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FUser {
    
    private String userCity;
    
    private boolean userOnlineFlag;
    
    private long userPoint;
    
    private String userAppRole;
    
    private String userIntro;
    
    private Integer userNo;
    
    private int onlineMinute;
    
    private String userAvatarURL;
    
    private String userNickname;
    
    private Long oId;
    
    private String userName;
    
    private String cardBg;
    
    private String allMetalOwned;
    
    private int followingUserCount;
    
    private String userAvatarURL20;
    
    private String sysMetal;
    
    private String canFollow;
    
    private String userRole;
    
    private String userAvatarURL210;
    
    private int followerCount;
    
    private String userURL;
    
    private String userAvatarURL48;
    
}
