package online.elves.third.apis.hotnews.topurl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 新闻对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class News {
    
    /**
     * 接口信息
     */
    private String _intro;
    
    /**
     * 日历
     */
    private Calendar calendar;
    
    /**
     * 历史上的今天
     */
    private List<HistoryList> historyList;
    
    /**
     * 新闻列表
     */
    private List<NewsList> newsList;
    
    /**
     * 成语对象
     */
    private Phrase phrase;
    
    /**
     * 每日一句
     */
    private Sentence sentence;
    
    /**
     * 每日诗
     */
    private Poem poem;
    
    /**
     * 天气
     */
    private Weather weather;
    
    /**
     * ???
     */
    private boolean deadline;
    
    /**
     * 什么玩意儿?
     */
    private String code;
    
}
