package online.elves.mapper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息记录表
 */
@Data
@TableName("msg_record")
public class MsgRecord implements Serializable {

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
     * 用户编号
     */
    private Integer userNo;

    /**
     * 聊天内容
     */
    private String content;

    /**
     * 内容类型 0 红包 1 文字消息 2 图片消息 3 小冰 4 点歌 5 朗读
     */
    private Integer type;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
