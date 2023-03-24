package online.elves.mapper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 行政区经纬信息
 */
@Data
@TableName("district_cn")
public class DistrictCn implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 省
     */
    private String province;

    /**
     * 市地理编码
     */
    private String cityGeocode;

    /**
     * 市
     */
    private String city;

    /**
     * 区地理编码
     */
    private String districtGeocode;

    /**
     * 区
     */
    private String district;

    /**
     * 区行政编码
     */
    private String districtCode;

    /**
     * 经度
     */
    private String lon;

    /**
     * 维度
     */
    private String lat;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
