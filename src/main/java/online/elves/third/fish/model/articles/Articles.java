package online.elves.third.fish.model.articles;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 文章.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Articles {
    private Integer articleShowInList;
    private String articleCreateTime;
    private String articleAuthorId;
    private Integer articleBadCnt;
    private List<ArticleParticipants> articleParticipants;
    private String articleLatestCmtTime;
    private Integer articleGoodCnt;
    private Integer articleQnAOfferPoInteger;
    private String articleThumbnailURL;
    private Integer articleStickRemains;
    private String timeAgo;
    private String articleUpdateTimeStr;
    private String articleAuthorName;
    private Integer articleType;
    private boolean offered;
    private String articleCreateTimeStr;
    private Integer articleViewCount;
    private String articleAuthorThumbnailURL20;
    private Integer articleWatchCnt;
    private String articlePreviewContent;
    private String articleTitleEmoj;
    private String articleTitleEmojUnicode;
    private String articleAuthorThumbnailURL48;
    private Integer articleCommentCount;
    private Integer articleCollectCnt;
    private String articleTitle;
    private String articleLatestCmterName;
    private String articleTags;
    private Long oId;
    private String cmtTimeAgo;
    private Integer articleStick;
    private List<ArticleTagObjs> articleTagObjs;
    private String articleLatestCmtTimeStr;
    private Integer articleAnonymous;
    private Integer articleThankCnt;
    private String articleUpdateTime;
    private Integer articleStatus;
    private Integer articleHeat;
    private Integer articlePerfect;
    private String articleAuthorThumbnailURL210;
    private String articlePermalink;
    private ArticleAuthor articleAuthor;
}
