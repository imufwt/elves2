package online.elves.service;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Maps;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import online.elves.config.Const;
import online.elves.enums.CrLevel;
import online.elves.mapper.*;
import online.elves.mapper.entity.*;
import online.elves.message.Publisher;
import online.elves.message.event.CrCmdEvent;
import online.elves.message.event.CrMsgEvent;
import online.elves.third.fish.Fish;
import online.elves.third.fish.model.FUser;
import online.elves.utils.DateUtil;
import online.elves.utils.RedisUtil;
import online.elves.utils.RegularUtil;
import online.elves.utils.StrUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * 鱼排服务对象
 */
@Slf4j
@Component
public class FService {

    @Resource
    Publisher publisher;

    @Resource
    UserMapper userMapper;

    @Resource
    DistrictCnMapper districtCnMapper;

    @Resource
    MsgRecordMapper msgRecordMapper;

    @Resource
    CurrencyMapper currencyMapper;

    @Resource
    RpRecordMapper rpRecordMapper;

    @Resource
    RpOpenLogMapper rpOpenLogMapper;

    /**
     * 查询用户
     *
     * @param userName
     * @return
     */
    public User getUser(String userName) {
        // 不检查参数了, 都是自己人
        QueryWrapper<User> cond = new QueryWrapper<>();
        cond.eq("user_name", userName);
        // 一次只有一个
        return userMapper.selectOne(cond);
    }

    /**
     * 查询用户
     *
     * @param userNo
     * @param userName
     * @return
     */
    public User getUser(Integer userNo, String userName) {
        // 获取用户 每次都查询一下. 防止改昵称
        FUser fUser = Fish.getUser(userName);
        // 相信阿达
        assert fUser != null;
        // 不检查参数了, 都是自己人
        QueryWrapper<User> cond = new QueryWrapper<>();
        // 用户编号
        if (Objects.nonNull(userNo)) {
            cond.eq("user_no", userNo);
        } else {
            if (StringUtils.isNotBlank(userName)) {
                cond.eq("user_name", userName);
            } else {
                // 都没有就查不到了
                return null;
            }
        }
        // 一次只有一个
        User user = userMapper.selectOne(cond);
        // 差不多多查询一次
        if (Objects.isNull(user)) {
            cond = new QueryWrapper<>();
            cond.eq("user_no", fUser.getUserNo());
            user = userMapper.selectOne(cond);
        }
        // 实在没有再更新
        if (Objects.isNull(user)) {
            // 新增
            user = new User();
            user.setUserNo(fUser.getUserNo());
            user.setUserName(userName);
            user.setUserNick(fUser.getUserNickname());
            user.setCreateTime(LocalDateTime.now());
            // 保存对象
            userMapper.insert(user);
        } else {
            // 这么邪性? 一起改了?
            if (Objects.nonNull(userNo) && !Objects.equals(userNo, fUser.getUserNo())) {
                user.setUserNo(fUser.getUserNo());
            }
            // 应该是改名了, 修改一下子
            user.setUserName(userName);
            user.setUserNick(fUser.getUserNickname());
            // 修改对象
            userMapper.updateById(user);
        }
        // 返回
        return user;
    }

    /**
     * 批量获取用户
     *
     * @param userNos
     * @return
     */
    public Map<Integer, String> getUserMap(List<Integer> userNos) {
        // 不检查参数了, 都是自己人
        QueryWrapper<User> cond = new QueryWrapper<>();
        // 用户编号
        cond.in("user_no", userNos);
        // 获取的用户
        List<User> users = userMapper.selectList(cond);
        if (CollUtil.isEmpty(users)) {
            return Maps.newHashMap();
        }
        // 返回的对象
        Map<Integer, String> map = Maps.newHashMap();
        users.forEach(x -> {
            map.put(x.getUserNo(), x.getUserNick() + "(" + x.getUserName() + ")");
        });
        return map;
    }

    /**
     * 接收消息
     *
     * @param userName
     * @param oId
     * @param md
     * @param content
     * @param isMsg
     */
    @Async("threadPool")
    public void recMsg(String userName, Long oId, String md, String content, boolean isMsg) {
        try {
            // 获取用户
            User user = getUser(null, userName);
            // 没找到就走一遍鱼排查询
            if (Objects.isNull(user)) {
                FUser fUser = Fish.getUser(userName);
                assert fUser != null;
                user = getUser(fUser.getUserNo(), null);
            }
            // 获取用户编码 自己人 不用检查了
            Integer user_no = user.getUserNo();
            if (Objects.isNull(user_no)) {
                return;
            }
            // 当前时间
            LocalDateTime now = LocalDateTime.now();
            // 构建信息记录对象
            MsgRecord record = new MsgRecord();
            record.setOid(oId);
            record.setUserNo(user_no);
            // 类型 不是消息就是红包
            int type = isMsg ? switchType(md) : 0;
            // 不是小冰和精灵 这些机器人. 计入排行榜
            if (!Const.ROBOT_LIST.contains(user_no)) {
                // 用户编号
                String uNo = user_no.toString();
                // 当前日
                Date time = DateUtil.ld2UDate(now.toLocalDate());
                // 类型日榜
                RedisUtil.incrScore(StrUtils.getKey(Const.RANKING_DAY_PREFIX, type + "", DateUtil.format(time, "yyyyMMdd")), uNo, 1);
                // 日榜
                RedisUtil.incrScore(StrUtils.getKey(Const.RANKING_DAY_PREFIX, "20", DateUtil.format(time, "yyyyMMdd")), uNo, 1);
                // 周榜
                RedisUtil.incrScore(StrUtils.getKey(Const.RANKING_WEEK_PREFIX, "21", now.getYear() + "", now.toLocalDate().get(WeekFields.ISO.weekOfWeekBasedYear()) + ""), uNo, 1);
                // 月榜
                RedisUtil.incrScore(StrUtils.getKey(Const.RANKING_MONTH_PREFIX, "22", now.getYear() + "", now.getMonth().getValue() + ""), uNo, 1);
                // 年榜
                RedisUtil.incrScore(StrUtils.getKey(Const.RANKING_YEAR_PREFIX, "23", now.getYear() + ""), uNo, 1);
                // 写入排行榜
                RedisUtil.incrScore(StrUtils.getKey(Const.RANKING_PREFIX, "24"), uNo, 1);
            }
            if (user_no == 8888 && md.contains("56c0f695")) {
                Fish.sendMsg("![让你乌拉](https://pwl.stackoverflow.wiki/2022/03/image-e4191ea4.png)");
            }
            record.setType(type);
            // 弹幕另外处理
            record.setContent(isMsg ? type == 7 ? content : md : content);
            record.setCreateTime(LocalDateTime.now());
            // 保存记录
            msgRecordMapper.insert(record);
            // 发送消息记录
            publisher.send(new CrMsgEvent(userName, user_no));
            // 文字消息再计算能活跃 弹幕不算
            if (isMsg) {
                // 计算用户活跃度
                incActivity(userName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("没了就没了吧...{}", e.getMessage());
        }
        // 关键词触发
        if (StringUtils.isNotBlank(md)) {
            // 校验执行命令
            publisher.send(new CrCmdEvent(userName, md));
        }
    }

    /**
     * 鱼翅购买记录
     *
     * @param oId
     * @param whoGive
     * @param money
     */
    @Async("threadPool")
    public void recordCurrency(Long oId, String whoGive, Integer money) {
        try {
            // 当前时间
            LocalDateTime now = LocalDateTime.now();
            // 鱼翅购买记录
            CurrencyLog codeLog = new CurrencyLog();
            codeLog.setOid(oId);
            codeLog.setUserName(whoGive);
            codeLog.setMoney(money);
            // 未领取
            codeLog.setState(0);
            // 保存记录
            currencyMapper.insert(codeLog);
            // 写入榜单
            // 获取用户ID 从鱼排走...比较铁 防止改名本地未修改
            User user = getUser(null, whoGive);
            // 用户编号
            Integer user_no = user.getUserNo();
            if (Objects.isNull(user_no)) {
                log.warn("{}...写入榜单失败, 无法获取用户UserNo", whoGive);
                return;
            }
            // 字符串
            String userNo = user_no.toString();
            // 日榜
            RedisUtil.incrScore(StrUtils.getKey(Const.RANKING_DAY_PREFIX, "10", DateUtil.format(DateUtil.ld2UDate(now.toLocalDate()), "yyyyMMdd")), userNo, money);
            // 周榜
            RedisUtil.incrScore(StrUtils.getKey(Const.RANKING_WEEK_PREFIX, "11", String.valueOf(now.getYear()), String.valueOf(now.toLocalDate().get(WeekFields.ISO.weekOfWeekBasedYear()))), userNo, money);
            // 月榜
            RedisUtil.incrScore(StrUtils.getKey(Const.RANKING_MONTH_PREFIX, "12", String.valueOf(now.getYear()), String.valueOf(now.getMonth().getValue())), userNo, money);
            // 年榜
            RedisUtil.incrScore(StrUtils.getKey(Const.RANKING_YEAR_PREFIX, "13", String.valueOf(now.getYear())), userNo, money);
            // 写入排行榜
            RedisUtil.incrScore(StrUtils.getKey(Const.RANKING_PREFIX, "14"), userNo, money);
            // 购买鱼翅
            buyCurrency(oId, whoGive, money);
        } catch (Exception e) {
            log.error("这可不能没了...", e);
            Fish.sendMsg("@" + RedisUtil.get(Const.ADMIN) + " . 老板, 我们的财阀..." + whoGive + "...大人的购买记录入库失败啦. 快来救命呀~");
        }
    }

    /**
     * 保存红包记录
     *
     * @param oId
     * @param userName
     * @param money
     * @param rpType
     */
    @Async("threadPool")
    public void recordRp(Long oId, String userName, Integer money, int rpType) {
        try {
            // 当前时间
            LocalDateTime now = LocalDateTime.now();
            // 鱼翅购买记录
            RpRecord record = new RpRecord();
            record.setOid(oId);
            Integer userNo_ = Fish.getUserNo(userName);
            record.setUserNo(userNo_);
            record.setMoney(money);
            record.setRpType(rpType);
            record.setCreateTime(now);
            record.setUpdateTime(now);
            // 保存记录
            rpRecordMapper.insert(record);
            // 字符串
            String userNo = userNo_.toString();
            // 日榜
            RedisUtil.incrScore(StrUtils.getKey(Const.RANKING_DAY_PREFIX, "25", DateUtil.format(DateUtil.ld2UDate(now.toLocalDate()), "yyyyMMdd")), userNo, money);
            // 周榜
            RedisUtil.incrScore(StrUtils.getKey(Const.RANKING_WEEK_PREFIX, "26", String.valueOf(now.getYear()), String.valueOf(now.toLocalDate().get(WeekFields.ISO.weekOfWeekBasedYear()))), userNo, money);
            // 月榜
            RedisUtil.incrScore(StrUtils.getKey(Const.RANKING_MONTH_PREFIX, "27", String.valueOf(now.getYear()), String.valueOf(now.getMonth().getValue())), userNo, money);
            // 年榜
            RedisUtil.incrScore(StrUtils.getKey(Const.RANKING_YEAR_PREFIX, "28", String.valueOf(now.getYear())), userNo, money);
            // 写入排行榜
            RedisUtil.incrScore(StrUtils.getKey(Const.RANKING_PREFIX, "29"), userNo, money);
        } catch (Exception e) {
            log.error("这可不能没了...", e);
            Fish.sendMsg("@" + RedisUtil.get(Const.ADMIN) + " . 老板, 散财童子大人的记录入库失败啦. 快来救命呀~");
        }
    }

    /**
     * 保存红包打开记录
     *
     * @param oId
     * @param whoGot
     * @param whoGive
     */
    @Async("threadPool")
    public void recordRpOpenLog(String oId, String whoGot, String whoGive) {
        try {
            // 当前时间
            LocalDateTime now = LocalDateTime.now();
            // 红包打开记录
            RpOpenLog openLog = new RpOpenLog();
            openLog.setOid(Long.parseLong(oId));
            Integer gotUno = Fish.getUserNo(whoGot);
            openLog.setUserNo(gotUno);
            // 获取红包记录
            QueryWrapper<RpRecord> cond = new QueryWrapper<>();
            cond.eq("oid", openLog.getOid());
            // 获取记录
            RpRecord record = rpRecordMapper.selectOne(cond);
            if (Objects.isNull(record)) {
                log.info("入库前的记录, 不管啦");
                return;
            }
            openLog.setOpened(0);
            openLog.setCreateTime(now);
            openLog.setUpdateTime(now);
            // 保存记录
            rpOpenLogMapper.insert(openLog);
        } catch (Exception e) {
            log.error("这可不能没了...", e);
            Fish.sendMsg("@" + RedisUtil.get(Const.ADMIN) + " . 老板, 红包打开记录入库失败啦. 快来救命呀~");
        }
    }

    /**
     * 获胜者
     *
     * @param uno
     * @param money
     */
    private void winner(String uno, int money, LocalDateTime now) {
        // 日榜
        RedisUtil.incrScore(StrUtils.getKey(Const.RANKING_DAY_PREFIX, "30", DateUtil.format(DateUtil.ld2UDate(now.toLocalDate()), "yyyyMMdd")), uno, money);
        // 周榜
        RedisUtil.incrScore(StrUtils.getKey(Const.RANKING_WEEK_PREFIX, "31", String.valueOf(now.getYear()), String.valueOf(now.toLocalDate().get(WeekFields.ISO.weekOfWeekBasedYear()))), uno, money);
        // 月榜
        RedisUtil.incrScore(StrUtils.getKey(Const.RANKING_MONTH_PREFIX, "32", String.valueOf(now.getYear()), String.valueOf(now.getMonth().getValue())), uno, money);
        // 年榜
        RedisUtil.incrScore(StrUtils.getKey(Const.RANKING_YEAR_PREFIX, "33", String.valueOf(now.getYear())), uno, money);
        // 写入排行榜
        RedisUtil.incrScore(StrUtils.getKey(Const.RANKING_PREFIX, "34"), uno, money);
    }

    /**
     * 失败者
     *
     * @param uno
     * @param money
     */
    private void loser(String uno, int money, LocalDateTime now) {
        // 日榜
        RedisUtil.incrScore(StrUtils.getKey(Const.RANKING_DAY_PREFIX, "35", DateUtil.format(DateUtil.ld2UDate(now.toLocalDate()), "yyyyMMdd")), uno, money);
        // 周榜
        RedisUtil.incrScore(StrUtils.getKey(Const.RANKING_WEEK_PREFIX, "36", String.valueOf(now.getYear()), String.valueOf(now.toLocalDate().get(WeekFields.ISO.weekOfWeekBasedYear()))), uno, money);
        // 月榜
        RedisUtil.incrScore(StrUtils.getKey(Const.RANKING_MONTH_PREFIX, "37", String.valueOf(now.getYear()), String.valueOf(now.getMonth().getValue())), uno, money);
        // 年榜
        RedisUtil.incrScore(StrUtils.getKey(Const.RANKING_YEAR_PREFIX, "38", String.valueOf(now.getYear())), uno, money);
        // 写入排行榜
        RedisUtil.incrScore(StrUtils.getKey(Const.RANKING_PREFIX, "39"), uno, money);
    }


    /**
     * 购买鱼翅
     *
     * @param oId
     * @param userName
     * @param money
     */
    public void buyCurrency(Long oId, String userName, Integer money) {
        log.info("{} 在消息 {} 支付积分, 兑换鱼翅...", userName, oId);
        // 单价 32 基础价格
        Integer rate = 32;
        // 欢乐时光
        boolean happy = false;
        if (StringUtils.isNotBlank(RedisUtil.get(Const.CURRENCY_HAPPY_TIME))) {
            rate = new SecureRandom().nextInt(64) + 1;
            Fish.sendMsg("尊敬的渔民大人 @" + userName + " " + CrLevel.getCrLvName(userName) + " " + " . 您参与欢乐时光本次费率为 ..." + rate + "积分/个");
            switch (rate) {
                case 1:
                    // 补偿
                    CurrencyService.sendCurrency(userName, 6, "欢乐时光极值奖励---1积分/段");
                    break;
                case 32:
                    // 补偿
                    CurrencyService.sendCurrency(userName, 1, "欢乐时光极值奖励---平价");
                    break;
                case 64:
                    // 补偿
                    CurrencyService.sendCurrency(userName, 9, "欢乐时光极值奖励---64积分/段");
                    break;
                default:
                    // 别的什么都不做
                    break;
            }
            happy = true;
        }
        // 鱼翅购买记录
        Currency currency = Currency.builder().oid(oId).user(userName).money(money).rate(rate).happy(happy).build();
        // 写入缓存
        RedisUtil.set(oId.toString(), JSON.toJSONString(currency));
    }

    /**
     * 计算用户活跃度 每次间隔60秒发言, 活跃度增加 0.6
     * 无法感知其余操作, 目前活跃度只能是一个大概范围
     * @param userName
     */
    public void incActivity(String userName) {
        // 当前日期
        LocalDate now = LocalDate.now();
        // 计算宵禁
        LocalDateTime time = LocalDateTime.now();
        if (!DateUtil.isBetween(time, LocalDateTime.of(now, Const.start), LocalDateTime.of(now, Const.end))) {
            // 宵禁时间 不计算
            return;
        }
        // 时间间隔
        String cdKey = Const.USER_ACTIVITY_LIMIT + userName;
        // 当前活跃度
        String userActivity = Const.USER_ACTIVITY + userName;
        // 查询能否获得间隔锁  可以获得就计算, 否则啥也不干
        if (StringUtils.isBlank(RedisUtil.get(cdKey))) {
            // key 时间差
            Integer diff = Long.valueOf(Duration.between(time, now.plusDays(1).atStartOfDay()).getSeconds()).intValue();
            // 当前活跃
            String cs = RedisUtil.get(userActivity);
            // 当前消耗一次. 下次继续 如果有 key. 就叠加
            if (StringUtils.isBlank(cs)) {
                RedisUtil.set(userActivity, "0.6", diff.longValue());
            } else {
                // 否则增加 1.67
                BigDecimal add = new BigDecimal(cs).add(new BigDecimal("0.6"));
                if (add.longValue() > 100) {
                    add = new BigDecimal("100");
                }
                RedisUtil.set(userActivity, add.toString(), diff);
            }
            // 30秒 cd
            RedisUtil.set(cdKey, "1", 60);
        }
    }

    private static void incLiveness(LocalDate now, LocalDateTime time, String userActivity) {
        // key 时间差
        Integer diff = Long.valueOf(Duration.between(time, now.plusDays(1).atStartOfDay()).getSeconds()).intValue();
        // 当前活跃
        String cs = RedisUtil.get(userActivity);
        // 当前消耗一次. 下次继续 如果有 key. 就叠加
        if (StringUtils.isBlank(cs)) {
            RedisUtil.set(userActivity, "2", diff.longValue());
        } else {
            // 否则增加 2
            BigDecimal add = new BigDecimal(cs).add(new BigDecimal("2"));
            if (add.longValue() > 100) {
                add = new BigDecimal("100");
            }
            RedisUtil.set(userActivity, add.toString(), diff);
        }
    }

    /**
     * 购买鱼翅
     *
     * @param oId
     * @param userName
     * @param money
     * @param fRate
     * @param isHappy
     */
    public void buyCurrency(Long oId, String userName, Integer money, int fRate, boolean isHappy) {
        // 打开红包
        if (StringUtils.isBlank(Fish.openRedPacket(oId, false))) {
            // 如果失败了, 就提示
            Fish.send2User(userName, "尊敬的财阀大人 . 我打不开你的红包啦. 快截图去找我老板 ...");
            return;
        }
        // 次数
        int count = money / fRate;
        // 涨价超过 32 了?
        if (count < 1) {
            // 积分不够, 送一次. 仅此一次
            String freeKey = Const.CURRENCY_FREE_PREFIX + userName;
            if (StringUtils.isBlank(RedisUtil.get(freeKey))) {
                // 发送设置
                Fish.send2User(userName, "尊敬的渔民大人 . 您的鱼翅兑换失败(~~买不到~~)~ 赠送一次(次数已增加), 仅此一次~ 下不为例!, 兑换编号:" + oId);
                // 当前消耗一次. 下次继续 如果有 key. 就叠加
                CurrencyService.sendCurrency(userName, count, "积分兑换鱼翅失败(~~买不到, 赠送一次(次数已增加), 仅此一次下不为例!~~), 兑换编号 : " + oId);
                RedisUtil.set(freeKey, DateUtil.nowStr());
            } else {
                // 发送设置
                Fish.send2User(userName, "尊敬的渔民大人 . 您的鱼翅兑换失败(~~买不到~~) 本次您可以当做赞助, 或联系售后退费(手续费 50%[向上取整])~, 兑换编号:" + oId);
            }
        } else {
            // 发送设置
            CurrencyService.sendCurrency(userName, count, "积分兑换鱼翅, 兑换编号 : " + oId);
        }
    }

    /**
     * 获取用户地理位置
     *
     * @param userCity
     * @return
     */
    public DistrictCn getDistrict(String userCity) {
        // 查询对象
        QueryWrapper<DistrictCn> cond = new QueryWrapper<>();
        cond.likeRight("district", userCity);
        // 模糊查询结果
        List<DistrictCn> cns = districtCnMapper.selectList(cond);
        if (CollectionUtils.isNotEmpty(cns)) {
            return cns.get(0);
        }
        return null;
    }

    /**
     * 匹配对象
     *
     * @param md
     * @return
     */
    private static Integer switchType(String md) {
        // 内容类型 0 红包 1 文字消息 2 图片消息 3 小冰 4 点歌 5 朗读 6 凌 7 弹幕
        if (RegularUtil.isMdImg(md)) {
            return 2;
        }
        if (md.startsWith("小冰")) {
            return 3;
        }
        if (md.startsWith("点歌")) {
            return 4;
        }
        if (md.startsWith("朗诵") || md.startsWith("TTS") || md.startsWith("tts")) {
            return 5;
        }
        if (md.startsWith("凌")) {
            return 6;
        }
        if (md.equals("弹幕-20230426163907")) {
            return 7;
        }
        // 普通文字消息
        return 1;
    }

    /**
     * 获取鱼翅购买人
     *
     * @return
     */
    public List<CurrencyLog> getBuyer() {
        QueryWrapper<CurrencyLog> cond = new QueryWrapper<>();
        cond.eq("state", 0);
        return currencyMapper.selectList(cond);
    }

    /**
     * 修改购买者的购买状态
     *
     * @param buyer
     */
    public void updateBuyer(List<CurrencyLog> buyer) {
        // 没数据就啥也不干
        if (CollUtil.isEmpty(buyer)) {
            return;
        }
        // 遍历
        for (CurrencyLog mc : buyer) {
            mc.setState(1);
            mc.setUpdateTime(LocalDateTime.now());
            currencyMapper.updateById(mc);
        }
    }

    /**
     * 查询最近发言记录
     *
     * @param start
     * @param end
     * @return
     */
    public List<MsgRecord> hasEntitlement(LocalDateTime start, LocalDateTime end) {
        QueryWrapper<MsgRecord> cond = new QueryWrapper<>();
        cond.notIn("user_no", Const.ROBOT_LIST);
        cond.between("create_time", DateUtil.formatDay(start), DateUtil.formatDay(end));
        return msgRecordMapper.selectList(cond);
    }

    /**
     * 处理红包记录
     */
    public void dealRpOpenLog() {
        QueryWrapper<RpOpenLog> roCond = new QueryWrapper<>();
        // 所有待处理的
        roCond.eq("opened", 0);
        // 获取所有对象
        List<RpOpenLog> rpOpenLogs = rpOpenLogMapper.selectList(roCond);
        if (CollUtil.isEmpty(rpOpenLogs)) {
            log.info("没有需要处理的打开对象");
            return;
        }
        // 按照oid 分组
        Map<Long, List<RpOpenLog>> oids = rpOpenLogs.stream().collect(groupingBy(RpOpenLog::getOid));
        // 查出所有原始记录
        QueryWrapper<RpRecord> recordCond = new QueryWrapper<>();
        recordCond.in("oid", oids.keySet());
        // 所有原始记录
        List<RpRecord> rpRecords = rpRecordMapper.selectList(recordCond);
        if (CollUtil.isEmpty(rpRecords)) {
            log.info("发生异常 没有红包原始数据");
            Fish.send2User(RedisUtil.get(Const.ADMIN), "红包记录异常, 没有原始记录信息..." + JSON.toJSONString(oids.keySet()));
            return;
        }
        // 分组
        Map<Long, RpRecord> rMap = rpRecords.stream().collect(Collectors.toMap(RpRecord::getOid, Function.identity()));
        // 遍历oid 分别处理 一次五个吧
        int count = 0;
        for (Long oid : oids.keySet()) {
            if (count > 5) {
                return;
            }
            dealRpOpenLog(oid, oids.get(oid), rMap.get(oid));
            // +1
            count++;
        }
    }

    /**
     * 处理红包打开的明细记录
     *
     * @param oid
     * @param rpOpenLogs
     * @param record
     */
    private void dealRpOpenLog(Long oid, List<RpOpenLog> rpOpenLogs, RpRecord record) {
        if (Objects.isNull(record)) {
            Fish.send2User(RedisUtil.get(Const.ADMIN), "红包记录异常, 没有原始记录信息...A..." + oid);
            return;
        }
        // 发送人
        Integer giveUno = record.getUserNo();
        // 打开红包
        String fResp;
        // 猜拳需要参数
        if (record.getRpType() == 5) {
            fResp = Fish.openRedPacket(oid, true);
        } else {
            fResp = Fish.openRedPacket(oid, false);
        }
        log.info("红包打开响应=>{}", fResp);
        // 获取列表
        JSONArray who;
        try {
            // 获取打开信息
            JSONObject rp = JSON.parseObject(fResp);
            // 获取列表
            who = rp.getJSONArray("who");
        } catch (Exception e) {
            e.printStackTrace();
            Fish.send2User(RedisUtil.get(Const.ADMIN), "红包记录异常, 没有原始记录信息...B..." + oid);
            return;
        }
        // 当前时间
        LocalDateTime now = LocalDateTime.now();
        // 用户打开记录
        Map<Integer, RpOpenLog> userRpOpen = rpOpenLogs.stream().collect(Collectors.toMap(RpOpenLog::getUserNo, Function.identity()));
        // 遍历
        for (Object o : who) {
            // 积分明细
            int point;
            // 获取人
            JSONObject w = (JSONObject) o;
            // 获取用户信息
            User user = getUser(w.getString("userName"));
            // 用户打开记录
            RpOpenLog openLog = userRpOpen.get(user.getUserNo());
            // 不为空则更新
            if (Objects.nonNull(openLog)) {
                point = w.getInteger("userMoney");
                openLog.setMoney(point);
                openLog.setOpened(1);
                openLog.setUpdateTime(now);
                // 更新信息
                rpOpenLogMapper.updateById(openLog);
                // 存在 且是猜拳 且不是不是精灵发的
                if (record.getRpType() == 5 && giveUno != 521) {
                    // 字符串
                    String gotNo = user.getUserNo().toString(), giveNo = giveUno.toString();
                    // got 赢了
                    if (point > 0) {
                        winner(gotNo, point, now);
                        loser(giveNo, point, now);
                    }
                    // give 赢了
                    if (point < 0) {
                        winner(giveNo, Math.abs(point), now);
                        loser(gotNo, Math.abs(point), now);
                    }
                    // 平手不算
                }
            }
        }
    }

    /**
     * 购买鱼翅
     */
    @Data
    @Builder
    public static class Currency {

        /**
         * 红包消息对象 id
         */
        private Long oid;

        /**
         * 购买人
         */
        private String user;

        /**
         * 购买金额
         */
        private int money;

        /**
         * 单价
         */
        private int rate;

        /**
         * 是否是欢乐时光
         */
        private boolean happy;

    }

}
