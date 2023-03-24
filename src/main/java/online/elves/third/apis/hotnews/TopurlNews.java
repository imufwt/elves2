package online.elves.third.apis.hotnews;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import online.elves.third.apis.hotnews.topurl.News;

/**
 * 新闻快捷.
 * https://news.topurl.cn/api
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopurlNews {
    
    /**
     * 响应code
     */
    private int code;
    
    /**
     * 返回信息
     */
    private String message;
    
    /**
     * 新闻对象
     */
    private News data;
}
