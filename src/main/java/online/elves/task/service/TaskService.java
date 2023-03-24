package online.elves.task.service;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import online.elves.config.Const;
import online.elves.mapper.entity.MysteryCodeLog;
import online.elves.mapper.entity.User;
import online.elves.service.FService;
import online.elves.third.fish.Fish;
import online.elves.third.fish.model.FResp;
import online.elves.third.fish.model.articles.Articles;
import online.elves.third.fish.model.articles.ArticlesObj;
import online.elves.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 活动服务.
 */
@Slf4j
@Component
public class TaskService {
    
    @Resource
    FService fService;
    
    /**
     * 购买神秘代码
     */
    public void buyMysteryCode() {
        log.info("神秘代码购买...开始处理");
        // 获取购买人
        List<MysteryCodeLog> buyer = fService.getBuyer();
        // 没有购买者
        if (CollUtil.isEmpty(buyer)) {
            log.info("神秘代码开包, 没有购买者...结束");
            return;
        }
        // 遍历购买者
        for (MysteryCodeLog cl : buyer) {
            // 消息对象
            String msg = RedisUtil.get(cl.getOid().toString());
            if (StringUtils.isBlank(msg)) {
                // 没找到
                log.info("没有找到信息[{}]的缓存记录.", cl.getOid());
                continue;
            }
            // 神秘代码对象
            FService.MysteryCode mysteryCode = JSON.parseObject(msg, FService.MysteryCode.class);
            // 购买
            fService.buyMysteryCode(mysteryCode.getOid(), mysteryCode.getUser(), mysteryCode.getMoney(), mysteryCode.getRate(), mysteryCode.isHappy());
            // 删除缓存
            RedisUtil.del(cl.getOid().toString());
        }
        // 修改状态
        fService.updateBuyer(buyer);
    }
    
    /**
     * 迎新
     */
    public void welcome() {
        log.info("...开始迎新...");
        // 获取天降红包对象. 一次一页
        FResp point = Fish.getNotifyList("point");
        // 获取所有通知计数
        JSONArray data = (JSONArray) point.getData();
        // 从最后一个开始
        for (int i = data.size() - 1; i >= 0; i--) {
            // 获取最后的私聊信息ID
            String oid = RedisUtil.get(Const.CHAT_WELCOME_LAST);
            if (StringUtils.isBlank(oid)) {
                oid = "1675428598753";
            }
            // 数字化
            Long oId = Long.valueOf(oid);
            // 消息
            JSONObject msg = data.getJSONObject(i);
            // 获取消息ID
            Long id = msg.getLong("oId");
            // 通知内容
            String desc = msg.getString("description");
            // 获取消息内容中的用户名
            String uName = getName(desc);
            // 更早的消息或者不是天降红包, 就过去了
            if (id <= oId || !desc.contains("摸鱼派新鱼油")) {
                continue;
            }
            // 查询用户信息
            User user = fService.getUser(null, uName);
            // 用户昵称
            String userNickname = user.getUserNick();
            // 用户编号 其实就是存下库
            Integer userNo = user.getUserNo();
            // 处理下OID 记录下最后一条
            RedisUtil.set(Const.CHAT_WELCOME_LAST, msg.getString("oId"));
            // 组织消息
            String content = userNickname + ": \n\n" +
                    "新晋鱼油 ❤️️ 你好 :" + " \n\n" +
                    "----" + " \n\n" +
                    " 👏🏻欢迎来到[**摸鱼派**](https://fishpi.cn) ，我是摸鱼派的＜**礼仪委员**>江户川-哀酱( @APTX-4869 ) 的好朋友 **礼仪精灵**，你在社区摸鱼期间遇到的疑问都可以私信他哦。" + " \n\n" +
                    "----" + " \n\n" +
                    "#### 你可以尝试下面的操作, 完成一些必要的设置, 以便让大家更好的认识你" + " \n\n" +
                    "- 首先" + " \n" +
                    " -- 你可以在此 [**发帖**](https://fishpi.cn/post?type=0) 此引用“`新人报道`”的标签可以晋升正式成员~" + " \n" +
                    "- 其次" + " \n" +
                    " -- 你可以修改个人信息, 例如给自己起一个帅气的名字 [点我修改名字](https://fishpi.cn/settings), 也可以设置一个吸引人的头像 [点我修改头像](https://fishpi.cn/settings/avatar)" + " \n\n" +
                    "> Tips: 正式成员可以赞同帖子/点踩帖子/艾特用户/指定帖子等功能,详细介绍请移步 [【公告】摸鱼派会员等级规则 ](https://fishpi.cn/article/1630575841478)" + " \n" +
                    "----" + " \n\n" +
                    "#### 下面几个守则可以让你快速了解摸鱼派社区" + " \n\n" +
                    "1. **摸鱼守则**： [【必修】摸鱼派：摸鱼守则（修订第九版）](https://fishpi.cn/article/1631779202219)" + " \n\n" +
                    "2. **新人手册**： [『新人手册』摸鱼派是个什么样的社区](https://fishpi.cn/article/1630569106133)" + " \n\n" +
                    "3. **积分规则**： [【公告】摸鱼派积分使用和消费规则](https://fishpi.cn/article/1630572449626)" + " \n\n" +
                    "4. **活跃度**： [【公示】社区活跃度详细算法](https://fishpi.cn/article/1636946098474)" + " \n\n" +
                    "----" + " \n\n " +
                    "> 当然, 我也有一些好玩的功能, 你可以去[聊天室](https://fishpi.cn/cr)使用指令 `凌 菜单` 或 `凌 帮助` 来查看一些指令, 祝你在摸鱼派摸的开心❤️" + " \n\n " +
                    "Tips: 本条私信为自动发送, 请勿回复! " + " \n\n ";
            // 欢迎 发私信
            Fish.send2User(uName, content);
        }
    }
    
    /**
     * 新人报道
     */
    public void runCheck() {
        log.info("...检查新人报道...");
        // 遍历文章列表
        List<Long> oids = Lists.newArrayList();
        // 获取新人报道对象
        FResp resp = Fish.getArticlesTag("新人报道", 0, 1, 5);
        if (resp.isOk()) {
            ArticlesObj obj = (ArticlesObj) resp.getData();
            collectOids(oids, obj);
        }
        resp = Fish.getArticlesTag("新人报到", 0, 1, 5);
        if (resp.isOk()) {
            ArticlesObj obj = (ArticlesObj) resp.getData();
            collectOids(oids, obj);
        }
        // 不应该为空的
        if (CollUtil.isEmpty(oids)) {
            log.info("未获取到 新人报道 / 新人报到 列表...请及时检查");
            return;
        }
        // 缓存 key
        String lastKey = Const.LAST_NEW_MEM_ARTICLE;
        // 获取最后一篇文章 oid
        Long oId = Long.valueOf(Objects.requireNonNull(RedisUtil.get(lastKey)));
        // 过滤 大于最后一篇的文章
        oids = oids.stream().filter(x -> x > oId).collect(Collectors.toList());
        // 暂时没有新人报道
        if (CollUtil.isEmpty(oids)) {
            log.info("暂时没有 新人报道 / 新人报到...");
            return;
        }
        // 遍历文章列表 回帖
        for (Long id : oids) {
            // 回帖
            log.info("文章" + id);
            reply(id);
        }
        // 写入最大的 oid
        RedisUtil.set(lastKey, oids.stream().max(Long::compare).get().toString());
    }
    
    /**
     * 回复帖子
     * @param id
     */
    private static void reply(Long id) {
        // 欢迎内容
        String content = "❤️️ 友友你好呀~\n" +
                "\n" +
                "----\n" +
                "\n" +
                "欢迎来到[**摸鱼派**](https://fishpi.cn) \uD83C\uDF89 ，我是摸鱼派的＜**礼仪委员**>\uD83D\uDE04 \uD83D\uDC99`江户川-哀酱` 的好朋友 **礼仪小精灵**，你在社区摸鱼期间遇到的疑问（鱼游、积分、活跃度、徽章、小冰、帖子、清风明月、个人信息等）都可以私信他( @APTX-4869 )哦\uD83D\uDE04 。\n" +
                "\n" +
                "----\n" +
                "\n" +
                "#### 你可以尝试下面的操作, 完成一些必要的设置, 以便让大家更好的认识你\n" +
                "\n" +
                "你以修改个人信息\n" +
                "\n" +
                ">例如\n" +
                "\n" +
                "- 给自己起一个帅气的名字 [点我修改名字](https://fishpi.cn/settings), \n" +
                "- 也可以设置一个吸引人的头像 [点我修改头像](https://fishpi.cn/settings/avatar)\n" +
                "\n" +
                "----\n" +
                "\n" +
                "#### 当然, 下面几个链接也可以让你快速融入到摸鱼派社区的\uD83D\uDC4D.\n" +
                "\n" +
                "1. \uD83C\uDF89 `摸鱼守则`： [【必修】摸鱼派：摸鱼守则（修订第九版）](https://fishpi.cn/article/1631779202219)\n" +
                "\n" +
                "2. \uD83C\uDF89 `新人手册`： [『新人手册』摸鱼派是个什么样的社区](https://fishpi.cn/article/1630569106133)\n" +
                "\n" +
                "3. \uD83C\uDF89 `积分规则`： [【公告】摸鱼派积分使用和消费规则](https://fishpi.cn/article/1630572449626)\n" +
                "\n" +
                "4. \uD83C\uDF89 `活跃度`： [【公示】社区活跃度详细算法](https://fishpi.cn/article/1636946098474)\n" +
                "\n" +
                "----\n" +
                "\n" +
                "> 当然我也有一些好玩的功能, 你可以去[聊天室](https://fishpi.cn/cr)使用指令 `凌 菜单` 或 `凌 帮助` 来查看一些指令, 祝你在摸鱼派摸的开心❤️️；";
        // 回复评论
        Fish.comment(id, content);
    }
    
    /**
     * 收集 oids
     * @param oids
     * @param articlesTag
     */
    private static void collectOids(List<Long> oids, ArticlesObj articlesTag) {
        // 获取文章列表详细内容
        List<Articles> articlesList = articlesTag.getArticles();
        // 不为空且有内容
        if (CollUtil.isNotEmpty(articlesList)) {
            // 遍历
            for (Articles o : articlesList) {
                // 直接获取
                oids.add(o.getOId());
            }
        }
    }
    
    /**
     * 获取通知对象里的用户名
     * @param desc
     * @return
     */
    private static String getName(String desc) {
        // 正则
        Pattern compile = Pattern.compile(">(.*?)<");
        // 匹配项
        Matcher matcher = compile.matcher(desc);
        if (matcher.find()) {
            return matcher.group().replace("<", "").replace(">", "");
        }
        return null;
    }
    
}
