package online.elves.third.apis.hotnews.topurl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 成语
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Phrase {
    
    private String phrase;
    
    private String explain;
    
    private String from;
    
    private String example;
    
    private String simple;
    
    private String pinyin;
    
}
