package online.elves.third.apis.caiyun.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import online.elves.third.apis.caiyun.enums.SkyCon;

/**
 * 实时天气数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Realtime {
    
    /**
     * 数据状态
     */
    private String status;
    
    /**
     * 地表两米气温
     */
    private int temperature;
    
    /**
     * 地表两米相对湿度 %
     */
    private double humidity;
    
    /**
     * 总云量 0.0-1.0
     */
    private int cloudrate;
    
    /**
     * 天气现象
     * @see SkyCon
     */
    private String skycon;
    
    /**
     * 地表水平能见度
     */
    private double visibility;
    
    /**
     * 向下短波辐射通量(W/M2)
     */
    private double dswrf;
    
    /**
     * 风
     */
    private Wind wind;
    
    /**
     * 地面气压
     */
    private double pressure;
    
    /**
     * 体感温度
     */
    private double apparent_temperature;
    
    /**
     * 降水描述
     */
    private Precipitation precipitation;
    
    /**
     * 空气质量
     */
    private AirQuality air_quality;
    
    /**
     * 生活指数
     */
    private LifeIndex life_index;
    
    /**
     * ???
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LifeIndex {
    
        /**
         * 紫外线
         */
        private Ultraviolet ultraviolet;
    
        /**
         * 舒适度
         */
        private Comfort comfort;
    }
    
    /**
     * 舒适度
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Comfort {
    
        /**
         * 等级
         */
        private int index;
    
        /**
         * 描述
         */
        private String desc;
    }
    
    /**
     * 紫外线
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Ultraviolet {
    
        /**
         * 等级
         */
        private int index;
    
        /**
         * 描述
         */
        private String desc;
    }
    
    /**
     * 空气质量
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AirQuality {
    
        /**
         * PM25 浓度(μg/m3)
         */
        private int pm25;
    
        /**
         * PM10 浓度(μg/m3)
         */
        private int pm10;
    
        /**
         * 臭氧浓度(μg/m3)
         */
        private int o3;
    
        /**
         * 二氧化氮浓度(μg/m3)
         */
        private int so2;
    
        /**
         * 二氧化硫浓度(μg/m3)
         */
        private int no2;
    
        /**
         * 一氧化碳浓度(mg/m3)
         */
        private double co;
    
        /**
         * 污染指数
         */
        private AQI aqi;
    
        /**
         * 污染描述
         */
        private AQIDesc description;
    }
    
    /**
     * 污染描述
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AQIDesc {
    
        /**
         * 国标 AQI
         */
        private String chn;
    
        /**
         * 美标 AQI
         */
        private String usa;
    }
    
    /**
     * 污染指数
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AQI {
    
        /**
         * 国标 AQI
         */
        private int chn;
    
        /**
         * 美标 AQI
         */
        private int usa;
    }
    
    /**
     * 降水信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Precipitation {
    
        /**
         * 本地降水强度
         */
        private Local local;
    
        /**
         * 最近降雨带
         */
        private Nearest nearest;
    }
    
    /**
     * 最近降雨带
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Nearest {
    
        /**
         * 状态
         */
        private String status;
    
        /**
         * 距离
         */
        private int distance;
    
        /**
         * 强度
         */
        private int intensity;
    }
    
    /**
     * 本地降水强度
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Local {
    
        /**
         * 状态
         */
        private String status;
    
        /**
         * 数据来源???
         */
        private String datasource;
    
        /**
         * 强度
         */
        private int intensity;
    }
    
    /**
     * 风
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Wind {
    
        /**
         * 地表十米风速
         */
        private double speed;
    
        /**
         * 地表十米风向
         */
        private int direction;
    }
}
