package online.elves.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import online.elves.mapper.entity.RpOpenLog;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 红包打开记录 Mapper 接口
 */
public interface RpOpenLogMapper extends BaseMapper<RpOpenLog> {
    /**
     * 昨日赌狗
     *
     * @param userNo
     * @param start
     * @param end
     * @return
     */
    @Select("SELECT ro.*,rr.rp_type FROM rp_open_log ro LEFT JOIN rp_record rr ON ro.oid=rr.oid WHERE rr.rp_type=5 AND ro.user_no=#{userNo} AND ro.opened=1 AND ro.create_time BETWEEN #{start} AND #{end}")
    List<RpOpenLog> selectDog(@Param("userNo") Integer userNo,@Param("start") String start,@Param("end") String end);

    /**
     * 昨日非赌狗
     *
     * @param userNo
     * @param start
     * @param end
     * @return
     */
    @Select("SELECT ro.*,rr.rp_type FROM rp_open_log ro LEFT JOIN rp_record rr ON ro.oid=rr.oid WHERE rr.rp_type!=5 AND ro.user_no=#{userNo} AND ro.opened=1 AND ro.create_time BETWEEN #{start} AND #{end}")
    List<RpOpenLog> selectNotDog(@Param("userNo") Integer userNo,@Param("start") String start,@Param("end") String end);
}
