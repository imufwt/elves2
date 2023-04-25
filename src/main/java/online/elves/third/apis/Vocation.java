package online.elves.third.apis;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import online.elves.utils.DateUtil;
import online.elves.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 节假日
 */
@Slf4j
public class Vocation {
    /**
     * 模拟浏览器信息
     */
    final public static String UA = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36";

    /**
     * 获取假日信息
     *
     * @return
     */
    public static VocationDetail get() {
        // 当前时间
        LocalDateTime now = LocalDateTime.now();
        // 节日明细
        VocationDetail detail = new VocationDetail();
        // 获取缓存
        String vocation = RedisUtil.get("VOCATION");
        // 为空就组装
        if (StringUtils.isBlank(vocation)) {
            // 今天
            String today = DateUtil.formatDay(now.toLocalDate());
            // 请求日期
            HttpResponse response = HttpRequest.get("https://timor.tech/api/holiday/info/" + today)
                    .timeout(70000).setConnectionTimeout(30000).header("User-Agent", UA).execute();
            if (200 == response.getStatus()) {
                response.charset("UTF-8");
                // 获取结果
                JSONObject result = JSON.parseObject(response.body());
                // 获取类型
                JSONObject resType = result.getJSONObject("type");
                // 节假日类型，分别表示 0工作日、1周末、2节日、3调休。
                int type = resType.getIntValue("type");
                // 写入类型
                detail.setType(type);
                // 写入今天的名字  没有假期显示周几，有的话显示假期，比如周六、国庆节
                detail.setDayName(resType.getString("name"));
                // 如果是周末或者节日，获取距离上班还有多久
                if (type == 1 || type == 2) {
                    HttpResponse workday = HttpRequest.get("https://timor.tech/api/holiday/workday/next/" + today)
                            .timeout(70000).setConnectionTimeout(30000).header("User-Agent", UA).execute();
                    if (200 == workday.getStatus()) {
                        workday.charset("UTF-8");
                        JSONObject workdayRes = JSON.parseObject(workday.body());
                        // 距离开学还有多久
                        detail.setWRest(workdayRes.getJSONObject("workday").getIntValue("rest"));
                    }
                }
                // 如果是工作日或者调休，获取下一个节假日
                if (type == 0 || type == 3) {
                    HttpResponse weekend = HttpRequest.get("https://timor.tech/api/holiday/next/" + today + "?type=Y&week=Y")
                            .timeout(70000).setConnectionTimeout(30000).header("User-Agent", UA).execute();
                    if (200 == weekend.getStatus()) {
                        weekend.charset("UTF-8");
                        JSONObject weekendRes = JSON.parseObject(weekend.body());
                        // 下一个假期的名字
                        detail.setVName(weekendRes.getJSONObject("holiday").getString("name"));
                        // 还有几天放假
                        detail.setVRest(weekendRes.getJSONObject("holiday").getIntValue("rest"));
                    }
                }
                // 写入缓存 明天0点失效
                RedisUtil.set("VOCATION", JSON.toJSONString(detail), Long.valueOf(Duration.between(now, now.toLocalDate().plusDays(1).atStartOfDay()).getSeconds()).intValue());
            } else {
                log.info("假日获取异常...{}", JSON.toJSONString(response));
                return null;
            }
        } else {
            // 存在则直接反序列化
            detail = JSON.parseObject(vocation, VocationDetail.class);
        }
        return detail;
    }

    /**
     * 组装假日一言
     *
     * @param detail
     * @return
     */
    public static String getWord(VocationDetail detail) {
        if (Objects.isNull(detail)) {
            return "啥也不是";
        }
        // 句子
        StringBuilder word = new StringBuilder();
        // 假日类型
        int type = detail.getType();
        // 今天的日子
        String dayName = detail.getDayName();
        // 假期的日子
        String vName = detail.getVName();
        // 距离放假还有
        int vRest = detail.getVRest();
        // 距离开工还有
        int wRest = detail.getWRest();
        // 工作日
        if (type == 0 || type == 3) {
            if (vRest == 1) {
                word.append(" 今天提桶, 明天跑路! ").append(vName).append(" 马上就要到啦~ 我宣布📢: 明天放假🎉!");
            } else {
                if (type == 3) {
                    word.append(" 调休不摸🐟, 天理难容! ");
                } else {
                    word.append(" 摸🐟加油! ");
                }
                word.append(" 距离 ").append(vName).append(" 还有 ").append(vRest).append(" 天");
            }
        } else {
            // 假期
            if (wRest == 1) {
                word.append(" 🥶 今天是 ").append(dayName).append(" , 假期越严重不足!!! 😭 明天上班 🥶");
            } else {
                word.append(" ").append(dayName).append(" 呢~ 🏖 假日余额还有 ").append(wRest).append(" 天! 愉快的去浪吧~");
            }
        }
        return word.toString();
    }

    /**
     * 假期明细
     */
    @Data
    @Builder
    @AllArgsConstructor
    public static class VocationDetail {
        /**
         * 类型
         */
        private int type;
        /**
         * 日期名字
         */
        private String dayName;
        /**
         * 假期名字
         */
        private String vName;
        /**
         * 还有几天放假
         */
        private int vRest;
        /**
         * 还有几天开学
         */
        private int wRest;

        /**
         * 初始化对象
         */
        public VocationDetail() {
            this.type = -1;
            this.dayName = "";
            this.vName = "";
            this.vRest = -1;
            this.wRest = -1;
        }
    }
}
