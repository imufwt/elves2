package online.elves.mapper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 鱼翅购买记录
 */
@Data
@TableName("rp_open_log")
public class RpOpenLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 消息 ID 时间戳
     */
    private Long oid;

    /**
     * 发红包的
     */
    private Integer userNo;

    /**
     * 红包金额
     */
    private Integer money;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 修改时间
     */
    private LocalDateTime updateTime;
}
