package online.elves.mapper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户记录表
 */
@Data
@TableName("fish_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 昵称
     */
    private String userNick;

    /**
     * 用户编号
     */
    private Integer userNo;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
