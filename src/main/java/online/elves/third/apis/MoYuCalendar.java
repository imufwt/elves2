package online.elves.third.apis;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import online.elves.third.apis.hotnews.TopurlNews;
import online.elves.third.apis.hotnews.topurl.*;
import online.elves.utils.DateUtil;
import online.elves.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * 日历工具.
 */
@Slf4j
public class MoYuCalendar {

    /**
     * 建站日期
     */
    private static int since = 2020;

    public static void main(String[] args) {
        log.info(getMyCal());
    }

    /**
     * 获取日历
     *
     * @return
     */
    public static String getMyCal() {
        // 摸鱼日历key
        String key = "fish:moyu:cal";
        // 获取缓存
        String cal = RedisUtil.get(key);
        if (StringUtils.isBlank(cal)) {
            // 组装对象
            StringBuilder my = new StringBuilder("> 鱼历 ");
            /* 处理新闻 这个一定是要有的*/
            // 新闻对象
            TopurlNews topurlNews = HotNews.getTopurlNews();
            if (Objects.isNull(topurlNews)) {
                return "抱歉, 我想我一定是遇见了时空乱流, 无法获取你想要的信息呢~ 老板, 你来看下...";
            }
            // 新闻对象
            News news = topurlNews.getData();
            // 时间对象
            Calendar calendar = news.getCalendar();
            // 当前时间
            LocalDate now = LocalDate.now();
            // 开始处理
            my.append(now.getYear() - since).append(" 年 ").append(calendar.getCMonth()).append(" 月 ").append(calendar.getCDay()).append("日 你摸鱼我摸鱼, 老板宝马变青桔").append(" ").append(" \n\n");
            // 获取假日明细
            Vocation.VocationDetail detail = Vocation.get();
            if (Objects.isNull(detail)) {
                my.append("#### 今天是个好日子 ").append(" \n\n");
            } else {
                // 一言
                my.append("### ").append(Vocation.getWord(detail)).append(" \n\n");
            }
            my.append("公历 ").append(DateUtil.formatDay(now)).append(" ").append(calendar.getNcWeek()).append(" ").append(" \n\n");
            my.append("农历 ").append(calendar.getYearCn()).append(" ").append(calendar.getMonthCn()).append(" ").append(calendar.getDayCn());
            my.append("(").append(calendar.getGzYear()).append(" 年 ").append(calendar.getGzMonth()).append(" 月 ").append(calendar.getGzDay()).append(" 日)").append(" ").append(" \n\n");
            // 每日诗词
            my.append("#### 每日一句, 整个活 ").append(" \n\n");
            Sentence sentence = news.getSentence();
            my.append(sentence.getSentence()).append(" -- ").append(sentence.getAuthor()).append(" \n\n");
            // 学成语
            my.append("#### 不知道这个对你的汉兜有帮助没 ").append(" \n\n");
            Phrase phrase = news.getPhrase();
            my.append("**").append(phrase.getPhrase()).append("** (").append(phrase.getPinyin()).append(") \n\n");
            // 新闻
            my.append("#### 抬眼看世界 ").append(" \n\n");
            // 新闻对象
            my.append(getNews(news.getNewsList()));
            my.append("#### 历史上的今天 ").append(" \n\n");
            // 历史上的今天
            List<HistoryList> historyList = news.getHistoryList();
            if (CollectionUtils.isNotEmpty(historyList)) {
                for (HistoryList th : historyList) {
                    my.append(th.getEvent()).append(" ").append(" \n\n");
                }
            } else {
                my.append("今天没啥事儿~ 哈哈哈哈哈");
            }
            my.append(" \n\n");
            my.append("> 试运行...有啥想说的记得提意见 改不改另说").append(" \n\n");
            // 缓存一小时
            RedisUtil.set(key, my.toString(), 60 * 60);
            return my.toString();
        } else {
            // 有缓存直接返回
            return cal;
        }
    }

    /**
     * 组装新闻
     *
     * @param newsList
     * @return
     */
    private static String getNews(List<NewsList> newsList) {
        // 组装对象
        StringBuilder n = new StringBuilder("<details><summary>每日随机看世界</summary><ul>");
        // 遍历新闻
        for (NewsList s : newsList) {
            n.append("<li><a href='").append(s.getUrl()).append("'>").append(s.getTitle()).append("</a></li>");
        }
        n.append("</ul></details> ").append(" \n\n");
        return n.toString();
    }

}
