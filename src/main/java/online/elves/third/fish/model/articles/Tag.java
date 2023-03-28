package online.elves.third.fish.model.articles;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
/**
 * 文章标签.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tag {
    private Integer tagShowSideAd;
    private String tagIconPath;
    private Integer tagStatus;
    private Integer tagBadCnt;
    private Double tagRandomDouble;
    private String tagTitle;
    private boolean isReserved;
    private Long oId;
    private String tagURI;
    private String tagAd;
    private Integer tagGoodCnt;
    private String tagCSS;
    private Integer tagCommentCount;
    private String tagDescriptionText;
    private Integer tagFollowerCount;
    private List<TagRelatedTags> tagRelatedTags;
    private List<String> tagDomains;
    private String tagSeoTitle;
    private Integer tagLinkCount;
    private String tagSeoDesc;
    private Integer tagReferenceCount;
    private String tagSeoKeywords;
    private String tagDescription;
}
