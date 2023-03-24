package online.elves.third.apis.caiyun.enums;

import lombok.Getter;

/**
 * 天气现象
 */
@Getter
public enum SkyCon {
    
    CLEAR_DAY("晴（白天）", "CLEAR_DAY", " cloudrate < 0.2"),
    CLEAR_NIGHT("晴（夜间）", "CLEAR_NIGHT", " cloudrate < 0.2"),
    PARTLY_CLOUDY_DAY("多云（白天）", "PARTLY_CLOUDY_DAY", " 0.8 >= cloudrate > 0.2"),
    PARTLY_CLOUDY_NIGHT("多云（夜间）", "PARTLY_CLOUDY_NIGHT", " 0.8 >= cloudrate > 0.2"),
    CLOUDY("阴", "CLOUDY", "  cloudrate > 0.8"),
    LIGHT_HAZE("轻度雾霾", "LIGHT_HAZE", "  PM2.5 100~150"),
    MODERATE_HAZE("中度雾霾", "MODERATE_HAZE", " PM2.5 150~200"),
    HEAVY_HAZE("重度雾霾", "HEAVY_HAZE", "  PM2.5 > 200"),
    LIGHT_RAIN("小雨", "LIGHT_RAIN", "  见 降水强度"),
    MODERATE_RAIN("中雨", "MODERATE_RAIN", " 见 降水强度"),
    HEAVY_RAIN("大雨", "HEAVY_RAIN", "  见 降水强度"),
    STORM_RAIN("暴雨", "STORM_RAIN", "  见 降水强度"),
    FOG("雾", "FOG", "能见度低，湿度高，风速低，温度低"),
    LIGHT_SNOW("小雪", "LIGHT_SNOW", "  见 降水强度"),
    MODERATE_SNOW("中雪", "MODERATE_SNOW", " 见 降水强度"),
    HEAVY_SNOW("大雪", "HEAVY_SNOW", "  见 降水强度"),
    STORM_SNOW("暴雪", "STORM_SNOW", "  见 降水强度"),
    DUST("浮尘", "DUST", "AQI > 150, PM10 > 150，湿度 < 30%，风速 < 6 m/s"),
    SAND("沙尘", "SAND", "AQI > 150, PM10> 150，湿度 < 30%，风速 > 6 m/s"),
    WIND("大风", "WIND", ""),
    ;
    
    /**
     * 中文天气现象
     */
    public String con;
    
    /**
     * 编码
     */
    public String code;
    
    /**
     * 描述
     */
    public String tips;
    
    /**
     * 构造函数
     * @param con
     * @param code
     * @param tips
     */
    SkyCon(String con, String code, String tips) {
        this.con = con;
        this.code = code;
        this.tips = tips;
    }
}
