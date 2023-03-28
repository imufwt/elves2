package online.elves.service.strategy.commands;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import online.elves.enums.CrLevel;
import online.elves.service.strategy.CommandAnalysis;
import online.elves.third.fish.Fish;
import online.elves.third.fish.model.articles.Articles;
import org.apache.commons.lang3.StringUtils;
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
                String resp = Fish.getArticlesRandom(10);
                // 请求失败或者没有文章可以看
                if (StringUtils.isBlank(resp)) {
                    Fish.sendMsg("亲爱的 @" + userName + " " + CrLevel.getCrLvName(userName) + " " + " 不知道干什么的话, 就来看帖子吧~\n\n [帖子大全](http://fishpi.cn/cr)");
                    return;
                }
                // 获取文章列表详细内容
                List<Articles> articles = JSON.parseObject(resp).getObject("articles", new TypeReference<List<Articles>>() {
                
                });
                // 需要返回的对象
                List<String> result = Lists.newArrayList();
                // 遍历
                for (Articles article : articles) {
                    // 构建 uri
                    String uri = "[" + article.getArticleTitle() + "(" + article.getArticleAuthorName() + ")](https://fishpi.cn/article/" + article.getOId() + ")";
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
