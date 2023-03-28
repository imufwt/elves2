package online.elves.third.fish.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import online.elves.third.fish.model.articles.Articles;

import java.util.List;
import java.util.Objects;

/**
 * 鱼排响应公共对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FResp {
    
    /**
     * 返回信息
     */
    private String msg;
    
    /**
     * 响应编码
     */
    private Integer code;
    
    /**
     * 返回对象
     */
    private Object data;
    
    /**
     * 让人惊奇的返回
     */
    private String Key;
    
    /**
     * 昨日奖励 -1 已领取
     */
    private int sum;
    
    /**
     * 接口响应
     * @return
     */
    public boolean isOk() {
        return Objects.nonNull(code) && code == 0;
    }
    
}
