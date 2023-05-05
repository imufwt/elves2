package online.elves.message.listener;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import online.elves.config.Const;
import online.elves.mapper.entity.MsgRecord;
import online.elves.mapper.entity.User;
import online.elves.message.event.CrCmdEvent;
import online.elves.service.FService;
import online.elves.third.fish.Fish;
import online.elves.utils.DateUtil;
import online.elves.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 聊天室消息监听
 * 合议庭
 */
@Slf4j
@Component
public class CollegiateBenchListener {
    /**
     * 禅定发起时间
     */
    public static String MEDITATION = "MEDITATION";
    public static String MEDITATION_LIMIT = "MEDITATION:LIMIT";
    public static String MEDITATION_JOIN = "MEDITATION:JOIN";
    /**
     * 破戒发起时间
     */
    public static String RELIEVE = "RELIEVE";
    public static String RELIEVE_LIMIT = "RELIEVE:LIMIT";
    public static String RELIEVE_JOIN = "RELIEVE:JOIN";
    // 规则
    public static String RULE = "## 合议庭(试行)\n" +
            "\n" +
            "合议前提: 除申请人和被申请人外, 其余满足条件人数应>=3人\n" +
            "\n" +
            "发起人/参与人/被申请人: 最近`30 min`在聊天室内聊天参与聊天次数大于`10`;\n" +
            "\n" +
            "> tips: 每次合议发起后有效期`60s`. 同一时间内只能发起一个合议决定";

    @Resource
    private FService fService;

    @EventListener(classes = {CrCmdEvent.class})
    public void exec(CrCmdEvent event) {
        // 事件消息 命令
        String cmd = event.getCmd();
        // 用户
        String userName = event.getSource().toString();
        // 替换连续空格并拆分换行
        cmd = cmd.trim().replaceAll(" + ", " ").replaceAll("<span[^>]*?>(</span>)*$", " ").split("\\n")[0];
        // 按照空格切分命令
        String[] commandKeys = cmd.split(" ");
        if (commandKeys.length < 2) {
            // 什么都不做, 我可是合议庭庭长哦
            if (cmd.equals("合议禅定") || cmd.equals("合议破戒")) {
                Fish.sendMsg(RULE);
                return;
            }
            return;
        }
        // 关键词
        String commandKey = commandKeys[1].trim();
        // 触发词汇
        switch (commandKeys[0]) {
            case "合议禅定":
                Fish.send2User(userName, "亲爱的, 合议禅定为应急预案, 为了避免用户滥用权利. 每次命令将消耗`64`积分. 请查看积分明细, 你的积分已被扣除!");
                meditation(userName, commandKey);
                break;
            case "合议破戒":
                relieve(userName, commandKey);
                break;
            default:
                // 什么都不足, 以后再填充 也可能是别的空格, 不过无所谓啦
                break;
        }
    }

    /**
     * 破戒
     *
     * @param sourceUser
     * @param targetUser
     */
    private void relieve(String sourceUser, String targetUser) {
        if (!checkSgy(targetUser)) {
            Fish.sendMsg("用户 " + targetUser + " 没有在思过崖面壁哦, 感谢你的仗义相救~");
            return;
        }
        if (targetUser.equals("dissoluteFate")) {
            Fish.sendMsg("抱歉, 鱼排VIP-风流无法通过合议解除~");
            return;
        }
        // 破戒对象
        String rLimit = RedisUtil.get(RELIEVE_LIMIT);
        // 不存在 则计算新发起
        if (StringUtils.isBlank(rLimit)) {
            // 删除参与人
            RedisUtil.del(RELIEVE_JOIN);
            // 设置对象
            RedisUtil.set(RELIEVE_LIMIT, targetUser, 60);
            if (sourceUser.equals("dissoluteFate")){
                // 设置当前参与人
                RedisUtil.set(RELIEVE_JOIN, JSON.toJSONString(Lists.newArrayList(sourceUser, sourceUser + "-左护法", sourceUser +"-右护法")));
                // 发送锁定通知
                Fish.sendMsg("请注意, 用户 @" + targetUser + " 正在被 `思过崖崖主` @" + sourceUser + " 发起合议破戒. 合议锁定 `60s` , 过期后本次发起失效!\n\n> " +
                        " 如果您赞成, 请发送命令  合议破戒 " + targetUser + " . 还需 `3` 个用户投票赞成");
            }else {
                // 设置当前参与人
                RedisUtil.set(RELIEVE_JOIN, JSON.toJSONString(Lists.newArrayList(sourceUser)));
                // 发送锁定通知
                Fish.sendMsg("请注意, 用户 @" + targetUser + " 正在被 @" + sourceUser + " 发起合议破戒. 合议锁定 `60s` , 过期后本次发起失效!\n\n> " +
                        " 如果您赞成, 请发送命令  合议破戒 " + targetUser + " . 还需 `5` 个用户投票赞成");
            }

        } else {
            // 计算被指定用户是否一致
            if (Objects.equals(RedisUtil.get(RELIEVE_LIMIT), targetUser)) {
                // 计算是否参与投票
                List<String> joins = JSON.parseArray(RedisUtil.get(RELIEVE_JOIN), String.class);
                if (joins.contains(sourceUser)) {
                    Fish.sendMsg("亲爱的 @" + sourceUser + " 你已经参与过啦\n\n> 感谢你的支持~");
                } else {
                    // 加入投票组
                    joins.add(sourceUser);
                    if ((joins.size() - 1) >= 5) {
                        // 公屏发送
                        Fish.sendMsg("用户 @" + targetUser + " 被合议庭投票通过, 解除禅定. 如有有异议, 请保留截图及消息及时私信反馈给OP/纪律!\n\n>" +
                                "参与人 " + JSON.toJSONString(joins));
                        // 禅定成功 发送强制禅定命令
                        Fish.sendCMD("执法 禁言 " + targetUser + " 0");
                        // 删除参与人
                        RedisUtil.del(RELIEVE_JOIN);
                        // 删除限制人
                        RedisUtil.del(RELIEVE_LIMIT);
                    } else {
                        // 回写joins
                        RedisUtil.set(RELIEVE_JOIN, JSON.toJSONString(joins));
                        // 重新限制对象
                        RedisUtil.set(RELIEVE_LIMIT, targetUser, 60);
                        // 发送锁定通知
                        Fish.sendMsg("投票有效! 请注意, 用户 @" + targetUser + " 正在被发起合议破戒. 合议锁定 `60s` , 过期后本次发起失效!\n\n> " +
                                " 如果您赞成, 请发送命令  合议破戒 " + targetUser + " . 还需 `" + (6 - joins.size()) + "` 个用户投票赞成");
                    }
                }
            } else {
                Fish.sendMsg("亲爱的 @" + sourceUser + " 你发起的针对用户 @" + targetUser + " 的合议破戒申请无法生效\n\n> " +
                        "当前在用户 " + RedisUtil.get(RELIEVE_LIMIT) + " 的被合议破戒投票中");
            }
        }
    }

    /**
     * 检查人在不在思过崖
     *
     * @param targetUser
     * @return
     */
    private boolean checkSgy(String targetUser) {
        String resp = HttpUtil.get("https://fishpi.cn/chat-room/si-guo-list");
        if (StringUtils.isBlank(resp)) {
            return false;
        }
        JSONObject parsed = JSON.parseObject(resp);
        // 获取对象
        JSONArray data = parsed.getJSONArray("data");
        return data.stream().anyMatch(x -> {
            JSONObject object = (JSONObject) x;
            return object.getOrDefault("userName", "").equals(targetUser);
        });
    }

    /**
     * 禅定
     *
     * @param sourceUser
     * @param targetUser
     */
    private void meditation(String sourceUser, String targetUser) {
        if (RedisUtil.get(Const.OP_LIST).contains(targetUser)) {
            Fish.sendMsg("@" + sourceUser + " 你说你没事儿, 招惹他们干嘛~");
            return;
        }
        if (Objects.equals(targetUser, RedisUtil.get(Const.ADMIN))) {
            Fish.sendMsg("@" + sourceUser + " 打住...不要继续说了~");
            return;
        }
        if (Objects.equals(sourceUser, targetUser)) {
            Fish.sendMsg("@" + sourceUser + " 怎么还自己ban自己呢~");
            return;
        }
        if (checkSgy(targetUser)) {
            Fish.sendMsg("用户 " + targetUser + " 正在思过崖面壁, 发起合议禅定失败~");
            return;
        }
        // 当前时间
        LocalDateTime now = LocalDateTime.now();
        // 禅定对象列表
        String meditation = RedisUtil.get(MEDITATION);
        // 判断发起/参与人是否有资格
        User source = fService.getUser(sourceUser);
        User target = fService.getUser(targetUser);
        if (Const.ROBOT_LIST.contains(target.getUserNo())) {
            Fish.sendMsg("@" + sourceUser + " 我们可是官方机器人哦, 诶嘿嘿~ Ban我们, 想Pitch~");
            return;
        }
        // 不存在 则计算新发起
        if (StringUtils.isBlank(meditation)) {
            // 首先清除上一轮合议成员
            RedisUtil.del(MEDITATION_JOIN);
            // 清除限定者
            RedisUtil.del(MEDITATION_LIMIT);
            // 获取最近三十分钟的所有发言人  直接撸吧 至少一条
            List<MsgRecord> msgRecords = fService.hasEntitlement(now.minusMinutes(30), now);
            // 分组对象
            Map<Integer, List<MsgRecord>> collected = msgRecords.stream().collect(Collectors.groupingBy(MsgRecord::getUserNo));
            // 拥有限制的对象
            List<Integer> limitsUser = Lists.newArrayList();
            // 循环对象
            for (Integer key : collected.keySet()) {
                // 临时对象
                List<MsgRecord> tmp = collected.getOrDefault(key, Lists.newArrayList());
                if (tmp.size() > 10) {
                    // 满足十次
                    limitsUser.add(key);
                }
            }
            // 最少要有三个人, 俩人投票才算
            if (limitsUser.size() < 5) {
                Fish.sendMsg("亲爱的 @" + sourceUser + " 当前聊天室发言人数不符合合议庭的发起条件\n\n> " +
                        "发起合议前提. 需要在发起合议前 30 min 在聊天室发言超过 10 次的人数大于 5 人.(~~除去发起和被发起的还要剩下三个哦~~)");
                return;
            }
            // 判断申请人权限
            if (limitsUser.contains(source.getUserNo())) {
                // 移除申请人
                limitsUser.remove(source.getUserNo());
                // 判断被申请人权限
                if (limitsUser.contains(target.getUserNo())) {
                    // 可以搞  移除申请人和被申请人后剩下的列表
                    limitsUser.remove(target.getUserNo());
                    // 可参与人写入redis 60 s 锁定
                    RedisUtil.set(MEDITATION, JSON.toJSONString(limitsUser), 60);
                    // 已参与人
                    RedisUtil.set(MEDITATION_JOIN, JSON.toJSONString(Lists.newArrayList(sourceUser)));
                    // 限定者
                    RedisUtil.set(MEDITATION_LIMIT, targetUser);
                    // 发送锁定通知
                    Fish.sendMsg("请注意, 用户 @" + targetUser + " 正在被 @" + sourceUser + " 发起合议禅定. 合议锁定 `60s` , 过期后本次发起失效!\n\n> " +
                            " 如果您赞成, 请发送命令  合议禅定 " + targetUser + " . 还需 `" + (limitsUser.size() / 3 + 1) + "` 个用户投票赞成");
                } else {
                    Fish.sendMsg("亲爱的 @" + sourceUser + " 你发起的针对用户 @" + targetUser + " 的合议禅定申请无法生效\n\n> 被禅定用户 [" +
                            targetUser + "] 需要在发起合议前 30 min 在聊天室发言超过 10 次.(~~人都没怎么说话, 你禅定什么?~~)");
                }
            } else {
                Fish.sendMsg("亲爱的 @" + sourceUser + " 你不符合合议庭的发起合议资格\n\n> " +
                        "发起/参与 合议. 需要在发起合议前 30 min 在聊天室发言超过 10 次.(~~主要防止权利滥用~~)");
            }
        } else {
            // 反序列化 拥有限制的对象
            List<Integer> limitsUser = JSON.parseArray(meditation, Integer.class);
            if (limitsUser.contains(source.getUserNo())) {
                // 计算被指定用户是否一致
                if (Objects.equals(RedisUtil.get(MEDITATION_LIMIT), targetUser)) {
                    // 计算是否参与投票
                    List<String> joins = JSON.parseArray(RedisUtil.get(MEDITATION_JOIN), String.class);
                    if (joins.contains(sourceUser)) {
                        Fish.sendMsg("亲爱的 @" + sourceUser + " 你已经参与过啦\n\n> 是什么让你这么迫切的想要禅定他呢?");
                    } else {
                        // 加入投票组
                        joins.add(sourceUser);
                        if ((joins.size() - 1) >= (limitsUser.size() / 3 + 1)) {
                            // 公屏发送
                            Fish.sendMsg("用户 @" + targetUser + " 被合议庭投票通过, 执行强行禅定. 如有有异议, 请保留截图及消息及时私信反馈给OP/纪律!\n\n>" +
                                    "参与人 " + JSON.toJSONString(joins));
                            // 禅定成功 发送强制禅定命令
                            Fish.sendCMD("执法 禁言 " + targetUser + " 10");
                            // 清除上一轮合议成员
                            RedisUtil.del(MEDITATION_JOIN);
                            // 清除限定者
                            RedisUtil.del(MEDITATION_LIMIT);
                        } else {
                            // 回写joins
                            RedisUtil.set(MEDITATION_JOIN, JSON.toJSONString(joins));
                            // 可参与人写入redis 60 分钟锁定
                            RedisUtil.set(MEDITATION, JSON.toJSONString(limitsUser), 60);
                            // 发送锁定通知
                            Fish.sendMsg("投票有效! 请注意, 用户 @" + targetUser + " 正在被发起合议禅定. 合议锁定 `60s` , 过期后本次发起失效!\n\n> " +
                                    " 如果您赞成, 请发送命令  合议禅定 " + targetUser + " . 还需 `" + (limitsUser.size() / 3 + 2 - joins.size()) + "` 个用户投票赞成");
                        }
                    }
                } else {
                    Fish.sendMsg("亲爱的 @" + sourceUser + " 你发起的针对用户 @" + targetUser + " 的合议禅定申请无法生效\n\n> " +
                            "当前在用户 " + RedisUtil.get(MEDITATION_LIMIT) + " 的被合议禅定投票中");
                }
            } else {
                Fish.sendMsg("亲爱的 @" + sourceUser + " 你不符合合议庭的参与合议资格\n\n> " +
                        "参与合议. 需要在发起合议前 30 min 在聊天室发言超过 10 次.(~~主要防止权利滥用~~)");
            }
        }
    }
}