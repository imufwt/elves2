package online.elves.config;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存缓存
 */
public class Cache {
    /**
     * 有效时间30分钟：30 * 60 * 1000
     */
    private static final long CACHE_HOLD_TIME_30M = 30 * 60 * 1000L;
    
    /**
     * 有效时间key后缀
     */
    private static final String SECONDS = "_seconds";
    /**
     * 缓存对象
     */
    private static volatile Cache cache;
    /**
     * 缓存内容
     */
    private static Map<String, String> CACHE_MAP;
    
    /**
     * 生成缓存对象
     */
    private Cache() {
        CACHE_MAP = new ConcurrentHashMap<>();
    }
    
    /**
     * 获取实例
     *
     * @return
     */
    public static Cache getInstance() {
        if (cache == null) {
            synchronized (Cache.class) {
                if (cache == null) {
                    cache = new Cache();
                }
            }
        }
        return cache;
    }
    
    /**
     * 存放一个缓存对象，默认保存时间30分钟
     *
     * @param cKey   缓存名称
     * @param cValue 缓存对象
     */
    public void put(String cKey, String cValue) {
        put(cKey, cValue, CACHE_HOLD_TIME_30M);
    }
    
    /**
     * 存放一个缓存对象，保存时间为holdTime
     *
     * @param cKey    缓存名称
     * @param cValue  缓存对象
     * @param seconds 时间
     */
    public void put(String cKey, String cValue, long seconds) {
        CACHE_MAP.put(cKey, cValue);
        // 设置缓存失效时间
        CACHE_MAP.put(cKey + SECONDS, (System.currentTimeMillis() + seconds * 1000) + "");
    }
    
    /**
     * 取出一个缓存对象
     *
     * @param cKey 缓存名称
     * @return 缓存对象
     */
    public String get(String cKey) {
        if (exist(cKey)) {
            return CACHE_MAP.get(cKey);
        }
        return null;
    }
    
    /**
     * 删除某个缓存
     *
     * @param cKey 缓存名称
     */
    public void remove(String cKey) {
        CACHE_MAP.remove(cKey);
        CACHE_MAP.remove(cKey + SECONDS);
    }
    
    /**
     * 检查缓存对象是否存在，
     * 若不存在，则返回false
     * 若存在，检查其是否已过有效期，如果已经过了则删除该缓存并返回false
     *
     * @param cKey 缓存名称
     * @return 缓存对象是否存在
     */
    public boolean exist(String cKey) {
        // 缓存对象
        String val = CACHE_MAP.get(cKey + SECONDS);
        if (StringUtils.isBlank(val)) {
            return false;
        }
        // 存在了再处理转化
        Long seconds = Long.valueOf(val);
        // 是否小于当前时间
        if (seconds < System.currentTimeMillis()) {
            remove(cKey);
            return false;
        }
        return true;
    }
}
