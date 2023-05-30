package online.elves.message.listener;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import online.elves.config.Const;
import online.elves.enums.CrLevel;
import online.elves.mapper.entity.User;
import online.elves.message.event.CrMsgEvent;
import online.elves.service.FService;
import online.elves.third.apis.Letter;
import online.elves.third.fish.Fish;
import online.elves.utils.DateUtil;
import online.elves.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

/**
 * 聊天室消息监听
 */
@Slf4j
@Component
public class CrMsgListener {

    @Resource
    FService fService;

    @EventListener(classes = {CrMsgEvent.class})
    public void exec(CrMsgEvent event) {
        // 事件消息 发送人
        Integer userNo = event.getUserNo();
        // 事件消息 发送人
        String userName = event.getSource().toString();
        // 不存在消息, 没说过话的用户编号大于12863 2023年03月30日17:21:40 时的最后一个人, 都当是新人吧~
        if (userNo > 12863 && isNew(userNo)) {
            welcome(userNo, userName);
        } else {
            halo(userNo, userName);
        }
        // 机器人和OP豁免
        if (!Const.ROBOT_LIST.contains(userNo) && !Objects.requireNonNull(RedisUtil.get(Const.OP_LIST)).contains(userName)) {
            // 敏感词判定
            String sw = RedisUtil.get(Const.SENSITIVE_WORDS);
            if (StringUtils.isNotBlank(sw)) {
                // 有敏感词存在
                List<String> sws = JSON.parseArray(sw, String.class);
                if (CollUtil.isNotEmpty(sws)) {
                    // 消息内容
                    String md = event.getMd();
                    // 循环敏感词
                    for (String s : sws) {
                        if (StringUtils.isNotBlank(s) && md.contains(s)) {
                            // 有 敏感词... 处理掉 1 个就可以嘞.
                            revokeJudge(userNo, userName, s, event.getOid());
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * 撤回判定
     *
     * @param userNo
     * @param userName
     * @param keyWord
     * @param oid
     */
    private void revokeJudge(Integer userNo, String userName, String keyWord, Long oid) {
        // 限定次数
        Integer limit = Integer.valueOf(RedisUtil.get(Const.SENSITIVE_WORDS_LIMIT));
        // 缓存对象
        String key = Const.SENSITIVE_WORDS_PREFIX + userName;
        // 计数器
        String s = RedisUtil.get(key);
        // 十五分钟计时器
        Integer count = Integer.valueOf(StringUtils.isBlank(s) ? "0" : s);
        // 撤回对象
        Fish.revoke(oid);
        // 敏感词预警
        Fish.send2User(userName, "⚠️敏感词预警⚠️ \n\n 撤回消息包含敏感词[" + keyWord + "], 请规范言行!");
        // 撤回+封禁
        if (count > limit) {
            // 处罚通告
            Fish.sendMsg("🚨处罚通报🚨 \n\n 用户@" + userName + " 连续使用敏感词[`" + limit + "`次], 触发处罚. 禁言`15`分钟! 请注意言辞, 维护和谐聊天室环境!");
            // 封禁
            Fish.sendCMD("执法 禁言 " + userName + " 15");
            // 删除判定
            RedisUtil.del(key);
        } else {
            // 撤回+警告
            // 处罚通告
            Fish.sendMsg("⚠️敏感词处罚预警⚠️ \n\n 用户 @" + userName + " 请注意. 检测到您的消息内容包含敏感词, 消息已被撤回. 请注意言辞, 维护和谐聊天室环境! \n\n >  连续使用敏感词[`" + limit + "`次]后, 将触发禁言`15`分钟! 您当前剩余[`" + (limit - count) + "`次]");
            // 计数器+1
            RedisUtil.reSet(key, String.valueOf(count + 1), 15 * 60);
        }
    }

    /**
     * 迎新
     *
     * @param userNo
     * @param userName
     */
    public void welcome(Integer userNo, String userName) {
        // 获取会员信息
        User user = fService.getUser(userNo, userName);
        // 构建返回
        StringBuilder content = new StringBuilder(user.getUserNick());
        content.append("( @").append(user.getUserName()).append(" ").append(CrLevel.getCrLvName(userName)).append(" ) ");
        content.append("新晋鱼油 ❤️️ 你好 :").append(" \n\n");
        content.append("----").append(" \n\n");
        content.append(" 👏🏻欢迎来到[**摸鱼派**](https://fishpi.cn) ，我是摸鱼派的＜**礼仪委员**>江户川-哀酱( @APTX-4869 ) 的好朋友 **精灵(我也是礼仪委员哦~)**，你在社区摸鱼期间遇到的疑问都可以私信他哦。").append(" \n\n");
        content.append("----").append(" \n\n");
        content.append("#### 你可以尝试下面的操作, 完成一些必要的设置, 以便让大家更好的认识你").append(" \n\n");
        content.append("- 首先").append(" \n");
        content.append(" -- 你可以在此 [**发帖**](https://fishpi.cn/post?type=0) 此引用“`新人报道`”的标签可以晋升正式成员~").append(" \n");
        content.append("- 其次").append(" \n");
        content.append(" -- 你可以修改个人信息, 例如给自己起一个帅气的名字 [点我修改名字](https://fishpi.cn/settings), 也可以设置一个吸引人的头像 [点我修改头像](https://fishpi.cn/settings/avatar)").append(" \n\n");
        content.append("> Tips: 正式成员可以赞同帖子/点踩帖子/艾特用户/指定帖子等功能,详细介绍请移步 [【公告】摸鱼派会员等级规则 ](https://fishpi.cn/article/1630575841478)").append(" \n");
        content.append("----").append(" \n\n");
        content.append("#### 下面几个守则也可以让你快速融入了解摸鱼派社区").append(" \n\n");
        content.append("1. **摸鱼守则**： https://fishpi.cn/article/1631779202219").append(" \n\n");
        content.append("2. **新人手册**： https://fishpi.cn/article/1630569106133").append(" \n\n");
        content.append("3. **积分规则**： https://fishpi.cn/article/1630572449626").append(" \n\n");
        content.append("4. **活跃度**： https://fishpi.cn/article/1683775497629").append(" \n\n");
        content.append("----").append(" \n\n ");
        content.append("> 当然我也有一些好玩的功能, 你可以使用指令 `凌 菜单` 或 `凌 帮助` 来查看一些指令, 祝你在摸鱼派摸的开心❤️").append(" \n\n ");
        // 发送消息
        Fish.sendMsg(content.toString());
        // 欢迎之后 写入记录
        RedisUtil.incrScore(Const.CHAT_ROOM_WELCOME, userNo.toString(), Long.valueOf(LocalDate.now().toEpochDay()).intValue());
    }

    /**
     * 打招呼
     *
     * @param userNo
     * @param userName
     */
    public void halo(Integer userNo, String userName) {
        // 获取用户
        User user = fService.getUser(userNo, userName);
        // 机器人. 就不打招呼了
        if (!Const.ROBOT_LIST.contains(userNo)) {
            // 当前时间
            LocalDateTime now = LocalDateTime.now();
            // 当前日
            String day = DateUtil.formatDay(now.toLocalDate());
            // 获取最后一次打招呼日期
            String last = RedisUtil.get(Const.LAST_HALO_PREFIX + userNo);
            // 为空 或者不是今天, 铁定要打招呼
            if (StringUtils.isBlank(last) || !last.equals(day)) {
                // 差异天数
                Long diff;
                if (StringUtils.isBlank(last)) {
                    diff = 0L;
                } else {
                    diff = DateUtil.getInterval(DateUtil.parseLd(last).atStartOfDay().plusDays(1), now, ChronoUnit.DAYS);
                }
                // 打招呼增加的内容, 几天没回来
                String msg = "";
                if (diff > 2) {
                    msg = "你已经 " + diff + " 天没有来聊天啦~ 欢迎回来...";
                }
                Fish.sendMsg("亲爱的 " + (StringUtils.isBlank(user.getUserNick()) ? userName : user.getUserNick()) + " " + CrLevel.getCrLvName(userName) + " " + msg + hello(now));
                // 回写打招呼日期
                RedisUtil.set(Const.LAST_HALO_PREFIX + userNo, day);
            }
        }
    }

    /**
     * 是否是聊天室新人
     *
     * @param userNo
     * @return
     */
    private boolean isNew(Integer userNo) {
        Double score = RedisUtil.getScore(Const.CHAT_ROOM_WELCOME, userNo.toString());
        if (Objects.nonNull(score)) {
            return false;
        }
        return true;
    }

    /**
     * 欢迎词
     *
     * @param now
     * @return
     */
    private static String hello(LocalDateTime now) {
        // 小时数
        int hour = now.getHour();
        if (hour >= 0 && hour <= 3) {
            return " 现在是宵禁时间, 夜深了你还不睡么? \n\n > " + Letter.getOneWord();
        }
        if (hour > 3 && hour <= 6) {
            return " 现在是宵禁时间, 天快亮了, 你是没睡还是醒了? \n\n > " + Letter.getOneWord();
        }
        if (hour > 6 && hour < 8) {
            return " 现在是宵禁时间, 天亮了, 早上好呀! \n\n > " + Letter.getOneWord();
        }
        if (hour >= 8 && hour <= 10) {
            return " 一日之计在于晨, 摸鱼咯! \n\n > " + Letter.getOneWord();
        }
        if (hour > 10 && hour <= 11) {
            return " 摸鱼辛苦了! 是时候点个饭犒劳下自己了~ \n\n > " + Letter.getOneWord();
        }
        if (hour >= 12 && hour <= 14) {
            return " 中午好呀, 吃饭了么? \n\n > " + Letter.getOneWord();
        }
        if (hour > 14 && hour <= 15) {
            return " 吃饱喝足, 下午继续? 动次打次摸起来 \n\n > " + Letter.getOneWord();
        }
        if (hour > 15 && hour <= 18) {
            return " 摸鱼累了么? 站起来休息会儿吧~ 做个提肛运动也是极好的! \n\n > " + Letter.getOneWord();
        }
        if (hour == 19 && now.getMinute() <= 30) {
            return " 马上要宵禁啦~ 水满了么? 要加油哦! \n\n > " + Letter.getOneWord();
        }
        if (hour == 19 && now.getMinute() > 30) {
            return " 现在是宵禁时间! 没水满也不要紧, 还是可以聊天哒~ \n\n > " + Letter.getOneWord();
        }
        if (hour > 19 && hour <= 22) {
            return " 现在是宵禁时间! 努力了一天, 还不准备下班么~ \n\n > " + Letter.getOneWord();
        }
        if (hour > 22 && hour <= 23) {
            return " 现在是宵禁时间! 准备洗洗睡吧, 做个好梦~ \n\n > " + Letter.getOneWord();
        }
        return " 你好呀~ \n\n > " + Letter.getOneWord();
    }

}