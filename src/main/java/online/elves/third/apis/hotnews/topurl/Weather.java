package online.elves.third.apis.hotnews.topurl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 天气
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Weather {
    
    private String city;
    
    private String weatherOf;
    
    private Detail detail;
    
}