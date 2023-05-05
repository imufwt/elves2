package online.elves.task.service;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import online.elves.config.Const;
import online.elves.mapper.entity.CurrencyLog;
import online.elves.mapper.entity.User;
import online.elves.service.FService;
import online.elves.third.fish.Fish;
import online.elves.third.fish.model.FResp;
import online.elves.third.fish.model.FUser;
import online.elves.third.fish.model.articles.*;
import online.elves.utils.DateUtil;
import online.elves.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 活动服务.
 */
@Slf4j
@Component
public class TaskService {

    @Resource
    FService fService;

    /**
     * 购买鱼翅
     */
    public void buyCurrency() {
        log.info("购买鱼翅...开始处理");
        // 获取购买人
        List<CurrencyLog> buyer = fService.getBuyer();
        // 没有购买者
        if (CollUtil.isEmpty(buyer)) {
            log.info("开包, 没有购买者...结束");
            return;
        }
        // 遍历购买者
        for (CurrencyLog cl : buyer) {
            // 消息对象
            String msg = RedisUtil.get(cl.getOid().toString());
            if (StringUtils.isBlank(msg)) {
                // 没找到
                log.info("没有找到信息[{}]的缓存记录.", cl.getOid());
                continue;
            }
            // 鱼翅对象
            FService.Currency currency = JSON.parseObject(msg, FService.Currency.class);
            // 购买
            fService.buyCurrency(currency.getOid(), currency.getUser(), currency.getMoney(), currency.getRate(), currency.isHappy());
            // 删除缓存
            RedisUtil.del(cl.getOid().toString());
        }
        // 修改状态
        fService.updateBuyer(buyer);
    }

    /**
     * 迎新
     */
    public void welcomeV1() {
        log.info("...开始迎新 v1.0 ...");
        // 直接获取最近的二十个注册大哥 一分钟一次
        List<FUser> users = Fish.getRecentRegs();
        // 批量处理
        for (FUser user : users) {
            // 查询用户信息
            User ut = fService.getUser(null, user.getUserName());
            // 获取用户打招呼记录
            Double score = RedisUtil.getScore(Const.RANKING_PREFIX + "WELCOME", ut.getUserNo().toString());
            if (Objects.nonNull(score)) {
                log.info("用户 {}  已欢迎过了..., 跳过", ut.getUserName());
                continue;
            }
            // 开始欢迎
            log.info("开始欢迎用户 {} ", ut.getUserName());
            // 记录欢迎的纪元日
            RedisUtil.incrScore(Const.RANKING_PREFIX + "WELCOME", ut.getUserNo().toString(), Long.valueOf(LocalDate.now().toEpochDay()).intValue());
            // 组织消息
            String content = ut.getUserNick() + ": \n\n" +
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
            Fish.send2User(ut.getUserName(), content);
        }
    }

    /**
     * 检查新人报道
     * 使用最近帖子, 循环获取最近五十个帖子, 然后检查标题和标签
     * 符合就回复
     */
    public void runCheckV1() {
        log.info("...检查新人报道 1.0 ...");
        // 遍历文章列表
        List<Long> ids = Lists.newArrayList();
        // 获取最近帖子列表 五分钟 20个应该OK吧? 新人不会一起这么报道吧???
        FResp resp = Fish.getArticlesRecent(1, 20);
        if (resp.isOk()) {
            ArticlesObj obj = JSON.parseObject(JSON.toJSONString(resp.getData()), ArticlesObj.class);
            collectOids(ids, obj);
        }
        // 获取新人报道对象
        resp = Fish.getArticlesTag("新人报道", 0, 1, 5);
        if (resp.isOk()) {
            ArticlesObj obj = JSON.parseObject(JSON.toJSONString(resp.getData()), ArticlesObj.class);
            collectOids(ids, obj);
        }
        resp = Fish.getArticlesTag("新人报到", 0, 1, 5);
        if (resp.isOk()) {
            ArticlesObj obj = JSON.parseObject(JSON.toJSONString(resp.getData()), ArticlesObj.class);
            collectOids(ids, obj);
        }
        // 不应该为空的
        if (CollUtil.isEmpty(ids)) {
            log.info("未获取到 新人报道 / 新人报到 列表...请及时检查");
            return;
        }
        // 遍历文章列表 回帖
        for (Long id : ids) {
            // 回帖
            checkReply(id);
        }
        log.info("...检查新人报道 1.0 结束...");
    }

    /**
     * 检查文章是否是新人报道
     * 是的话  检查是否已经回复
     * 没有的话就回复一下
     *
     * @param id
     */
    private void checkReply(Long id) {
        Double score = RedisUtil.getScore(Const.WELCOME_CHECK_REPLY, id.toString());
        if (Objects.nonNull(score)) {
            // 已经评论过了
            log.info("文章...OID:{}...已经评论...{}", id, score);
            return;
        }
        // 获取文章详细内容
        FResp resp = Fish.getArticle(id, 1);
        if (resp.isOk()) {
            // 反序列化
            ArticleObj obj = JSON.parseObject(JSON.toJSONString(resp.getData()), ArticleObj.class);
            // 获取文章
            Article article = obj.getArticle();
            // 所有评论列表  初始化第一页
            List<ArticleComments> comments = Lists.newArrayList(article.getArticleComments());
            // 多页情况下翻页
            if (obj.getPagination().getPaginationPageCount() > 1) {
                // 直接从第二页开始, 相信接口. 默认OK
                for (int i = 2; i < obj.getPagination().getPaginationPageCount() + 1; i++) {
                    resp = Fish.getArticle(id, 1);
                    // 反序列化
                    obj = JSON.parseObject(JSON.toJSONString(resp.getData()), ArticleObj.class);
                    // 获取文章
                    article = obj.getArticle();
                    comments.addAll(article.getArticleComments());
                }
            }
            // 评论人
            if (hasReply(id, comments)) {
                // 已经回复过了
                log.info("文章...OID:{}...已经评论", id);
                return;
            }
            // 检查标题是否包含新人报道
            if (hasNewMsg(article.getArticleTitle())) {
                // 是的话就回复
                reply(id);
                // 然后返回继续
                return;
            }
            // 检查标签是否包含新人报道
            if (hasNewMsg(article.getArticleTags())) {
                // 是的话就回复
                reply(id);
                // 然后返回继续
                return;
            }
            // 检查内容是否包含新人报道
            if (hasNewMsg(article.getArticleOriginalContent())) {
                // 是的话就回复
                reply(id);
                // 然后返回继续
                return;
            }
            // 不是新人报道
            return;
        }
        log.info("文章...OID:{}...获取详情失败", id);
    }

    /**
     * 是否包含特定文本
     *
     * @param str
     * @return
     */
    private boolean hasNewMsg(String str) {
        return str.contains("新手报道") || str.contains("新手报到") || str.contains("新人报道") || str.contains("新人报到");
    }

    /**
     * 是否自己已经回复过了
     *
     * @param comments
     * @return
     */
    private boolean hasReply(Long id, List<ArticleComments> comments) {
        // 遍历评论
        for (ArticleComments ac : comments) {
            // 有精灵回复过 就直接返回
            if (Objects.equals(ac.getCommentAuthorName(), RedisUtil.get(Const.ELVES_MAME))) {
                // 评论创建时间
                LocalDate crDate = Objects.requireNonNull(DateUtil.parseLdt(ac.getCommentCreateTimeStr())).toLocalDate();
                // 记录回复记录
                RedisUtil.incrScore(Const.WELCOME_CHECK_REPLY, id.toString(), Long.valueOf(crDate.toEpochDay()).intValue());
                return true;
            }
        }
        return false;
    }

    /**
     * 回复帖子
     *
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
                "你可以修改个人信息\n" +
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
        // 记录回复记录
        RedisUtil.incrScore(Const.WELCOME_CHECK_REPLY, id.toString(), Long.valueOf(LocalDate.now().toEpochDay()).intValue());
    }

    /**
     * 收集 oids
     *
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
     *
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
