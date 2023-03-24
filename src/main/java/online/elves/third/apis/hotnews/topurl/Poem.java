package online.elves.third.apis.hotnews.topurl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 诗词欣赏
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Poem {
    
    private List<String> content;
    
    private String type;
    
    private String title;
    
    private String author;
    
}
