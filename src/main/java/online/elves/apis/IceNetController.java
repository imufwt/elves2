package online.elves.apis;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import online.elves.apis.model.EResp;
import online.elves.config.Const;
import online.elves.service.FService;
import online.elves.utils.DateUtil;
import online.elves.utils.EncryptUtil;
import online.elves.utils.RedisUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Objects;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/ice/")
public class IceNetController {

    @Resource
    FService fService;

    @GetMapping("credit/get")
    public EResp<JSONObject> getCredit(@RequestParam("user") String user, @RequestParam("sign") String sign) {
        log.info("iceNet get user credit ...{}...{}", user, sign);
        // 校验密码
        if (sign.equals(EncryptUtil.MD5(user + RedisUtil.get(Const.TP_API_PREFIX + "ICE_NET") + DateUtil.formatDay(LocalDate.now()) + RedisUtil.get(Const.TP_API_PREFIX + "ICE_NET") + user))) {
            // 查询用户征信
            JSONObject job = fService.getUserCredit(user);
            if (Objects.isNull(job)) {
                return EResp.createByErrorMessage("查询异常, 请联系接口提供者...嘻嘻");
            } else {
                return EResp.createBySuccess(job);
            }
        }
        return EResp.createByErrorMessage("签名错误, 骚年~ 你要干什么?");
    }
}
