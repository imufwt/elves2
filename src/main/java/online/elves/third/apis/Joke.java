package online.elves.third.apis;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import online.elves.config.Const;
import online.elves.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 获取指定接口的信息 笑话
 */
@Slf4j
public class Joke {
    
    /**
     * 获取笑话
     * @return
     */
    public static String getJoke() {
        // 获取 笑话列表
        String jokeList = RedisUtil.get(Const.JOKE_LIST);
        // 没有列表, 就去查询
        if (StringUtils.isBlank(jokeList)) {
            return getJokeString();
        }
        // 有值
        List<String> list = JSON.parseArray(jokeList, String.class);
        if (CollUtil.isEmpty(list)) {
            return getJokeString();
        }
        // 获取第一个
        String word = list.get(0);
        // 移除已经用过的
        list.remove(word);
        // 放入缓存 并返回
        RedisUtil.set(Const.JOKE_LIST, JSON.toJSONString(list));
        return word;
    }
    
    /**
     * 获取笑话地址
     * @return
     */
    private static String getJokeString() {
        // 参数对象
        Map<String, Object> params = Maps.newConcurrentMap();
        params.put("app_id", Const.MXN_API_KEY);
        params.put("app_secret", Const.MXN_API_SECRET);
        // 笑话列表
        String uri = "https://www.mxnzp.com/api/jokes/list/random";
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
        // 段子列表
        JSONArray data = onz.getJSONArray("data");
        // 预备 放
        List<String> js = Lists.newArrayList();
        // 返回对象
        String word = "";
        // 遍历
        for (Object jo : data) {
            // 强转一波
            JSONObject object = (JSONObject) jo;
            // 笑话
            String content = object.getString("content");
            // 需要返回的
            if (StringUtils.isBlank(word)) {
                word = content;
            } else {
                js.add(content);
            }
        }
        // 放入缓存
        RedisUtil.set(Const.JOKE_LIST, JSON.toJSONString(js));
        return word;
    }
}
