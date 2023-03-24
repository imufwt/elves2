package online.elves.third.apis.juhe;

import lombok.Data;

/**
 * 聚合数据 星座今日运势
 */
@Data
public class Today {
    /**
     * 星座名称
     */
    private String name;
    
    /**
     * 日期
     */
    private String datetime;
    
    /**
     * 日期
     */
    private String date;
    
    /**
     * 综合指数
     */
    private String all;
    
    /**
     * 幸运色
     */
    private String color;
    
    /**
     * 健康指数
     */
    private String health;
    
    /**
     * 爱情指数
     */
    private String love;
    
    /**
     * 财运指数
     */
    private String money;
    
    /**
     * 幸运数字
     */
    private String number;
    
    /**
     * 速配星座
     */
    private String QFriend;
    
    /**
     * 今日概述
     */
    private String summary;
    
    /**
     * 工作指数
     */
    private String work;
    
    /**
     * 返回码
     */
    private String error_code;
}
