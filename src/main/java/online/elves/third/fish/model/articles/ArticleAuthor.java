package online.elves.third.fish.model.articles;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章作者.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleAuthor {
    private boolean userOnlineFlag;
    private Integer onlineMinute;
    private Integer userPoIntegerStatus;
    private Integer userFollowerStatus;
    private Integer userGuideStep;
    private Integer userOnlineStatus;
    private Long userCurrentCheckinStreakStart;
    private Integer chatRoomPictureStatus;
    private String userTags;
    private Integer userCommentStatus;
    private String userTimezone;
    private String userURL;
    private Integer userForwardPageStatus;
    private Integer userUAStatus;
    private String userIndexRedirectURL;
    private Long userLatestArticleTime;
    private Integer userTagCount;
    private String userNickname;
    private Integer userListViewMode;
    private Integer userLongestCheckinStreak;
    private Integer userAvatarType;
    private Long userSubMailSendTime;
    private Long userUpdateTime;
    private Integer userSubMailStatus;
    private Integer userJoinPoIntegerRank;
    private Long userLatestLoginTime;
    private Integer userAppRole;
    private Integer userAvatarViewMode;
    private Integer userStatus;
    private Long userLongestCheckinStreakEnd;
    private Integer userWatchingArticleStatus;
    private Long userLatestCmtTime;
    private String userProvince;
    private Integer userCurrentCheckinStreak;
    private Integer userNo;
    private String userAvatarURL;
    private Integer userFollowingTagStatus;
    private String userLanguage;
    private Integer userJoinUsedPoIntegerRank;
    private Long userCurrentCheckinStreakEnd;
    private Integer userFollowingArticleStatus;
    private Integer userKeyboardShortcutsStatus;
    private Integer userReplyWatchArticleStatus;
    private Integer userCommentViewMode;
    private Integer userBreezemoonStatus;
    private Long userCheckinTime;
    private Integer userUsedPoInteger;
    private Integer userArticleStatus;
    private Integer userPoInteger;
    private Integer userCommentCount;
    private String userIntro;
    private String userMobileSkin;
    private Integer userListPageSize;
    private Long oId;
    private String userName;
    private Integer userGeoStatus;
    private Long userLongestCheckinStreakStart;
    private String userSkin;
    private Integer userNotifyStatus;
    private Integer userFollowingUserStatus;
    private Integer userArticleCount;
    private String userRole;
}
