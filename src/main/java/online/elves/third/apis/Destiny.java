package online.elves.third.apis;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import online.elves.config.Const;
import online.elves.third.apis.juhe.Today;
import online.elves.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 算命 api.
 */
@Slf4j
public class Destiny {
    
    /**
     * 聚合 api 的 key
     */
    private static final String JUHE_KEY = "";
    
    /**
     * 获取星座运势
     * @param consName
     * @return
     */
    public static String getConstellation(String consName) {
        if (StringUtils.isBlank(consName)) {
            return "emmm...你倒是告诉我星座呀~";
        }
        // 查询 key
        String consKey = Const.CONSTELLATION_PREFIX + consName;
        // 获取 星座运势
        String cons = RedisUtil.get(consKey);
        // 没有就去查询
        if (StringUtils.isBlank(cons)) {
            return getJuheConsToday(consName, consKey);
        }
        return cons;
    }
    
    /**
     * 聚合接口的星座运势  今日
     * http://web.juhe.cn/constellation/getAll
     * @param consName
     * @param consName
     * @return
     */
    private static String getJuheConsToday(String consName, String consKey) {
        // 参数对象
        Map<String, Object> params = Maps.newConcurrentMap();
        // api key
        params.put("key", JUHE_KEY);
        // 星座名称
        params.put("consName", consName);
        // 运势类型：today,tomorrow,week,month,year
        params.put("type", "today");
        // 笑话列表
        String uri = "http://web.juhe.cn/constellation/getAll";
        // 获取返回结果
        String result = HttpUtil.get(uri, params);
        if (StringUtils.isBlank(result)) {
            log.warn("(╥╯^╰╥)服务器开小差了, 要不你再试一下?");
            return "";
        }
        // 今日运势
        Today today = JSON.parseObject(result, Today.class);
        // 最后需要处理的对象
        StringBuilder word = new StringBuilder("#### " + consName + " 今日运势\n\n");
        word.append("> ").append(today.getSummary()).append("\n\n");
        word.append("今天幸运数字: `").append(today.getNumber()).append("` | 幸运颜色: `").append(today.getColor()).append("` **今天适合...你自己看着办吧~ 嘻嘻**\n\n");
        word.append("爱情指数: `").append(today.getLove()).append("` | 速配星座: `").append(today.getQFriend()).append("` **少年,机会在你自己手里**\n\n");
        word.append("财运指数: `").append(today.getMoney()).append("` | 健康指数: `").append(today.getHealth()).append("` **生命在于运动, 健康就是财富!**\n\n");
        word.append("工作指数: `").append(today.getWork()).append("` **反正摸鱼是第一生产力, 不允许反驳**\n\n");
        // 0 点过期
        LocalDateTime time = LocalDateTime.now();
        RedisUtil.set(consKey, word.toString(), Long.valueOf(Duration.between(time, time.plusDays(1).toLocalDate().atStartOfDay()).getSeconds()).intValue());
        return word.toString();
    }
    
    public static void main(String[] args) {
        //log.info(getJuheConsToday("双子座", Const.FishKey.CONSTELLATION_PREFIX.value + "双子座"));
        LocalDate now = LocalDate.now();
        //log.info("今天是{}年{}月{}日, 星期{}", now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), WEEK_CN[now.getDayOfWeek().getValue()]);
    }
    
}
