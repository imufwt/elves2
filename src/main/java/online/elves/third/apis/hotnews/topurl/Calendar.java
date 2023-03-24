package online.elves.third.apis.hotnews.topurl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 日历
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Calendar {
    
    /* 阴历年月日 数字 */
    private int lYear;
    
    private int lMonth;
    
    private int lDay;
    
    /* 属相 */
    private String animal;
    
    /* 阴历年月日 汉字 */
    private String yearCn;
    
    private String monthCn;
    
    private String dayCn;
    
    /* 阳历年月日 汉字 */
    private int cYear;
    
    private int cMonth;
    
    private int cDay;
    
    /* 天干地支年月日 */
    private String gzYear;
    
    private String gzMonth;
    
    private String gzDay;
    
    private boolean isToday;
    
    private boolean isLeap;
    
    /**
     * 一年的第几周
     */
    private int nWeek;
    
    /**
     * 星期几
     */
    private String ncWeek;
    
    private boolean isTerm;
    
    private String term;
    
}
