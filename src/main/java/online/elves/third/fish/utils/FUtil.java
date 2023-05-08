package online.elves.third.fish.utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import online.elves.third.fish.model.FResp;

/**
 * 网络工具类
 */
@Slf4j
public class FUtil {
    
    private static final String UA = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36 Edg/90.0.818.56";
    
    /**
     * get 请求 特殊请求
     * @param uri
     * @param key
     * @return
     */
    public static String getSpec(String uri, String key) {
        try {
            // 获取返回信息
            HttpResponse response = HttpRequest.get(uri + key).header("User-Agent", UA).execute();
            // 返回对象
            return response.body();
        } catch (Exception e) {
            log.warn("some request get error...{}", e.getMessage());
        }
        return null;
    }

    /**
     * post 请求
     * @param uri
     * @param key
     * @return
     */
    public static String postSpec(String uri, String key, String body) {
        try {
            // 获取返回信息
            HttpResponse response = HttpRequest.post(uri + key).header("User-Agent", UA).body(body).execute();
            // 返回对象
            return response.body();
        } catch (Exception e) {
            log.warn("some request post error...{}", e.getMessage());
        }
        return null;
    }

    /**
     * get 请求
     * @param uri
     * @param key
     * @return
     */
    public static FResp get(String uri, String key) {
        try {
            // 获取返回信息
            HttpResponse response = HttpRequest.get(uri + key).header("User-Agent", UA).execute();
            // 返回对象
            return JSON.parseObject(response.body(), FResp.class);
        } catch (Exception e) {
            log.warn("some request get error...{}", e.getMessage());
        }
        return new FResp();
    }
    
    /**
     * post 请求
     * @param uri
     * @param key
     * @return
     */
    public static FResp post(String uri, String key, String body) {
        try {
            // 获取返回信息
            HttpResponse response = HttpRequest.post(uri + key).header("User-Agent", UA).body(body).execute();
            // 返回对象
            return JSON.parseObject(response.body(), FResp.class);
        } catch (Exception e) {
            log.warn("some request post error...{}", e.getMessage());
        }
        return new FResp();
    }
    
}
