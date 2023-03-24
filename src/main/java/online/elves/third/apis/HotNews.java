package online.elves.third.apis;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import online.elves.third.apis.hotnews.TopurlNews;
import online.elves.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 热点新闻.
 */
@Slf4j
@Component
public class HotNews {
    
    /**
     * 缓存更新时间  宵禁结束
     */
    private static LocalTime start = LocalTime.of(8, 0, 0);
    
    /**
     * 获取今日新闻
     * @return
     */
    public static TopurlNews getTopurlNews() {
        // 缓存key
        String key = "hot:news:topurl";
        // 获取缓存
        String n = RedisUtil.get(key);
        if (StringUtils.isBlank(n)) {
            // 获取新闻对象
            String get = HttpUtil.get("https://news.topurl.cn/api");
            // 判断是否为空
            if (StringUtils.isNotBlank(get)) {
                // 反序列化
                TopurlNews topurlNews;
                try {
                    topurlNews = JSON.parseObject(get, TopurlNews.class);
                } catch (Exception e) {
                    log.info("hot news topurl 反序列化异常 => {}", get);
                    // 直接返回异常
                    return null;
                }
                // 请求失败
                if (topurlNews.getCode() != 200){
                    log.info("hot news topurl 请求失败 => {}", get);
                    return null;
                }
                // 没啥问题的话 存下缓存 宵禁结束缓存过期
                LocalDateTime time = LocalDateTime.of(LocalDate.now().plusDays(1), start);
                // 设置缓存
                RedisUtil.set(key, get, Long.valueOf(Duration.between(LocalDateTime.now(), time).getSeconds()).intValue());
                return topurlNews;
            } else {
                log.info("hot news topurl interface has no msg");
            }
            return null;
        } else {
            // 存在的话, 肯定不会反序列化问题了
            return JSON.parseObject(n, TopurlNews.class);
        }
    }
    
}
