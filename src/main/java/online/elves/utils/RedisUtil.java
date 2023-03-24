package online.elves.utils;

import online.elves.config.RedisConfigService;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * redis 工具类
 */
@Component
public class RedisUtil {
    
    /**
     * 写入字符串
     * @param key
     * @param value
     */
    public static void set(String key, String value) {
        RedisConfigService.get().opsForValue().set(key, value);
    }
    
    /**
     * 写入字符串 带过期时间
     * @param key
     * @param value
     * @param timeOut
     */
    public static void set(String key, String value, long timeOut) {
        RedisConfigService.get().opsForValue().set(key, value, timeOut, TimeUnit.SECONDS);
    }
    
    /**
     * 获取字符串缓存
     * @param key
     * @return
     */
    public static String get(String key) {
        Object o = RedisConfigService.get().opsForValue().get(key);
        if (Objects.nonNull(o)) {
            return Objects.toString(o);
        }
        return null;
    }
    
    /**
     * 给用户加积分 做排行榜
     * @param key
     * @param uNo
     * @param score
     * @return
     */
    public static double incrScore(String key, String uNo, int score) {
        // 增加积分
        return RedisConfigService.get().opsForZSet().incrementScore(key, uNo, score);
    }
    
    /**
     * 获取排行榜数据
     * @param key
     * @param start
     * @param end
     * @return
     */
    public static Set<ZSetOperations.TypedTuple> rank(String key, int start, int end) {
        return RedisConfigService.get().opsForZSet().reverseRangeWithScores(key, start, end);
    }
    
    /**
     * 获取排行榜指定用户分值
     * @param key
     * @param user
     * @return
     */
    public static Double getScore(String key, String user) {
        return RedisConfigService.get().opsForZSet().score(key, user);
    }
    
    /**
     * 删除对象
     * @param key
     * @return
     */
    public static String del(String key) {
        Object v = RedisConfigService.get().opsForValue().getAndDelete(key);
        if (Objects.nonNull(v)) {
            return Objects.toString(v);
        }
        return null;
    }
    
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    /* * * * * * * * * * * * * * * * * * 常用方法 * * * * * * * * * * * * * * * * * * */
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    
    /**
     * 给 key 修改 键值对
     * @param key
     * @param count
     */
    public static void modify(String key, int count) {
        // 原始数量
        int origin = Integer.valueOf(Optional.ofNullable(get(key)).orElse("0"));
        // 增加/减少数量并回写
        int sunt = origin + count;
        if (sunt < 0) {
            sunt = 0;
        }
        set(key, sunt + "");
    }
    
}
