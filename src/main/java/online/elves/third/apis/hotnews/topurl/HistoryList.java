package online.elves.third.apis.hotnews.topurl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 历史上的今天
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryList {
    
    /**
     * 历史上的今天
     */
    private String event;
    
}
