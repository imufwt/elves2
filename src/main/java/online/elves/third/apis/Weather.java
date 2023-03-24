package online.elves.third.apis;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import online.elves.config.Const;
import online.elves.mapper.entity.DistrictCn;
import online.elves.third.apis.caiyun.CaiYunRealTime;
import online.elves.third.apis.caiyun.enums.Comfort;
import online.elves.third.apis.caiyun.enums.SkyCon;
import online.elves.third.apis.caiyun.model.Realtime;
import online.elves.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 获取指定接口的信息 天气
 */
@Slf4j
public class Weather {
    
    /**
     * 获取城市天气
     * @param city 查询的城市
     * @param type 0 精灵的信息
     * @return
     */
    public static String get(DistrictCn city, Integer type) {
        // 缓存 key
        String wKey = Const.WEATHER_PREFIX + type + ":" + city;
        // 天气缓存
        String weather = RedisUtil.get(wKey);
        if (StringUtils.isNotBlank(weather)) {
            return weather;
        }
        try {
            // 查询地理位置
            if (Objects.isNull(city)) {
                return "o(╯□╰)o...你是去外太空了咩? 一定要照顾好自己哦~";
            }
            // 区分类型
            switch (type) {
                case 0:
                default:
                    // 天气对象
                    String w = "`" + city.getDistrict() + "` 现在 " + getCaiYunRealTime(city.getLon(), city.getLat());
                    // 缓存同一唯一天气状况十分钟
                    RedisUtil.set(wKey, w, 600);
                    return w;
            }
        } catch (Exception e) {
            log.info("查询地理位置出错...", e.getMessage());
            return "┗|｀O′|┛ 嗷~~我刚跑神了哇...生自己的气";
        }
    }
    
    /**
     * 使用彩云 api 获取实时天气
     * https://docs.caiyunapp.com/docs/realtime
     * @param lon 经度
     * @param lat 维度
     * @return
     */
    private static String getCaiYunRealTime(String lon, String lat) {
        // 请求地址
        String uri = "https://api.caiyunapp.com/v2.6/" + Const.CAI_YUN_API + "/" + lon + "," + lat + "/realtime";
        // 获取天气结果
        String result = HttpUtil.get(uri);
        if (StringUtils.isBlank(result)) {
            return " (╥╯^╰╥) 我还小, 感知不到你所在位置的天气";
        }
        // 实况天气
        CaiYunRealTime realTime = JSON.parseObject(result, CaiYunRealTime.class);
        if (realTime.isSuc()) {
            // 天气内容
            Realtime realtime = realTime.getResult().getRealtime();
            // 返回对象
            return "`" + SkyCon.valueOf(realtime.getSkycon()).con + "` , 体感温度 `" + realtime.getApparent_temperature() + "` 摄氏度. 当前室外 " + Comfort.judge(realtime.getLife_index().getComfort().getIndex());
        }
        return "┭┮﹏┭┮...小精灵在大气层迷路啦...";
    }
    
    public static void main(String[] args) {
        log.info(getCaiYunRealTime("116.310316", "39.956074"));
    }
    
}
