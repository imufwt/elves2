package online.elves.third.apis;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import online.elves.config.Const;
import online.elves.utils.LotteryUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 获取指定接口的信息 彩票
 */
@Slf4j
public class Lottery {
    
    /* *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  */
    /* *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  */
    /* *  *  *  *  *  *  *  *  * 本地抽奖 start   *  *  *  *  *  *  */
    /* *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  */
    /* *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  */
    
    
    // 获取等级与权重
    private static TreeMap<Integer, Double> map = new TreeMap<>();
    
    /**
     * 默认
     */
    static {
        map.put(0, Double.valueOf("0.50"));
        map.put(1, Double.valueOf("0.0001"));
        map.put(2, Double.valueOf("0.0009"));
        map.put(3, Double.valueOf("0.001"));
        map.put(4, Double.valueOf("0.238"));
        map.put(5, Double.valueOf("0.25"));
    }
    
    /**
     * 幸运大抽奖
     */
    public static int get() {
        // 获取概率与奖品等级分组
        List<Double> list = new ArrayList<>(map.values());
        List<Integer> level = new ArrayList<>(map.keySet());
        return level.get(new LotteryUtil(list).next());
    }
    

    
    /* *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  */
    /* *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  */
    /* *  *  *  *  *  *  *  *  * 本地抽奖 end  *  *  *  *  *  *  *  */
    /* *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  */
    /* *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  */
    
    /**
     * 获取中奖信息结果
     * @param expect 指定期数
     * @param code 彩票类型
     * @return
     */
    public static String getInfo(String expect, String code) {
        // 参数对象
        Map<String, Object> params = Maps.newConcurrentMap();
        params.put("app_id", Const.MXN_API_KEY);
        params.put("app_secret", Const.MXN_API_SECRET);
        params.put("code", code);
        // 最新一期的开奖信息
        String uri = "https://www.mxnzp.com/api/lottery/common/latest";
        if (StringUtils.isNotBlank(expect)){
            // 不获取指定期数, 则获取最新一期
            uri = "https://www.mxnzp.com/api/lottery/common/aim_lottery";
            // 放入指定日期
            params.put("expect", expect);
        }
        // 获取随机的笑话段子
        String result = HttpUtil.get(uri, params);
        if (StringUtils.isBlank(result)) {
            return " (╥╯^╰╥)服务器开小差了, 要不你再试一下?";
        }
        // 救命 这么多转化
        JSONObject onz = JSON.parseObject(result);
        if (onz.getInteger("code") != 1) {
            return " 刚跑神了, 要不你再问问我?";
        }
        // 结果对象
        JSONObject data = onz.getJSONObject("data");
        // 组合对象
        StringBuilder rs = new StringBuilder();
        rs.append("`").append(data.getString("name")).append("`\n\n");
        rs.append(" 第 `").append(data.getString("expect")).append("` 期的开奖结果是 :\n\n");
        rs.append(data.getString("openCode")).append("\n\n");
        rs.append(" `开奖时间` : ").append(data.getString("time")).append("\n\n");
        rs.append(" > !!!本结果仅供参考, 不具有任何指导意义!!!");
        return rs.toString();
    }
    
}
