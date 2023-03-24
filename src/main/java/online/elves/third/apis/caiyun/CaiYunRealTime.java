package online.elves.third.apis.caiyun;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import online.elves.third.apis.caiyun.model.Realtime;

import java.util.Objects;

/**
 * 彩云实时天气数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaiYunRealTime {
    
    /**
     * 接口状态
     */
    private String status;
    
    /**
     * 接口版本
     */
    private String api_version;
    
    /**
     * 接口状态
     */
    private String api_status;
    
    /**
     * 语言模式
     */
    private String lang;
    
    /**
     * 单位类型
     */
    private String unit;
    
    /**
     * ??? 不晓得是啥
     */
    private Integer tzshift;
    
    /**
     * 时区
     */
    private String timezone;
    
    /**
     * 服务器时间
     */
    private Long server_time;
    
    /**
     * 经纬度
     */
    private Double[] location;
    
    /**
     * 结果对象
     */
    private Result result;
    
    /**
     * 接口是否成功
     * @return
     */
    public Boolean isSuc() {
        return this.getStatus().equals("ok") && Objects.nonNull(this.result) && Objects.nonNull(this.result.realtime) && this.result.realtime.getStatus().equals("ok");
    }
    
    /**
     * 结果对象
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Result {
        
        /**
         * 实时数据
         */
        private Realtime realtime;
        
        /**
         * 主键???
         */
        private Integer primary;
        
    }
    
}
