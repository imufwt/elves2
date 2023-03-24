package online.elves.service.strategy.commands;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import online.elves.enums.CrLevel;
import online.elves.service.strategy.CommandAnalysis;
import online.elves.third.fish.Fish;
import online.elves.third.fish.model.FResp;
import online.elves.third.fish.model.articles.Articles;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 文章命令分析
 */
@Slf4j
@Component
public class ArticlesAnalysis extends CommandAnalysis {
    
    /**
     * 关键字
     */
    private static final List<String> keys = Arrays.asList("看帖");
    
    @Override
    public boolean check(String commonKey) {
        return keys.contains(commonKey);
    }
    
    @Override
    public void process(String commandKey, String commandDesc, String userName) {
        // 文章命令
        switch (commandKey) {
            case "看帖":
                // 获取一个随机的帖子
                FResp resp = Fish.getArticlesRandom(10);
                // 请求失败或者没有文章可以看
                if (!resp.isOk() || CollUtil.isEmpty(resp.getArticles())) {
                    Fish.sendMsg("亲爱的 @" + userName + " " + CrLevel.getCrLvName(userName) + " " + " 不知道干什么的话, 就来看帖子吧~\n\n [帖子大全](http://fishpi.cn/cr)");
                    return;
                }
                // 获取文章列表详细内容
                List<Articles> articles = resp.getArticles();
                // 需要返回的对象
                List<String> result = Lists.newArrayList();
                // 遍历
                for (Articles article : articles) {
                    // 构建 uri
                    String uri = "[" + article.getArticleTitle() + "(" + article.getArticleAuthorName() + ")] (https://fishpi.cn/article/" + article.getOId() + ")";
                    // 放入列表
                    result.add(uri);
                }
                Fish.sendMsg("亲爱的 @" + userName + " " + CrLevel.getCrLvName(userName) + " " + " 不知道干什么的话, 就来看帖子吧~\n\n" + result.get(new Random().nextInt(result.size())));
                break;
            default:
                // 什么也不用做
                break;
        }
    }
    
}
