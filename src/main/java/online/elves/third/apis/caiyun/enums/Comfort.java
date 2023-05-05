package online.elves.third.apis.caiyun.enums;

import lombok.Getter;

/**
 * 舒适度指数
 */
@Getter
public enum Comfort {
    C_0(0, "闷热"),
    C_1(1, "酷热"),
    C_2(2, "很热"),
    C_3(3, "热"),
    C_4(4, "温暖"),
    C_5(5, "舒适"),
    C_6(6, "凉爽"),
    C_7(7, "冷"),
    C_8(8, "很冷"),
    C_9(9, "寒冷"),
    C_10(10, " 极冷"),
    C_11(11, " 刺骨的冷"),
    C_12(12, " 湿冷"),
    C_13(13, " 干冷"),
    ;
    
    /**
     * 等级
     */
    public int level;
    
    /**
     * 描述
     */
    public String desc;
    
    /**
     * 构造函数
     * @param level
     * @param desc
     */
    Comfort(int level, String desc) {
        this.level = level;
        this.desc = desc;
    }
    
    /**
     * 判断舒适度
     * @param index
     * @return
     */
    public static String judge(int index) {
        switch (index){
            case 0:
                return "`闷热`难耐, 快躲到空调屋离去吧~";
            case 1:
                return "`酷热`天气, 尽量减少户外运动以防中暑哦~";
            case 2:
                return "`很热`, 如无必要请勿外出~";
            case 3:
                return "`热`, 但也不是不能出去";
            case 4:
                return "`温暖`, 喝杯咖啡休息一会儿吧☺️";
            case 5:
                return "`舒适`的很, 快去户外散散步吧~ ~~陪上你心爱的Ta~~";
            case 6:
                return "`凉爽`怡人, 适合健身呀! 摸鱼累了就去运动一下";
            case 7:
                return "`冷`, 🥶记得添衣服";
            case 8:
                return "`很冷`, 还是戴上帽子吧!";
            case 9:
                return "`寒冷`逼人, 能不出去就不出去哦~ 我们一起来摸鱼";
            case 10:
                return "`极冷`, 秋裤 + 线裤 + 绒裤 + 棉裤 +++ 尽情套啊~";
            case 11:
                return "`刺骨的冷`, 炕头不暖和么? 千万别出去啊!!!";
            case 12:
                return "`湿冷`, 抱个小太阳, 起码先把`湿`去掉, `冷`咱们另说";
            case 13:
                return "`干冷`, 加热的同时别忘了加湿哦, 不然没妹子你也流鼻血...";
            default:
                return "";
        }
    }
}
