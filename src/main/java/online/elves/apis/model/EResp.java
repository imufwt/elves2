package online.elves.apis.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.Serializable;

/**
 * 返回对象
 *
 * @param <T>
 */
@Data
public class EResp<T> implements Serializable {
    private Integer code;
    private T data;
    private String msg;

    public EResp() {
    }

    public EResp(T data) {
        this.code = 0;
        this.data = data;
        this.msg = "";
    }

    public EResp(Integer code, String message) {
        this.code = code;
        this.data = (T) new JSONObject();
        this.msg = message;
    }

    public EResp(T data, Integer code, String message) {
        this.code = code;
        this.msg = message;
        this.data = data;
    }

    public static <T> EResp<T> createBySuccess(T data) {
        return new EResp(data);
    }

    public static <T> EResp<T> createByErrorCodeMessage(Integer errorCode, String errorMessage) {
        return new EResp(errorCode, errorMessage);
    }

    public static <T> EResp<T> createByErrorMessage(String errorMessage) {
        return new EResp(10000, errorMessage);
    }
}
