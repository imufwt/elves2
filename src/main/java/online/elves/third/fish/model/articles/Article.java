package online.elves.third.fish.model.articles;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 文章
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Article {
    private String articleCreateTime;
    private boolean discussionViewable;
    private String articleToC;
    private int thankedCnt;
    private List<ArticleComments> articleComments;
    private int articleRewardPoint;
    private int articleRevisionCount;
    private String articleLatestCmtTime;
    private String articleThumbnailURL;
    private String articleAuthorName;
    private int articleType;
    private String articleCreateTimeStr;
    private int articleViewCount;
    private boolean articleCommentable;
    private String articleAuthorThumbnailURL20;
    private String articleOriginalContent;
    private String articlePreviewContent;
    private String articleContent;
    private String articleAuthorIntro;
    private int articleCommentCount;
    private int rewardedCnt;
    private String articleLatestCmterName;
    private int articleAnonymousView;
    private String cmtTimeAgo;
    private String articleLatestCmtTimeStr;
    private List<String> articleNiceComments;
    private boolean rewarded;
    private int articleHeat;
    private int articlePerfect;
    private String articleAuthorThumbnailURL210;
    private String articlePermalink;
    private String articleCity;
    private int articleShowInList;
    private boolean isMyArticle;
    private String articleIP;
    private int articleEditorType;
    private int articleVote;
    private double articleRandomDouble;
    private String articleAuthorId;
    private int articleBadCnt;
    private String articleAuthorURL;
    private boolean isWatching;
    private int articleGoodCnt;
    private int articleQnAOfferPoint;
    private long articleStickRemains;
    private String timeAgo;
    private String articleUpdateTimeStr;
    private boolean offered;
    private int articleWatchCnt;
    private String articleTitleEmoj;
    private String articleTitleEmojUnicode;
    private String articleAudioURL;
    private String articleAuthorThumbnailURL48;
    private boolean thanked;
    private String articleImg1URL;
    private int articlePushOrder;
    private int articleCollectCnt;
    private String articleTitle;
    private boolean isFollowing;
    private String articleTags;
    private Long oId;
    private long articleStick;
    private List<ArticleTagObjs> articleTagObjs;
    private int articleAnonymous;
    private int articleThankCnt;
    private String articleRewardContent;
    private int redditScore;
    private String articleUpdateTime;
    private int articleStatus;
    private ArticleAuthor articleAuthor;
}
