package online.elves.service;

import lombok.extern.slf4j.Slf4j;
import online.elves.config.Const;
import online.elves.third.fish.Fish;
import online.elves.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 缓存服务类
 */
@Slf4j
@Component
public class CurrencyService {
    /**
     * 发送对象
     *
     * @param user
     * @param count
     * @return
     */
    public static void sendCurrency(String user, int count, String ref) {
        if (StringUtils.isBlank(user)) {
            return;
        }
        // 鱼翅个数 缓存 key
        String key = Const.CURRENCY_TIMES_PREFIX + user;
        // 补偿次数
        RedisUtil.modify(key, count);
        if (count >= 0) {
            Fish.send2User(user, "您获得了 ..." + count + " 个... 鱼翅. 已到账...[Cause: " + ref + "]");
        } else {
            Fish.send2User(user, "您失去了 ..." + Math.abs(count) + " 个... 鱼翅. 已扣除...[Cause: " + ref + "]");
        }
    }

    /**
     * 发送对象  鱼丸
     *
     * @param user
     * @param count
     * @return
     */
    public static void sendCurrencyFree(String user, int count, String ref) {
        if (StringUtils.isBlank(user)) {
            return;
        }
        // 鱼丸个数 缓存 key
        String key = Const.CURRENCY_TIMES_FREE_PREFIX + user;
        // 补偿次数
        RedisUtil.modify(key, count);
        if (count >= 0) {
            Fish.send2User(user, "您获得了 ..." + count + " 个... 鱼丸. 已到账...[Cause: " + ref + "]");
        } else {
            Fish.send2User(user, "您失去了 ..." + Math.abs(count) + " 个... 鱼丸. 已扣除...[Cause: " + ref + "]");
        }
    }

    /**
     * 获取用户鱼翅数量
     *
     * @param user
     * @return
     */
    public static int getCurrency(String user) {
        // 鱼翅个数 缓存 key
        String lKey = Const.CURRENCY_TIMES_PREFIX + user;
        // 获取次数
        String lTimes = RedisUtil.get(lKey);
        // 有
        if (StringUtils.isNotBlank(lTimes)) {
            return Integer.parseInt(lTimes);
        }
        // 没有鱼翅
        return -1;
    }

    /**
     * 获取用户鱼丸数量
     *
     * @param user
     * @return
     */
    public static int getCurrencyFree(String user) {
        // 鱼丸数量
        String lfKey = Const.CURRENCY_TIMES_FREE_PREFIX + user;
        // 鱼丸个数
        String lfTimes = RedisUtil.get(lfKey);
        // 有
        if (StringUtils.isNotBlank(lfTimes)) {
            return Integer.parseInt(lfTimes);
        }
        // 没有鱼丸
        return -1;
    }
}
