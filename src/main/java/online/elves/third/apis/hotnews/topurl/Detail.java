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
public class Detail {
    
    private String date;
    
    private String text_day;
    
    private String code_day;
    
    private String text_night;
    
    private String code_night;
    
    private String high;
    
    private String low;
    
    private String rainfall;
    
    private String precip;
    
    private String wind_direction;
    
    private String wind_direction_degree;
    
    private String wind_speed;
    
    private String wind_scale;
    
    private String humidity;
    
}
