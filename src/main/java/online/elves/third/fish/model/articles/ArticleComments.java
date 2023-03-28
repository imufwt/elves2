package online.elves.third.fish.model.articles;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import online.elves.third.fish.model.user.SysMetal;

import java.util.List;

/**
 * 评论
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleComments {
    private boolean commentNice;
    private String commentCreateTimeStr;
    private String commentAuthorId;
    private int commentScore;
    private String commentCreateTime;
    private String commentAuthorURL;
    private int commentVote;
    private int commentRevisionCount;
    private String timeAgo;
    private String commentOriginalCommentId;
    private List<SysMetal> sysMetal;
    private int commentGoodCnt;
    private int commentVisible;
    private String commentOnArticleId;
    private int rewardedCnt;
    private String commentSharpURL;
    private int commentAnonymous;
    private int commentReplyCnt;
    private Long oId;
    private String commentContent;
    private int commentStatus;
    private Commenter commenter;
    private String commentAuthorName;
    private int commentThankCnt;
    private int commentBadCnt;
    private boolean rewarded;
    private String commentAuthorThumbnailURL;
    private String commentAudioURL;
    private int commentQnAOffered;
}
