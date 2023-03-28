package online.elves.third.fish.model.articles;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章标签对象.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleTagObjs {
    private Integer tagShowSideAd;
    private String tagIconPath;
    private Integer tagStatus;
    private Integer tagBadCnt;
    private Double tagRandomDouble;
    private String tagTitle;
    private Long oId;
    private String tagURI;
    private String tagAd;
    private Integer tagGoodCnt;
    private String tagCSS;
    private Integer tagCommentCount;
    private Integer tagFollowerCount;
    private String tagSeoTitle;
    private Integer tagLinkCount;
    private String tagSeoDesc;
    private Integer tagReferenceCount;
    private String tagSeoKeywords;
    private String tagDescription;
}
