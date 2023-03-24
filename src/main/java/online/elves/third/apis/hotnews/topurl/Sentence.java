package online.elves.third.apis.hotnews.topurl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 句子
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sentence {
    
    private boolean wrong;
    
    private String author;
    
    private String sentence;
}