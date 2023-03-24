package online.elves.enums;

import lombok.Getter;
import online.elves.config.Const;
import online.elves.third.fish.Fish;
import online.elves.utils.NumUtils;
import online.elves.utils.RedisUtil;
import online.elves.utils.StrUtils;

import java.util.Objects;

/**
 * 所谓语料?
 */
@Getter
public enum CrLevel {
    /**
     * 等级
     * x * x * (x << 5 + 32)
     */
    Lv_0(0, 0, 32),
    Lv_1(1, 32, 256),
    Lv_2(2, 256, 864),
    Lv_3(3, 864, 2048),
    Lv_4(4, 2048, 4000),
    Lv_5(5, 4000, 6912),
    Lv_6(6, 6912, 10976),
    Lv_7(7, 10976, 16384),
    Lv_8(8, 16384, 23328),
    Lv_9(9, 23328, 32000),
    Lv_10(10, 32000, 42592),
    Lv_11(11, 42592, 55296),
    Lv_12(12, 55296, 70304),
    Lv_13(13, 70304, 87808),
    Lv_14(14, 87808, 108000),
    Lv_15(15, 108000, 131072),
    Lv_16(16, 131072, 157216),
    Lv_17(17, 157216, 186624),
    Lv_18(18, 186624, 219488),
    Lv_19(19, 219488, Integer.MAX_VALUE),
    ;
    
    /**
     * 等级
     */
    public int lv;
    
    /**
     * 经验区间开始
     */
    public Integer start;
    
    /**
     * 经验区间结束
     */
    public Integer end;
    
    /**
     * 有参构造
     */
    CrLevel(int lv, Integer start, Integer end) {
        this.lv = lv;
        this.start = start;
        this.end = end;
    }
    
    /**
     * 获取用户聊天室等级
     * @param exp
     * @return
     */
    public static int getCrLv(Integer exp) {
        if (Objects.nonNull(exp)) {
            // 遍历等级
            for (CrLevel c : CrLevel.values()) {
                if (NumUtils.isBetween(exp, c.start, c.end)) {
                    return c.lv;
                }
            }
        }
        return 0;
    }
    
    /**
     * 获取用户聊天室等级称谓
     * @param userName
     * @return
     */
    public static String getCrLvName(String userName) {
        // 用户编码
        Integer userNo = Fish.getUserNo(userName);
        // 缓存key
        String key = StrUtils.getKey(Const.RANKING_PREFIX, "24");
        // 获取得分
        Double score = RedisUtil.getScore(key, userNo + "");
        // 不存在就赋值 0
        if (Objects.isNull(score)) {
            score = Double.valueOf("0");
        }
        return "[" + Const.CHAT_ROOM_LEVEL_NAME.get(getCrLv(score.intValue())) + "]";
    }
}
