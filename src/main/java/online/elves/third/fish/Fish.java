package online.elves.third.fish;

import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import online.elves.config.Cache;
import online.elves.config.Const;
import online.elves.third.fish.model.FResp;
import online.elves.third.fish.model.FUser;
import online.elves.third.fish.utils.FUtil;
import online.elves.utils.DateUtil;
import online.elves.utils.EncryptUtil;
import online.elves.utils.LotteryUtil;
import online.elves.utils.RedisUtil;
import online.elves.ws.WsClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 摸鱼派工具类
 */
@Slf4j
@Component
public class Fish {
    
    /**
     * 猜拳权重
     */
    private static TreeMap<Integer, Double> RockMap = new TreeMap<>();
    
    /**
     * 默认
     */
    static {
        RockMap.put(0, Double.valueOf("0.33"));
        RockMap.put(1, Double.valueOf("0.33"));
        RockMap.put(2, Double.valueOf("0.33"));
        RockMap.put(3, Double.valueOf("0.01"));
    }
    
    /**
     * 获取 apiKey
     * @return
     */
    public static String getKey() {
        // 缓存key
        String sKey = "SECRET.KEY";
        // 查看是否有缓存
        String key = Cache.getInstance().get(sKey);
        if (StringUtils.isBlank(key)) {
            key = RedisUtil.get(sKey);
            if (StringUtils.isNotBlank(key)) {
                // 放入本地缓存, 减少开销
                Cache.getInstance().put(sKey, key);
                return key;
            }
            // 否则继续查询 查询参数
            JSONObject body = new JSONObject();
            body.put("nameOrEmail", RedisUtil.get(Const.ELVES_MAME));
            body.put("userPassword", EncryptUtil.MD5(RedisUtil.get(Const.ELVES_SECRET)));
            // 返回对象
            FResp respObj = FUtil.post("https://fishpi.cn/api/getKey", "", body.toJSONString());
            if (respObj.isOk()) {
                // 获取秘钥
                key = respObj.getKey();
                // 放入缓存
                Cache.getInstance().put(sKey, key);
                // 暂时不用过期了
                RedisUtil.set(sKey, key);
                return key;
            }
            log.error("获取 APIKEY 异常...");
            // 返回一个空
            return "";
        }
        return key;
    }
    
    /**
     * 获取用户信息
     * @return
     */
    public static FUser getUser(String userName) {
        // 直接返回
        String spec = FUtil.getSpec("https://fishpi.cn/user/" + userName, "");
        if (StringUtils.isNotBlank(spec)) {
            return JSON.parseObject(spec, FUser.class);
        }
        return null;
    }
    
    /**
     * 获取用户编号
     * @return
     */
    public static Integer getUserNo(String userName) {
        String uNo = Cache.getInstance().get(userName);
        if (Objects.nonNull(uNo)) {
            return Integer.valueOf(uNo);
        }
        // 直接返回
        String spec = FUtil.getSpec("https://fishpi.cn/user/" + userName, "");
        if (StringUtils.isNotBlank(spec)) {
            FUser user = JSON.parseObject(spec, FUser.class);
            Integer userNo = user.getUserNo();
            // 放入内存缓存 不过期
            Cache.getInstance().put(userName, userNo + "");
            return userNo;
        }
        return null;
    }
    
    /**
     * 发送消息
     * @param content
     * @return
     */
    public static void sendMsg(String content) {
        // 组装对象
        StringBuilder cont = new StringBuilder(content);
        // 查询参数
        JSONObject body = new JSONObject();
        body.put("apiKey", Fish.getKey());
        // 广告
        String tempCont = RedisUtil.get(Const.TEMPORARY_CONTENT);
        if (StringUtils.isNotBlank(tempCont)) {
            cont.append(" \n\n <span id = 'ad'>" + tempCont + "</span>");
        }
        cont.append(" \n\n <span id = '" + System.currentTimeMillis() + "'/>");
        // 方便屏蔽
        cont.append(" \n\n <span id = 'elves'/>");
        body.put("content", cont.toString());
        // 发送消息
        Fish.send(body);
    }
    
    /**
     * 发送专属红包
     */
    public static void sendSpecify(String userName, int money, String msg) {
        // 查询参数
        JSONObject body = new JSONObject();
        body.put("apiKey", getKey());
        // 红包内容
        JSONObject rp = new JSONObject();
        // 红包信息
        if (StringUtils.isBlank(msg)) {
            msg = "试试看，这是给你的红包吗？";
        }
        rp.put("msg", msg);
        // 红包金额
        rp.put("money", money);
        // 红包个数
        rp.put("count", 1);
        // 接收者...专属红包有效
        rp.put("recivers", Lists.newArrayList(userName));
        // 红包类型 random(拼手气红包), average(平分红包)，specify(专属红包)，heartbeat(心跳红包)，rockPaperScissors(猜拳红包)
        rp.put("type", "specify");
        // 消息内容
        body.put("content", "[redpacket]" + rp.toJSONString() + "[/redpacket]");
        // 为空就重新获取
        send(body);
    }
    
    /**
     * 发送猜拳红包
     */
    public static void sendRockPaperScissors(String userName, int money) {
        // 获取概率与奖品等级分组
        List<Double> list = new ArrayList<>(RockMap.values());
        List<Integer> level = new ArrayList<>(RockMap.keySet());
        // 获取结果
        Integer result = level.get(new LotteryUtil(list).next());
        // 查询参数
        JSONObject body = new JSONObject();
        body.put("apiKey", getKey());
        // 红包内容
        JSONObject rp = new JSONObject();
        // 红包信息
        if (StringUtils.isNotBlank(userName)) {
            rp.put("msg", userName + " 来 solo");
        } else {
            rp.put("msg", "石头剪刀布, 你行么?");
        }
        // 红包金额
        rp.put("money", money);
        // 红包个数
        rp.put("count", 1);
        // 接收者...专属红包有效
        //rp.put("recivers", List);
        // 红包类型 random(拼手气红包), average(平分红包)，specify(专属红包)，heartbeat(心跳红包)，rockPaperScissors(猜拳红包)
        rp.put("type", "rockPaperScissors");
        // 猜拳红包必须参数，表示发送者出招，0 = 石头，1 = 剪刀，2 = 布
        rp.put("gesture", result == 3 ? new Random().nextInt(3) : result);
        // 消息内容
        body.put("content", "[redpacket]" + rp.toJSONString() + "[/redpacket]");
        // 为空就重新获取
        send(body);
    }
    
    /**
     * 领奖
     */
    public static void getAward() {
        // 当前时间
        LocalDateTime now = LocalDateTime.now();
        // 当前日
        String day = DateUtil.formatDay(now.toLocalDate());
        //
        String last = RedisUtil.get(Const.AWARD_STATUS);
        // 存在且是今天 就不管了
        if (StringUtils.isNotBlank(last) && last.equals(day)) {
            // 啥也不做 领过奖了
        } else {
            // 拿到奖励了
            if (award()) {
                RedisUtil.set(Const.AWARD_STATUS, day);
            }
        }
    }
    
    /**
     * 根据 标签 获取文章
     */
    public static FResp getArticlesTag(String tag, int model, int p, int size) {
        // 拼接 uri
        String uri = "https://fishpi.cn/api/articles/tag/" + URLUtil.encode(tag) + sm(model) + "p=" + (p < 1 ? 1 : p) + "&size=" + (size < 1 ? 1 : size) + "&apiKey=";
        return FUtil.get(uri, getKey());
    }
    
    /**
     * 随机获取指定篇数的文章
     */
    public static FResp getArticlesRandom(int size) {
        // 拼接 uri
        String uri = "https://fishpi.cn/article/random/" + size + "?_=" + System.currentTimeMillis();
        return FUtil.get(uri, getKey());
    }
    
    /**
     * 获取指定类型通知
     */
    public static FResp getNotifyList(String type) {
        // 拼接 uri
        String uri = "https://fishpi.cn/api/getNotifications?type=" + type + "&apiKey=";
        return FUtil.get(uri, getKey());
    }
    
    /**
     * 评论文章
     * @param oId
     * @param content
     */
    public static void comment(Long oId, String content) {
        // 查询参数
        JSONObject body = new JSONObject();
        // 通用秘钥
        body.put("apiKey", getKey());
        body.put("articleId", oId);
        // 是否匿名
        body.put("commentAnonymous", false);
        // 是否仅楼主可见
        body.put("commentVisible", false);
        // 评论原文
        body.put("commentContent", content);
        // 请求评论
        FUtil.post("https://fishpi.cn/comment", "", body.toJSONString());
    }
    
    /**
     * 打开红包
     * @param oId
     * @param gesture 猜拳
     */
    public static boolean openRedPacket(Long oId, Boolean gesture) {
        // 查询参数
        JSONObject body = new JSONObject();
        body.put("apiKey", getKey());
        body.put("oId", oId);
        if (gesture) {
            // 获取概率与奖品等级分组
            List<Double> list = new ArrayList<>(RockMap.values());
            List<Integer> level = new ArrayList<>(RockMap.keySet());
            // 获取结果
            Integer result = level.get(new LotteryUtil(list).next());
            // 0 = 石头，1 = 剪刀，2 = 布
            body.put("gesture", result == 3 ? new Random().nextInt(3) : result);
        }
        // 返回对象
        return FUtil.post("https://fishpi.cn/chat-room/red-packet/open", "", body.toJSONString()).isOk();
    }
    
    /**
     * 给某人发私信
     * @param user
     * @param content
     */
    public static void send2User(String user, String content) {
        if (StringUtils.isBlank(user)) {
            log.error("打开私聊没有用户名");
            return;
        }
        try {
            // 会话
            Pair<LocalDateTime, Session> session = WsClient.session.get(user);
            // 会话关闭了
            if (Objects.isNull(session) || !session.getValue().isOpen()) {
                // 重新打开
                WsClient.start(user);
                // 重新获取
                session = WsClient.session.get(user);
                if (Objects.isNull(session) || !session.getValue().isOpen()) {
                    // 打开链接失败
                    log.error("{} 打开私聊失败!!! ... {}", user, content);
                    return;
                }
            }
            // 异步发送消息
            session.getValue().getAsyncRemote().sendText(content);
        } catch (Exception e) {
            log.info("私聊失败 {}", e.getMessage());
        }
    }
    
    /**
     * 领取昨日奖励
     * @return
     */
    public static Boolean award() {
        // 返回对象
        FResp respObj = FUtil.get("https://fishpi.cn/activity/yesterday-liveness-reward-api?apiKey=", getKey());
        // 请求成功 key 有效
        if (respObj.isOk() && respObj.getSum() < 0) {
            return true;
        }
        return false;
    }
    
    /**
     * 文章请求后缀
     * @param model
     * @return
     */
    private static String sm(int model) {
        switch (model) {
            case 1:
                // 热门
                return "/hot?";
            case 2:
                // 点赞
                return "/good?";
            case 3:
                // 最近回复
                return "/reply?";
            case 4:
                // 优选
                return "/perfect?";
            case 0:
            default:
                return "?";
        }
    }
    
    
    /**
     * 发送聊天室消息
     * @param body
     */
    public static void send(JSONObject body) {
        // 返回对象
        FResp respObj = FUtil.post("https://fishpi.cn/chat-room/send", "", body.toJSONString());
        // 请求成功
        if (!respObj.isOk()) {
            log.error("chat room sendCoupon error ..." + JSON.toJSONString(respObj));
        }
    }
    
}
