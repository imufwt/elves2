package online.elves.third.apis;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import online.elves.config.Const;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * 获取指定接口的信息 文字 诗词等
 */
@Slf4j
public class Letter {
    
    /**
     * 获取每日一句
     */
    public static String getOneWord() {
        // 参数对象
        Map<String, Object> params = Maps.newConcurrentMap();
        params.put("app_id", Const.MXN_API_KEY);
        params.put("app_secret", Const.MXN_API_SECRET);
        // 获取随机一句话
        params.put("count", 1);
        // 笑话列表
        String uri = "https://www.mxnzp.com/api/daily_word/recommend";
        // 获取随机的笑话段子
        String result = HttpUtil.get(uri, params);
        if (StringUtils.isBlank(result)) {
            log.warn("(╥╯^╰╥)服务器开小差了, 要不你再试一下?");
            return "";
        }
        // 救命 这么多转化
        JSONObject onz = JSON.parseObject(result);
        if (onz.getInteger("code") != 1) {
            log.warn("刚跑神了, 要不你再问问我?");
            return "";
        }
        // 一句话
        return onz.getJSONArray("data").getJSONObject(0).getString("content");
    }
}
