package online.elves.third.apis.hotnews.topurl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 新闻列表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsList {
    
    private String title;
    
    private String url;
    
    private String category;
    
}