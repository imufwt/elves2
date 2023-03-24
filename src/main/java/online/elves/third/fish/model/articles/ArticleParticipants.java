package online.elves.third.fish.model.articles;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章参与人.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleParticipants {
    private String articleParticipantURL;
    private String commentId;
    private String oId;
    private String articleParticipantName;
    private String articleParticipantThumbnailURL;
}
